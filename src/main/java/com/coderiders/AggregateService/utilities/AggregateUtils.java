package com.coderiders.AggregateService.utilities;

import com.coderiders.AggregateService.models.LeaderboardUser;
import com.coderiders.commonutils.GoogleBookUtils;
import com.coderiders.commonutils.models.GamificationLeaderboard;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.UtilsUser;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;

import java.util.List;
import java.util.Map;
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

    public static List<UserLibraryWithBookDetails> googleBookToLibraryWithDetails(List<GoogleBook> googleBooks, List<UserLibraryWithBookDetails> inCachedLibrary) {
        Set<String> inLibraryBookIds = inCachedLibrary.stream()
                .map(UserLibraryWithBookDetails::getBook_id)
                .collect(Collectors.toSet());

        return googleBooks.stream()
                .filter(book -> book.volumeInfo.pageCount > 0) // Only Books > 0 allowed
                .map(AggregateUtils::googleBookToLibraryWithDetails)
                .peek(book -> book.setInLibrary(inLibraryBookIds.contains(book.getBook_id())))
                .collect(Collectors.toList());
    }

    public static List<LeaderboardUser> gamificationLeaderboardToLeaderboardUser(List<GamificationLeaderboard> gl, List<UtilsUser> ul) {
        Map<String, UtilsUser> clerkIdToUserMap = ul.stream()
                .collect(Collectors.toMap(UtilsUser::getClerkId, u -> u));

        List<LeaderboardUser> retList = gl.stream()
                .map(gamificationLeaderboard -> {
                    UtilsUser user = clerkIdToUserMap.get(gamificationLeaderboard.getClerkId());

                    String firstLast = user == null ? null : user.getFirstName() + " " + user.getLastName();
                    String username = user == null ? gamificationLeaderboard.getClerkId() : user.getUsername();
                    String displayName = firstLast == null ? username : firstLast;

                    String imageUrl = user == null ? null : user.getImageUrl();
                    String avatarUrl = imageUrl == null ? "https://i.pravatar.cc/300" : imageUrl;

                    LeaderboardUser usr = new LeaderboardUser();
                    usr.setPoints(gamificationLeaderboard.getTotalPoints());
                    usr.setAvatarUrl(avatarUrl);
                    usr.setClerkId(gamificationLeaderboard.getClerkId());
                    usr.setDisplayName(displayName);

                    return usr;
                })
                .sorted((o1, o2) -> o2.getPoints() - o1.getPoints()) // sort by points
                .collect(Collectors.toList());

        for (int i = 0; i < retList.size(); i++) {
            retList.get(i).setRank(i + 1);
        }

        return retList;
    }
}
