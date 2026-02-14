package com.example.backend.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.example.backend.config.DeepSeekConfig;
import com.example.backend.entity.DeepSeekResponse;
import com.example.backend.entity.request.deepseek.DeepSeekRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Optional;

/**
 * @Author caiya
 * @Description
 * @Version 1.0
 */
@Service
@Slf4j
public class DeepSeekService {

    @Resource
    private DeepSeekConfig deepSeekConfig;

    public String generateResponse(String query) {
        try {
            // 构建 deepseek 请求对象
            DeepSeekRequest request = buildRequest(query);
            // 设置超时时间（单位：毫秒）
            int connectionTimeout = 60000; // 连接超时 60 秒
            int readTimeout = 60000;      // 读取超时 60 秒

            // 执行 HTTP 请求
            HttpResponse execute = HttpUtil.createRequest(Method.POST, deepSeekConfig.getApiUrl())
                    .body(JSONUtil.toJsonStr(request))
                    .setConnectionTimeout(connectionTimeout)
                    .setReadTimeout(readTimeout)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                    .header("Accept", "application/json")
                    .execute();

            // 获取并日志记录响应内容
            String resp = execute.body();
            log.info("deepseek response: {}", resp);

            // 解析响应并提取内容
            DeepSeekResponse chatResponse = JSONUtil.toBean(resp, DeepSeekResponse.class);
            return extractResponse(chatResponse);
        } catch (Exception e) {
            log.error("Error generating response for query: {}", query, e);
            throw new RuntimeException("Failed to generate response", e);
        }
    }


    /**
     * 构建 deepseek 请求对象
     */
    private DeepSeekRequest buildRequest(String query) {
        return DeepSeekRequest.builder()
                .model(deepSeekConfig.getModel())
                .temperature(deepSeekConfig.getTemperature())
                .max_tokens(deepSeekConfig.getMaxTokens())
                .messages(Collections.singletonList(new DeepSeekRequest.Message("user", query)))
                .build();
    }

    /**
     * 提取回复内容
     */
    private String extractResponse(DeepSeekResponse response) {
        return Optional.ofNullable(response)
                .map(DeepSeekResponse::getChoices)
                .filter(choices -> !choices.isEmpty())
                .map(choices -> choices.get(0))
                .map(choice -> choice.getMessage().getContent())
                .orElseThrow(() -> new RuntimeException("Empty response"));
    }
}
