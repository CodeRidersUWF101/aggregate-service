package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<SaveToLibraryResponse> saveToUsersLibrary(SaveBookRequest saveBookRequest);
    Mono<SaveToLibraryResponse> removeFromUsersLibrary(String bookId);
}
