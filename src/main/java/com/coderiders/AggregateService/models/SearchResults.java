package com.coderiders.AggregateService.models;

import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResults {

    private String kind;
    private int totalItems;
    private List<GoogleBook> items;

}