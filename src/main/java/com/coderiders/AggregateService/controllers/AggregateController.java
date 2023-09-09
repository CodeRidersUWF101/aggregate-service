package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.exceptions.AggregateException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequiredArgsConstructor
public class AggregateController {

    @GetMapping("/")
    public String myRoute() {
        return "Successful AggregateController";
    }

    @GetMapping("/exceptionTest")
    public String myRouteException() {
        throw new AggregateException("My Exception Test");
//        return "Successful AggregateController";
    }
}
