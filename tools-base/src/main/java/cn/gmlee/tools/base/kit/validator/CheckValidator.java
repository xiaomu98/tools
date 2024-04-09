package cn.gmlee.tools.base.kit.validator;

import cn.gmlee.tools.base.anno.Check;
import cn.gmlee.tools.base.anno.El;
import cn.gmlee.tools.base.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 枚举校验器.
 *
 * @author Jas
 */
@Slf4j
public class CheckValidator implements ConstraintValidator<Check, Object> {
    private Check check;

    @Override
    public void initialize(Check check) {
        this.check = check;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Field[] fields = value.getClass().getDeclaredFields();
        Map<String, Object> valuesMap = ClassUtil.generateMap(value);
        // 支持类修饰 && 字段修饰
        return checkClass(valuesMap) && checkFields(valuesMap, fields);
    }

    private boolean eval(Map<String, Object> valuesMap, El el) {
        if (BoolUtil.isNull(el)) {
            return true; // 表达式是空不检查
        }
        String[] conditions = el.conditions();
        boolean condition = execute(valuesMap, conditions);
        if (!condition) { // 条件不满足不检查表达式
            return true;
        }
        String[] expressions = el.value();
        return execute(valuesMap, expressions);
    }

    private boolean execute(Map<String, Object> valuesMap, String... expressions) {
        if (BoolUtil.isEmpty(expressions)) {
            return true;
        }
        for (String condition : expressions) {
            // 处理空符
            CollectionUtil.valReplace(valuesMap, (k, v) ->
                    // 因为script会忽略空白符; 所以需要将空内容替换成带引号的空内容
                    v == null || "".equals(v)  || RegexUtil.match(v.toString(), String.format("[\\s]{%s,}", v.toString().length()))? "\"\"" : v.toString()
            );
            // 参数替换
            condition = PlaceholderHelper.replace(condition, valuesMap);
            // 脚本执行
            Object ok = ScriptUtil.execute(condition);
            log.info("condition:{}:{}", ok, condition);
            // 执行结果
            if (ok instanceof Boolean && !(Boolean) ok) {
                return false;
            }
        }
        return true;
    }

    private boolean checkFields(Map<String, Object> valuesMap, Field... fields) {
        for (Field field : fields) {
            El el = field.getAnnotation(El.class);
            if (!eval(valuesMap, el)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkClass(Map<String, Object> valuesMap) {
        El[] els = check.value();
        for (El el : els){
            if (!eval(valuesMap, el)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 依赖于Spring的帮助类,请确保依赖包存在
     */
    @SuppressWarnings("all")
    public static class PlaceholderHelper {

        private static final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

        /**
         * 占位符替换工具.
         *
         * @param content 内容
         * @param map     注意: 非String类型的value不会被替换
         * @return the string
         */
        public static String replace(String content, Map map) {
            if (BoolUtil.isEmpty(content)) {
                return content;
            }
            Properties properties = new Properties();
            if (BoolUtil.notEmpty(map)) {
                Set keys = map.keySet();
                for (Object key : keys) {
                    Object val = map.get(key);
                    if (val == null) {
                        map.put(key, "");
                    }
                }
                properties.putAll(map);
            }
            return helper.replacePlaceholders(content, properties);
        }

        /**
         * Handle string.
         *
         * @param content the content
         * @param key     the key
         * @param val     the val
         * @return the string
         */
        public static String replace(String content, String key, Object val) {
            if (BoolUtil.isEmpty(content)) {
                return content;
            }
            Properties properties = new Properties();
            properties.put(key, val != null ? val.toString() : "");
            return helper.replacePlaceholders(content, properties);
        }
    }
}
