package com.kainom.reactive.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.kainom.reactive.model.User;

import reactor.core.publisher.Mono;


public interface UserRepository  extends R2dbcRepository<User,Long>{
    public Mono<User> findByEmail(String email  );

}
