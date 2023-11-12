package com.coderiders.AggregateService.services;


import com.coderiders.AggregateService.models.commonutils.models.UserLibraryWithBookDetails;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BookSearchService {
  Mono<List<UserLibraryWithBookDetails>> getBasicSearch(String query);
  Mono<List<UserLibraryWithBookDetails>> getPaginatedSearch(String query, String startIndex);
}
