package com.kangwon.flow.controller;

import com.kangwon.flow.dto.AllowUserResponse;
import com.kangwon.flow.dto.AllowedUserResponse;
import com.kangwon.flow.dto.RegisterUserResponse;
import com.kangwon.flow.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
                                 @RequestParam(name = "user_id") Long userId) {
        return userQueueService.isAllowed(queue, userId)
                .map(AllowedUserResponse::new);
    }
}
