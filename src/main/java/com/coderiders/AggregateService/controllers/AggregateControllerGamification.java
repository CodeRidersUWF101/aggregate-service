package com.coderiders.AggregateService.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/gamification")
@RequiredArgsConstructor
public class AggregateControllerGamification {
    @GetMapping("/")
    public String myRoute() {
        log.info("Gamification Base Route Hit");
        return "Successful Gamification AggregateController";
    }
}
