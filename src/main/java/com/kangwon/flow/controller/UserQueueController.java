package com.kangwon.flow.controller;

import com.kangwon.flow.dto.AllowUserResponse;
import com.kangwon.flow.dto.AllowedUserResponse;
import com.kangwon.flow.dto.RankNumberResponse;
import com.kangwon.flow.dto.RegisterUserResponse;
import com.kangwon.flow.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/queue")
public class UserQueueController {

    private final UserQueueService userQueueService;

    // 등록할 수 있는 API Path

    @PostMapping("")
    public Mono<RegisterUserResponse> registerUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                    @RequestParam(name = "user_id") Long userId) {
        return userQueueService.registerWaitQueue(queue, userId)
                .map(i -> new  RegisterUserResponse((Long) i));
    }

    @PostMapping("/allow")
    public Mono<AllowUserResponse> allowUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                             @RequestParam(name = "count") Long count) {
        return userQueueService.allowUser(queue, count)
                .map(allowed -> new AllowUserResponse(count, allowed));
    }

    @GetMapping("/allowed")
    public Mono<AllowedUserResponse> isAllowedUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                 @RequestParam(name = "user_id") Long userId,
                                                   @RequestParam(name = "token") String token) throws NoSuchAlgorithmException {
        return userQueueService.isAllowedByToken(queue, userId, token)
                .map(AllowedUserResponse::new);
    }

    @GetMapping("/rank")
    public Mono<RankNumberResponse> getRankUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId) {
        return userQueueService.getRank(queue, userId)
                .map(RankNumberResponse::new);
    }

    @GetMapping("/touch")
    Mono<?> touch(@RequestParam(name = "queue", defaultValue = "default") String queue,
                  @RequestParam(name = "user_id") Long userId,
                  ServerWebExchange exchange) {
        return Mono.defer(() -> {
                    try {
                        return userQueueService.generateToken(queue, userId);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(token -> {
                    exchange.getResponse().addCookie(
                            ResponseCookie.from("user-queue-%s-token".formatted(queue), token)
                                    .maxAge(Duration.ofSeconds(300))
                                    .path("/")
                                    .build()
                    );
                    return token;
                });

    }
}
