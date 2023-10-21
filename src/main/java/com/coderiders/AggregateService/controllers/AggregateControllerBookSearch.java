package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/search")
    public Mono<List<UserLibraryWithBookDetails>> getBookSearchMockData(@RequestParam String term) {
        log.info("/book/search GET ENDPOINT HIT: " + term);
        return bookSearchService.getBasicSearch(term);
    }
}
