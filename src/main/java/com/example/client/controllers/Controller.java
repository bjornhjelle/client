package com.example.client.controllers;

import com.example.client.models.TokenRequestRequest;
import com.example.client.models.TokenRequestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RestController
public class Controller {


    private final String PPID = "356860-111643";
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    private WebClient webClient;

    @Value("${endpoint.apple}")
    String appleBaseUrl;

    public Controller(RestTemplate restTemplate, ObjectMapper objectMapper, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }


    @ApiOperation(value = "Request tokens from Apple")
    @PostMapping(path = "/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity requestTokens(@Valid @RequestParam Integer numberOfTokens) {
        String appleUrl = String.format("%s/authEntityRequests", appleBaseUrl);
        log.info("received POST /tokens?numberOfTokens={}", numberOfTokens);
        TokenRequestRequest tokenRequestRequest = TokenRequestRequest.builder()
                .ppid(PPID)
                .requestedAuthEntityCount(numberOfTokens)
                .build();

        HttpHeaders headers = getHttpHeaders();
        log.info("headers: \n{}", headers);

        try {
            String body = objectMapper.writeValueAsString(tokenRequestRequest);
            log.info("will send POST to {} with requestBody:\n{}", appleUrl, body);
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<TokenRequestResponse> response =
                    restTemplate.postForEntity(appleUrl, request, TokenRequestResponse.class);
            log.info("return status: {}", response.getStatusCode());
        } catch (HttpStatusCodeException e) {
            log.info("return status: {}", e.getStatusCode());
            log.info("exception: {}", e.getMessage());
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "Request tokens from Apple")
    @PostMapping(path = "/fluxTokens", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity requestTokensFlux(@Valid @RequestParam Integer numberOfTokens) {
        String appleUrl = String.format("%s/authEntityRequests", appleBaseUrl);
        log.info("received POST /tokens?numberOfTokens={}", numberOfTokens);

        TokenRequestRequest tokenRequestRequest = TokenRequestRequest.builder()
                .ppid(PPID)
                .requestedAuthEntityCount(numberOfTokens)
                .build();

        HttpHeaders headers = getHttpHeaders();
        log.info("headers: \n{}", headers);

        try {
            String body = objectMapper.writeValueAsString(tokenRequestRequest);
            log.info("will send POST to {} with requestBody:\n{}", appleUrl, body);

            webClient.post()
                    .uri(appleUrl)
                    .body(Mono.just(tokenRequestRequest), TokenRequestRequest.class)
                    .retrieve()
                    .bodyToMono(TokenRequestResponse.class)
                    .doOnError(error -> {
                        log.error("Failed to call apple server: " + error.getMessage());
                    })
                    .subscribe(e -> log.info(e.toString()));
        } catch (HttpStatusCodeException e) {
            log.info("return status: {}", e.getStatusCode());
            log.info("exception: {}", e.getMessage());
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "BagId AS/Token Authentication Server/0.0.1");
        return headers;
    }


}
