package com.kangwon.flow.service;

import com.kangwon.flow.EmbeddedRedis;
import com.kangwon.flow.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(EmbeddedRedis.class)
class UserQueueServiceTest {

    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
    }

    @Test
    void registerWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerWaitQueue("default", 101L))
                .expectNext(2L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerWaitQueue("default", 102L))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void emptyAllowUser() {
        StepVerifier.create(
                userQueueService.registerWaitQueue("default", 100L)
                .then(userQueueService.registerWaitQueue("default", 101L))
                .then(userQueueService.registerWaitQueue("default", 102L))
                .then(userQueueService.allowUser("default", 2L)))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void emptyAllowUse2r() {
        StepVerifier.create(
                        userQueueService.registerWaitQueue("default", 100L)
                                .then(userQueueService.registerWaitQueue("default", 101L))
                                .then(userQueueService.registerWaitQueue("default", 102L))
                                .then(userQueueService.allowUser("default", 2L)))
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    void isAllowed() {
        StepVerifier.create(
                        userQueueService.isAllowed("default", 100L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void getRank() {
        StepVerifier.create(
                userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.getRank("default", 100L)))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void emptyRank() {
        StepVerifier.create(
                        userQueueService.registerWaitQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void isAllowedByToken() throws NoSuchAlgorithmException {
        StepVerifier.create(userQueueService.isAllowedByToken("default", 100L, ""))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void generateToken() throws NoSuchAlgorithmException {
        StepVerifier.create(userQueueService.generateToken("default", 100L))
                .expectNext("d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8")
                .verifyComplete();
    }
}