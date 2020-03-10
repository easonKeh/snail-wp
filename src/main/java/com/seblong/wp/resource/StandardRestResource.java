package com.seblong.wp.resource;

import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class StandardRestResource {

	@ApiModelProperty(value = "状态码", name = "status", dataType = "Integer", example = "200")
	protected int status;
	@ApiModelProperty(value = "结果说明", name = "message", dataType = "String", example = "OK")
	protected String message;

	public StandardRestResource( int status, String message ) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus( int status ) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

	public static StandardRestResource fail(String msg) {
        return new StandardRestResource(400, msg);
    }
}
