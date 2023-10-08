package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.GetLibraryService;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.AggregateService.utilities.AggregateConstants;
import com.coderiders.AggregateService.utilities.UriBuilderWrapper;
import com.coderiders.commonutils.models.User;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
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

    @Autowired
    private GetLibraryService getLibraryService;

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
        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getLibraryService.getUsersLibrary(userId));
        List<String> bookIds = usrLibrary.stream().map(UserLibraryWithBookDetails::getBook_id).toList();

        if (bookIds.contains(saveBookRequest.getBook().getBook_id())) {
            return usrLibrary.stream().peek(item -> item.setInLibrary(true)).toList();
        }

        usrLibrary.add(saveBookRequest.getBook());

        String res = webClient
                .post()
                .uri(usersLibraryEndpoint)
                .bodyValue(createSaveBookRequest(saveBookRequest.getBook()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + usersLibraryEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + usersLibraryEndpoint, errorMessage))))
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
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from DELETE " + usersLibraryEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from DELETE " + usersLibraryEndpoint, errorMessage))))
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new AggregateException("Remove From User's Library Failed");
        }

        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getLibraryService.getUsersLibrary(userId));

        log.debug("=== UserLibrarySize:  {}", usrLibrary.size());
        usrLibrary.removeIf(existingBook -> existingBook.getBook_id().equals(response));
        log.debug("=== UserLibrarySize (Should be 1 less):  {}", usrLibrary.size());
        // TODO: Currently is not 1 less. I think the getUsersLibrary(userId) is always making the DB call. Why?

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
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + usersFriendsEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + usersFriendsEndpoint, errorMessage))))
                .bodyToMono(SaveToLibraryResponse.class).
                onErrorResume(e -> { throw new AggregateException(e); });
    }



    @Cacheable(value = "users", key = "#user.clerkId")
    @Override
    public User addUser(User user) {

        User response = webClient.post()
                .uri(usersSignUpEndpoint)
                .bodyValue(user)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + usersSignUpEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + usersSignUpEndpoint, errorMessage))))
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
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from PATCH " + usersLibraryEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from PATCH " + usersLibraryEndpoint, errorMessage))))
                .bodyToMono(UpdateProgress.class)
                .block();

        if (progress == null) {
            throw new AggregateException("Failed to save update user progress");
        }

        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getLibraryService.getUsersLibrary(updateProgress.getClerkId()));

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

    public SaveBookRequest createSaveBookRequest(UserLibraryWithBookDetails book) {
        UserContext userContext = UserContext.getCurrentUserContext();

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
