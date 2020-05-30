package com.asl.pms.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionControllerA {

    @ExceptionHandler(value = NotFoundException.class)
    public String exception(NotFoundException e, Model model){
        model.addAttribute("exceptionMessage", e.getMessage());
        return "404";
    }

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e, Model model){
        log.debug(e.getMessage());
        e.printStackTrace();
        model.addAttribute("exceptionMessage", e.getMessage());
        return "error";
    }
}