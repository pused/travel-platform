package com.example.backend.controller;

import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.service.impl.DeepSeekService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author caiya
 * @Description AI 接口控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/ai")
public class ChatController {
    @Resource
    private DeepSeekService deepSeekService;

    @PostMapping("/send")
    public BaseResponse<String> send(String query) {
        String res = deepSeekService.generateResponse(query);
        return Result.success(res);
    }
}
