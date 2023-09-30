package com.coderiders.AggregateService.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RefreshScope
@RestController
@RequiredArgsConstructor
public class AggregateController {
    @GetMapping("/")
    public String myRoute() {
        log.info("Base Route Hit");
        return "Successful AggregateController";
    }
}
