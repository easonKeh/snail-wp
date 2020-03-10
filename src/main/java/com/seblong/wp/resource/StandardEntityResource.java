package com.seblong.wp.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@ApiModel("查询结果")
@JsonInclude(Include.NON_EMPTY)
public class StandardEntityResource<T> extends StandardRestResource {

	@ApiModelProperty(value = "查询的结果", name = "result", dataType = "List")
	private T result;

	public StandardEntityResource( T res ) {
		super( 200, "OK" );
		this.result = res;
	}

	public T getResult() {
		return result;
	}

	public void setResult( T result ) {
		this.result = result;
	}
}
