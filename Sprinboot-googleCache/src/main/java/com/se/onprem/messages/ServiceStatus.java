package com.se.onprem.messages;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "code", "success", "message" })
public class ServiceStatus {
    private int code;
    private String message;
    private Boolean success;

    public ServiceStatus(OperationMessages message, Boolean success) {
        super();
        this.message = message.getMsg();
        this.code = message.getCode();
        this.success = success;
    }

    public ServiceStatus(String message, Boolean success) {
        super();
        this.message = message;
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
