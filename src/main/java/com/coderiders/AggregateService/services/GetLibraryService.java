package com.coderiders.AggregateService.services;


import com.coderiders.AggregateService.models.commonutils.models.UserLibraryWithBookDetails;

import java.util.List;

public interface GetLibraryService {
    List<UserLibraryWithBookDetails> getUsersLibrary(String userId);
}
