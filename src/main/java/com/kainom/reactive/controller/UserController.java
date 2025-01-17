package com.kainom.reactive.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kainom.reactive.err.EmailUniquessException;
import com.kainom.reactive.model.User;
import com.kainom.reactive.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
        return userRepository.findByEmail(user.email())
                .flatMap(existingUser -> Mono.error(new EmailUniquessException()))
                .then(userRepository.save(user)) // Save the new user if the email doesn't exist
                .map(ResponseEntity::ok) // Map the saved user to a ResponseEntity
                .doOnNext(savedUser -> System.out.println("New user created: " + savedUser)) // Logging or further
                // action
                .onErrorResume(e -> { // Handling errors, such as email uniqueness violation
                    System.out.println("An exception has occurred: " + e.getMessage());
                    if (e instanceof EmailUniquessException) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .build());
                    } else {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build());
                    }
                });
    }

    @GetMapping("/")
    public Flux<User> getAll() {
        Long start = System.currentTimeMillis();

        return userRepository.findAll()
                .onBackpressureBuffer()
                .doOnNext(user -> log.debug("Processed User: {} in {} ms", user.nome(),
                        System.currentTimeMillis() - start))
                .doOnComplete(() -> log.info("Finished streaming users for getAllUsers in {} ms",
                        System.currentTimeMillis() - start));
    }

    @GetMapping("/stream")
    public Flux<User> getUserByEmail() {
        long start = System.currentTimeMillis();
        return userRepository.findAll()
                .onBackpressureBuffer()
                .doOnNext(user -> log.debug("Processed User: {} in {} ms", user.nome(),
                        System.currentTimeMillis() - start))
                .doOnError(error -> log.error("Error streaming users", error))
                .doOnComplete(() -> log.info("Finished streaming users for streamUsers in {} ms",
                        System.currentTimeMillis(   ) - start));
    }

    @GetMapping("/{id}")
    public Mono<User> getUserByEmail(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteUser(@PathVariable Long id) {
        return userRepository.deleteById(id)
                .flatMap(deleted -> Mono.just(ResponseEntity.ok().build()))
                .switchIfEmpty(
                        Mono.error(new RuntimeException("User not found")))
                .onErrorResume(
                        e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

}
