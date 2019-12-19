package com.solactive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.solactive.entity.RestEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class TradeStatsExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TradeStatsValidationException.class)
    public RestEntity TradeStatsValidationException(TradeStatsValidationException ex){
    	RestEntity restEntity = new RestEntity();
        restEntity.setErrors(Collections.singletonList(ex.getLocalizedMessage()));
        return restEntity;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TickNotFoundException.class)
    public RestEntity TickNotFoundException(TickNotFoundException ex){
        RestEntity restEntity = new RestEntity();
        restEntity.setErrors(Collections.singletonList(ex.getLocalizedMessage()));
        return restEntity;
    }

}