package com.strcat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ShortUrlService {
    private final String id;
    private final String secret;

    @Autowired
    public ShortUrlService(@Value("${naver.client.id}") String id,
                           @Value("${naver.client.secret}") String secret) {
        this.id = id;
        this.secret = secret;
    }

    public String generateUrl(String originUrl) throws Exception {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", id);
        headers.set("X-Naver-Client-Secret", secret);

        // 쿼리 파라미터 설정
        String API_URL = "https://openapi.naver.com/v1/util/shorturl";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("url", originUrl);

        RestTemplate restTemplate = new RestTemplate();

        //header 설정
        restTemplate.setInterceptors(List.of(
                (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
                    request.getHeaders().addAll(headers);
                    return execution.execute(request, body);
                }
        ));

        String json = restTemplate.getForEntity(builder.toUriString(), String.class).getBody();

        return extractUrl(json);
    }

    private String extractUrl(String rawResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(rawResponse).get("result");

        return jsonNode.get("url").asText();
    }
}
