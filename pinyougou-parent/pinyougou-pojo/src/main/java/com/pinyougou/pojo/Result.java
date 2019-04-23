package com.pinyougou.pojo;

import java.io.Serializable;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/2/25 09:26
 */
public class Result implements Serializable {
    private boolean success;
    private String message;

    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
