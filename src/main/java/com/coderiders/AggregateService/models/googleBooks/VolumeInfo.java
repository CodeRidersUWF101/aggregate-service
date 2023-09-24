package com.coderiders.AggregateService.models.googleBooks;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VolumeInfo {

  public String title;
  public String subtitle;
  public List<String> authors;

  public String publisher;
  public String publishedDate;
  public String description;
  public List<IndustryIdentifier> industryIdentifiers;

  public int pageCount;
  public String printType;
  public List<String> categories;
  public double averageRating;
  public int ratingsCount;
  public String maturityRating;
  public ImageLinks imageLinks;
  public String language;
  public String previewLink;
  public String infoLink;
  public String canonicalVolumeLink;
}
