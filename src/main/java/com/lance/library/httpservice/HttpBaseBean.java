package com.lance.library.httpservice;

import java.util.HashMap;

/**
 * Created by Tjcx on 2017/9/18.
 */

public class HttpBaseBean {

    private int errorCode;

    private String message;

    private String mobileToken;

    private boolean firstStepSuccess = true;

    private HashMap<String, Object> data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFirstStepSuccess() {
        return firstStepSuccess;
    }

    public void setFirstStepSuccess(boolean firstStepSuccess) {
        this.firstStepSuccess = firstStepSuccess;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public String getMobileToken() {
        return mobileToken;
    }

    public void setMobileToken(String mobileToken) {
        this.mobileToken = mobileToken;
    }

    @Override
    public String toString() {
        return "HttpBaseBean{" +
                "errorCode=" + errorCode +
                ", message='" + message + '\'' +
                ", mobileToken='" + mobileToken + '\'' +
                ", firstStepSuccess=" + firstStepSuccess +
                ", data=" + data +
                '}';
    }
}
