package com.sdk.server.ApiResult;

import com.company.ApiResult.ResultSupport;

public class APIResult <T> extends ResultSupport {
    protected T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 接口调用失败,有错误字符串码和描述,有返回对象
     * @param code
     * @param message
     * @param data
     * @param <U>
     * @return
     */
    public static <U> com.company.ApiResult.APIResult<U> newFailResult(int code, String message, U data) {
        com.company.ApiResult.APIResult<U> apiResult = new com.company.ApiResult.APIResult<U>();
        apiResult.setStatusCode(code);
        apiResult.setMessage(message);
        apiResult.setData(data);
        return apiResult;
    }

    /**
     * 接口调用失败,有错误字符串码和描述,没有返回对象
     * @param code
     * @param message
     * @param <U>
     * @return
     */
    public static <U> com.company.ApiResult.APIResult<U> newFailResult(int code, String message) {
        com.company.ApiResult.APIResult<U> apiResult = new com.company.ApiResult.APIResult<U>();
        apiResult.setStatusCode(code);
        apiResult.setMessage(message);
        return apiResult;
    }

    public static <U> com.company.ApiResult.APIResult<U> newSuccessResult(U data){
        com.company.ApiResult.APIResult<U> apiResult = new com.company.ApiResult.APIResult<U>();
        apiResult.setData(data);
        return apiResult;
    }
}
