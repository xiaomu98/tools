package cn.gmlee.tools.base.mod;

import cn.gmlee.tools.base.enums.XCode;
import cn.gmlee.tools.base.ex.SkillException;
import cn.gmlee.tools.base.util.ExceptionUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Json响应对象
 * <p>
 * Http状态码
 *
 * @param <T> 响应对象泛型
 * @author Jas °
 */
@Data
public class JsonResult<T> implements Serializable {
    /**
     * 操作失败.
     */
    public static final JsonResult FAIL = new JsonResult(XCode.FAIL);
    /**
     * 操作成功.
     */
    public static final JsonResult OK = new JsonResult(XCode.OK);


    /**
     * 响应码: 系统返回的响应码值; 必填
     */
    private Integer code;
    /**
     * 提示语: 系统返回的提示信息; 必填; 一般展示
     */
    private String msg;
    /**
     * 自述文: 系统返回的详细说明; 非必填; 一般不展示
     */
    private String desc;
    /**
     * 数据值: 系统返回的数据对象; 非必填
     */
    private T data;
    /**
     * 响应时: 系统处理完成的时间戳; 必填
     */
    private Long responseTime = System.currentTimeMillis();

    /**
     * Instantiates a new Json result.
     */
    public JsonResult() {
        this.code = OK.code;
        this.msg = OK.msg;
        this.desc = null;
        this.data = null;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param throwable the throwable
     */
    public JsonResult(Throwable throwable) {
        this.code = FAIL.code;
        this.msg = FAIL.msg;
        this.desc = ExceptionUtil.getOriginMsg(throwable);
        this.data = null;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param se the se
     */
    public JsonResult(SkillException se) {
        this.code = se.getCode();
        this.msg = se.getMessage();
        this.desc = ExceptionUtil.getOriginMsg(se);
        this.data = null;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param code the code
     * @param msg  the msg
     * @param data the data
     */
    public JsonResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.desc = null;
        this.data = data;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param code the code
     * @param msg  the msg
     */
    public JsonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.desc = null;
        this.data = null;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param xCode the x code
     */
    public JsonResult(XCode xCode) {
        this.code = xCode.code;
        this.msg = xCode.msg;
        this.desc = null;
        this.data = null;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param xCode the x code
     * @param desc  the desc
     */
    public JsonResult(XCode xCode, String desc) {
        this.code = xCode.code;
        this.msg = xCode.msg;
        this.desc = desc;
        this.data = null;
    }

    /**
     * Instantiates a new Json result.
     *
     * @param xCode the x code
     * @param data  the data
     */
    public JsonResult(XCode xCode, T data) {
        this(xCode.code, xCode.msg, data);
    }

    /**
     * Instantiates a new Json result.
     *
     * @param xCode     the x code
     * @param throwable the throwable
     */
    public JsonResult(XCode xCode, Throwable throwable) {
        this.code = xCode.code;
        this.msg = xCode.msg;
        this.desc = ExceptionUtil.getOriginMsg(throwable);
        this.data = null;
    }

    /**
     * Newly json result.
     *
     * @param throwable the throwable
     * @return the json result
     */
    public JsonResult newly(Throwable throwable) {
        return new JsonResult(throwable);
    }

    /**
     * Newly json result.
     *
     * @param xCode     the x code
     * @param throwable the throwable
     * @return the json result
     */
    public JsonResult newly(XCode xCode, Throwable throwable) {
        return new JsonResult(xCode, throwable);
    }

    /**
     * Newly json result.
     *
     * @param se the se
     * @return the json result
     */
    public JsonResult newly(SkillException se) {
        return new JsonResult(se);
    }

    /**
     * Newly json result.
     *
     * @param msg the msg
     * @return the json result
     */
    public JsonResult newly(String msg) {
        return new JsonResult(this.code, msg, this.data);
    }

    /**
     * Newly json result.
     *
     * @param xCode the x code
     * @param desc  the desc
     * @return the json result
     */
    public JsonResult newly(XCode xCode, String desc) {
        return new JsonResult(xCode, desc);
    }

    /**
     * Newly json result.
     *
     * @param data the data
     * @return the json result
     */
    public JsonResult newly(T data) {
        return new JsonResult(this.code, this.msg, data);
    }

    /**
     * Newly json result.
     *
     * @param code the code
     * @param msg  the msg
     * @return the json result
     */
    public JsonResult newly(Integer code, String msg) {
        return new JsonResult(code, msg, data);
    }

    /**
     * Newly json result.
     *
     * @param data the data
     * @param msg  the msg
     * @return the json result
     */
    public JsonResult newly(T data, String msg) {
        return new JsonResult(code, msg, data);
    }
}
