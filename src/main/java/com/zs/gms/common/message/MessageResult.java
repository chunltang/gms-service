package com.zs.gms.common.message;

/**
 * 0处理成功，1处理失败，2已发送还没返回结果，10过期响应,11响应请求后的成功状态，12响应请求后的失败状态,13没有处理结果字段
 */
public enum MessageResult {

    SUCCESS("0"),
    FAIL("1"),
    SENDING("2"),
    RESPONSE_EXPIRE("10"),
    AFTER_SUCCESS("11"),
    AFTER_FAIL("12"),
    NO_STATUS("13");

    private String value;

    MessageResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}