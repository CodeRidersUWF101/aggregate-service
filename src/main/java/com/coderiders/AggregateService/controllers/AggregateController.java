package com.coderiders.AggregateService.controllers;


import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RefreshScope
@RestController
@RequiredArgsConstructor
public class AggregateController {

    private final BookSearchService bookSearchService;
    private final UserService userService;

    @Value("${flags.booksearch.mockbooks:false}")
    private boolean mockBooks;

    @Value("${flags.aggregateService.mockdeletebook:false}")
    private boolean mockDeleteBook;

    @Value("${flags.aggregateService.mocksavebook:false}")
    private boolean mockSaveBook;

    @GetMapping("/")
    public String myRoute() {
        log.info("Base Route Hit");
        return "Successful AggregateController";
    }

    @GetMapping("/book/search")
    public Mono<List<GoogleBook>> getBookSearchMockData(@RequestParam String term) {
        log.info("/book/search GET ENDPOINT HIT: " + term);
        return mockBooks || term.isEmpty()
                ? bookSearchService.getGoogleBooksMockData()
                : bookSearchService.getBasicSearch(term);
    }

    @PostMapping("/users/library")
    public Mono<SaveToLibraryResponse> saveBookToLibrary(@RequestBody SaveBookRequest sbr) {
        log.info("/users/library POST ENDPOINT HIT: " + sbr);
        return mockSaveBook
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Added Book"))
                : userService.saveToUsersLibrary(sbr);

//        return userService.saveToUsersLibrary(sbr);
    }

    @DeleteMapping("/users/library")
    public Mono<SaveToLibraryResponse> removeBookFromLibrary(@RequestParam("book_id") String bookId) {
        log.info("/users/library DELETE ENDPOINT HIT: " + bookId);
        return mockDeleteBook
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Removed Book"))
                : userService.removeFromUsersLibrary(bookId);

//        return userService.removeFromUsersLibrary(bookId, clerkId);
    }
}
