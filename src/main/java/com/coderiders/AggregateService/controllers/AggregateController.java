package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.googleBooks.GoogleBook;
import com.coderiders.AggregateService.services.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@RestController
@RequiredArgsConstructor
public class AggregateController {

    private final BookSearchService bookSearchService;

    @GetMapping("/")
    public String myRoute() {
        return "Successful AggregateController";
    }

    @GetMapping("/exceptionTest")
    public String myRouteException() {
        throw new AggregateException("My Exception Test");
//        return "Successful AggregateController";
    }

    @GetMapping("/book/search")
    public Mono<List<GoogleBook>> getBookSearchMockData() {
        return bookSearchService.getGoogleBooksMockData();
//        return "Successful AggregateController";
    }
}
