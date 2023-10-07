package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.AggregateService.utilities.AggregateConstants;
import com.coderiders.AggregateService.utilities.UriBuilderWrapper;
import com.coderiders.commonutils.models.User;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;

    @Value("${endpoints.user.library}")
    private String usersLibraryEndpoint;
    @Value("${endpoints.user.friends}")
    private String usersFriendsEndpoint;
    @Value("${endpoints.user.signup}")
    private String usersSignUpEndpoint;

    public UserServiceImpl(@Qualifier("userServiceClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @CachePut(value = "userLibraries", key = "#userId")
    @Override
    public List<UserLibraryWithBookDetails> saveToUsersLibrary(String userId, SaveBookRequest saveBookRequest) {
        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getUsersLibrary(userId));
        log.debug("========================= saveToUsersLibrary: {}", usrLibrary.size());
        usrLibrary.add(saveBookRequest.getBook());
        log.debug("========================= saveToUsersLibrary2: {}", usrLibrary.size());
        String res = webClient
                .post()
                .uri(usersLibraryEndpoint)
                .bodyValue(createSaveBookRequest(UserContext.getCurrentUserContext(), saveBookRequest.getBook()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (res == null) {
            throw new AggregateException("Failed to save to user library");
        }

        return usrLibrary.stream().peek(item -> item.setInLibrary(true)).toList();
    }

    @CachePut(value = "userLibraries", key = "#userId")
    @Override
    public List<UserLibraryWithBookDetails> removeFromUsersLibrary(String userId, UserLibraryWithBookDetails book) {
        String uri = new UriBuilderWrapper(usersLibraryEndpoint)
                .setParameter(AggregateConstants.BOOK_ID, book.getBook_id())
                .setParameter(AggregateConstants.CLERK_ID, getUserContextClerkId())
                .build();

        String response = webClient
                .delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new AggregateException("Remove From User's Library Failed");
        }

        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getUsersLibrary(userId));

        log.debug("=== UserLibrarySize:  {}", usrLibrary.size());

        usrLibrary.removeIf(existingBook -> existingBook.getBook_id().equals(response));

        log.debug("=== UserLibrarySize (Should be 1 less):  {}", usrLibrary.size());

        return usrLibrary;
    }

    @Override
    public Mono<SaveToLibraryResponse> getFriendsCurrentlyReading() {
        String uri = new UriBuilderWrapper(usersFriendsEndpoint)
                .setParameter(AggregateConstants.CLERK_ID, getUserContextClerkId())
                .build();

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class).
                onErrorResume(e -> { throw new AggregateException(e); });
    }

    @Cacheable(value = "userLibraries", key = "#userId")
    public List<UserLibraryWithBookDetails> getUsersLibrary(String userId) {
        List<UserLibraryWithBookDetails> response = webClient
                .get()
                .uri(builder -> builder.path(usersLibraryEndpoint)
                        .queryParam(AggregateConstants.CLERK_ID, userId)
                        .build())
                .retrieve()
                .bodyToFlux(UserLibraryWithBookDetails.class)
                .map(item -> {
                    item.setInLibrary(true);
                    return item;
                }).collectList()
                .block();

        if (response == null) {
            throw new AggregateException("Failed to retrieve user library");
        }

        log.debug("Retrieved library size: {}", response.size());
        return response;
    }

    @Cacheable(value = "users", key = "#user.clerkId")
    @Override
    public User addUser(User user) {

        User response = webClient.post()
                .uri(usersSignUpEndpoint)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .block();

        if (response == null) {
            throw new AggregateException("Failed to save user to database.");
        }

        return response;
    }

    @CachePut(value = "userLibraries", key = "#updateProgress.clerkId")
    @Override
    public List<UserLibraryWithBookDetails> updateBookProgress(UpdateProgress updateProgress) {
        updateProgress.setClerkId(getUserContextClerkId());

        UpdateProgress progress = webClient.patch()
                .uri(usersLibraryEndpoint)
                .bodyValue(updateProgress)
                .retrieve()
                .bodyToMono(UpdateProgress.class)
                .block();

        if (progress == null) {
            throw new AggregateException("Failed to save update user progress");
        }

        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getUsersLibrary(updateProgress.getClerkId()));

        usrLibrary = usrLibrary.stream().map(book -> {
            if (!book.getBook_id().equalsIgnoreCase(progress.getBookId())) {
                return book;
            }

            book.setLast_page_read(progress.getCurrentPage());
            book.setLast_reading_update(Timestamp.from(Instant.now()));

            if (progress.getCurrentPage() < book.getPage_count()) {
                book.setReading_status(AggregateConstants.IN_PROGRESS);
            } else if (progress.getCurrentPage() == 0) {
                book.setReading_status(AggregateConstants.NOT_STARTED);
            } else {
                book.setReading_status(AggregateConstants.COMPLETED);
            }
            return book;
        }).toList();

        return usrLibrary;
    }

    public String getUserContextClerkId() {
        return UserContext.getCurrentUserContext().getClerkId();
    }

    public static SaveBookRequest createSaveBookRequest(UserContext userContext, UserLibraryWithBookDetails book) {
        User user = User.builder()
                .clerkId(userContext.getClerkId())
                .firstName(userContext.getFirstname())
                .lastName(userContext.getLastname())
                .imageUrl(userContext.getImageUrl())
                .username(userContext.getUsername())
                .build();

        return new SaveBookRequest(user, book);
    }
}
