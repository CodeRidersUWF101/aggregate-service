package com.coderiders.AggregateService.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class AggregateControllerRecommendation {

    @GetMapping("/")
    public String myRoute() {
        log.info("Recommendation Base Route Hit");
        return "Successful Recommendation AggregateController";
    }
}
