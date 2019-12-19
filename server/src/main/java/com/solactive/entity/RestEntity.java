package com.solactive.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestEntity<T> {

    private T success;
    private List<String> errors;

    public T getSuccess() {
        return success;
    }

    public void setSuccess(T success) {
        this.success = success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public RestEntity<T> setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

}