package com.coderiders.AggregateService.controllers;



import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@RestController
@RequiredArgsConstructor
public class AggregateController {

    private final BookSearchService bookSearchService;
    private final UserService userService;

    @Value("${flags.booksearch.mockbooks:false}")
    private boolean mockBooks;

    @GetMapping("/")
    public String myRoute() {
        return "Successful AggregateController";
    }

    @GetMapping("/book/search")
    public Mono<List<GoogleBook>> getBookSearchMockData(@RequestParam String term) {
        System.out.println("/book/search ENDPOINT HIT: "  + term);

        return mockBooks || term.isEmpty()
            ? bookSearchService.getGoogleBooksMockData()
            : bookSearchService.getBasicSearch(term);
    }

    @PostMapping("/users/library")
    public Mono<SaveToLibraryResponse> saveBookToLibrary(@RequestBody SaveBookRequest sbr) {
        System.out.println("/book/search ENDPOINT HIT: "  + sbr);

        return userService.saveToUsersLibrary(sbr);
    }
}
