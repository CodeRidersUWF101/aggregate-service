package com.coderiders.AggregateService.services;


import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BookSearchService {
  Mono<List<GoogleBook>> getGoogleBooksMockData();
  Mono<List<GoogleBook>> getBasicSearch(String query);
}
