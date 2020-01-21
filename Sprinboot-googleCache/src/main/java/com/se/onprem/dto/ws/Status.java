package com.se.onprem.dto.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.onprem.messages.OperationMessages;


public class Status {

    @JsonProperty("Code")
    private Integer code;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Success")
    private Boolean success;

    public Status()
	{
	}
    
    public Status(OperationMessages message, Boolean success) {
        this.message = message.getMsg();
        this.code = message.getCode();
        this.success = success;
    }

    public Status(String message, Boolean success) {
        super();
        this.message = message;
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
