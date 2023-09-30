package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class AggregateControllerBookSearch {


    private final BookSearchService bookSearchService;

    @Value("${flags.booksearch.mockbooks:false}")
    private boolean mockBooks;

    @GetMapping("/search")
    public Mono<List<GoogleBook>> getBookSearchMockData(@RequestParam String term) {
        log.info("/book/search GET ENDPOINT HIT: " + term);
        return mockBooks || term.isEmpty()
                ? bookSearchService.getGoogleBooksMockData()
                : bookSearchService.getBasicSearch(term);
    }
}
