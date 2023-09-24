package com.coderiders.AggregateService.models.googleBooks;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleBook {

  public String kind;
  public String id;
  public String etag;
  public String selfLink;
  public VolumeInfo volumeInfo;
  public SearchInfo searchInfo;
}
