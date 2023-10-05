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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;

    @Autowired
    @Qualifier("userServiceClient")
    private RestTemplate userServiceRestTemplate;

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
    public List<UserLibraryWithBookDetails> saveToUsersLibrary(String userId, UserLibraryWithBookDetails book) {
        UserContext usr = UserContext.getCurrentUserContext();
        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getUsersLibrary(userId));
        book.setInLibrary(true);
        usrLibrary.add(book);

        ResponseEntity<SaveToLibraryResponse> response =
                userServiceRestTemplate.postForEntity(
                        usersLibraryEndpoint,
                        createSaveBookRequest(usr, book),
                        SaveToLibraryResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AggregateException("Failed to save to user library");
        }

        return usrLibrary;
    }

    @CachePut(value = "userLibraries", key = "#userId")  // Update the annotation to CachePut
    @Override
    public List<UserLibraryWithBookDetails> removeFromUsersLibrary(String userId, UserLibraryWithBookDetails book) {
        UserContext usr = UserContext.getCurrentUserContext();

        String uri = new UriBuilderWrapper(usersLibraryEndpoint)
                .setParameter(AggregateConstants.BOOK_ID, book.getBook_id())
                .setParameter(AggregateConstants.CLERK_ID, usr.getClerkId())
                .build();

        var response = webClient
                .delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class)
                .onErrorResume(e -> { throw new AggregateException(e); });

        // Retrieve the current list from the cache, remove the specified book, then update the cache
        List<UserLibraryWithBookDetails> usrLibrary = new ArrayList<>(getUsersLibrary(userId));
        usrLibrary.removeIf(existingBook -> existingBook.getBook_id().equals(book.getBook_id()));

        return usrLibrary;
    }

    @Override
    public Mono<SaveToLibraryResponse> getFriendsCurrentlyReading() {
        UserContext usr = UserContext.getCurrentUserContext();

        String uri = new UriBuilderWrapper(usersFriendsEndpoint)
                .setParameter(AggregateConstants.CLERK_ID, usr.getClerkId())
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
        String uri = new UriBuilderWrapper(usersLibraryEndpoint)
                .setParameter(AggregateConstants.CLERK_ID, userId)
                .build();

        ResponseEntity<List<UserLibraryWithBookDetails>> response =
                userServiceRestTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new AggregateException("Failed to retrieve user library");
        }

        return response.getBody().stream()
                .peek(item -> item.setInLibrary(true))
                .toList();
    }

    @Cacheable(value = "users", key = "#user.clerkId")
    @Override
    public User addUser(User user) {
        String uri = new UriBuilderWrapper(usersSignUpEndpoint).build();

        ResponseEntity<User> response = userServiceRestTemplate.postForEntity(uri, user, User.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new AggregateException("Failed to save user to database.");
        }
        return response.getBody();
    }


    public String getUserContextClerkId() {
        return UserContext.getCurrentUserContext().getClerkId();
    }

    public static SaveBookRequest createSaveBookRequest(UserContext usr, UserLibraryWithBookDetails book) {
        return new SaveBookRequest(usr.getClerkId(), book.getIsbn_10(), book.getIsbn_13());
    }
}
