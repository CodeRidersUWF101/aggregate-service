package com.coderiders.AggregateService.utilities;

import com.coderiders.commonutils.GoogleBookUtils;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregateUtils {

    public static UserLibraryWithBookDetails googleBookToLibraryWithDetails(GoogleBook googleBook) {
        return UserLibraryWithBookDetails.builder()
                .book_id(googleBook.id)
                .api_id(googleBook.id)
                .title(googleBook.volumeInfo.title)
                .author(GoogleBookUtils.getAuthors(googleBook))
                .publisher(googleBook.volumeInfo.publisher)
                .published_date(googleBook.volumeInfo.publishedDate)
                .description(googleBook.volumeInfo.description)
                .isbn_10(GoogleBookUtils.getISBN10(googleBook))
                .isbn_13(GoogleBookUtils.getISBN13(googleBook))
                .page_count(googleBook.volumeInfo.pageCount)
                .print_type(googleBook.volumeInfo.printType)
                .categories(GoogleBookUtils.getCategories(googleBook))
                .average_rating(googleBook.volumeInfo.averageRating)
                .ratings_count(googleBook.volumeInfo.ratingsCount)
                .maturity_rating(googleBook.volumeInfo.maturityRating)
                .small_thumbnail(GoogleBookUtils.getSmallThumbnail(googleBook))
                .thumbnail(GoogleBookUtils.getThumbnail(googleBook))
                .reading_status("NOT_STARTED")
                .last_page_read(null)
                .last_reading_update(null)
                .isInLibrary(false)
                .build();
    }

    public static List<UserLibraryWithBookDetails> googleBookToLibraryWithDetails(List<GoogleBook> googleBooks, List<UserLibraryWithBookDetails> inLibrary) {
        Set<String> inLibraryBookIds = inLibrary.stream()
                .map(UserLibraryWithBookDetails::getBook_id)
                .collect(Collectors.toSet());

        return googleBooks.stream()
                .map(AggregateUtils::googleBookToLibraryWithDetails)
                .peek(book -> book.setInLibrary(inLibraryBookIds.contains(book.getBook_id())))
                .collect(Collectors.toList());
    }
}