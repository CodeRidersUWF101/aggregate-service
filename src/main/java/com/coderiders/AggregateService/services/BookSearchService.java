package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.googleBooks.GoogleBook;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BookSearchService {
  Mono<List<GoogleBook>> getGoogleBooksMockData();
}
