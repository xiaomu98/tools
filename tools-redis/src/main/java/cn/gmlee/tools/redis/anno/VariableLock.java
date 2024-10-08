package cn.gmlee.tools.redis.anno;

import java.lang.annotation.*;

/**
 * 变量锁.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VariableLock {
    /**
     * 来源枚举.
     */
    enum Origin {
        /**
         * Head origin.
         */
        HEAD, // 请求头
        /**
         * Query origin.
         */
        QUERY, // URL
        /**
         * Form origin.
         */
        FORM, // 表单
        /**
         * Args origin.
         */
        ARGS, // 形参
        /**
         * Cookie origin.
         */
        COOKIE, // cookie
    }

    /**
     * 名称.
     *
     * @return 不允许空 string [ ]
     */
    String[] value();

    /**
     * 业务.
     *
     * @return 默认""不区分业务
     */
    String biz() default "";

    /**
     * 来源.
     *
     * @return empty表示全部 origin [ ]
     */
    Origin[] origin() default {};

    /**
     * 是否自旋.
     * <p>
     * 自旋排队的公平锁.
     * </p>
     *
     * @return the boolean
     */
    boolean spin() default false;

    /**
     * 是否加锁.
     *
     * @return the boolean
     */
    boolean lock() default true;

    /**
     * 是否检锁.
     *
     * @return the boolean
     */
    boolean check() default true;

    /**
     * 是否解锁.
     *
     * @return the boolean
     */
    boolean unlock() default true;

    /**
     * 超时时间.
     * <p>
     * 实际上每次接口完成后会自动释放锁, 必须大于0否则无法上锁
     * </p>
     *
     * @return 默认1分钟 long
     */
    long timeout() default 60 * 1000;

    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "处理中";
}
