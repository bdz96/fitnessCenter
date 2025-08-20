package com.example.FitnessCenterApp.controllerTests;

import com.example.FitnessCenterApp.FitnessCenterAppApplication;
import com.example.FitnessCenterApp.controller.login.LoginRequest;
import com.example.FitnessCenterApp.controller.login.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FitnessCenterAppApplication.class)
@Sql(scripts = "/sql/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UsersControllerTest {

    private static final Logger log = LoggerFactory.getLogger(UsersControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String BASE_PATH = "/users/token";

    @SneakyThrows
    private HttpEntity<String> createJsonEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = OBJECT_MAPPER.writeValueAsString(body);
        return new HttpEntity<>(json, headers);
    }

    @Test
    @SneakyThrows
    public void loginSuccessfully() {
        LoginRequest request = new LoginRequest("login.client@example.com", "krompir96");

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                BASE_PATH,
                createJsonEntity(request),
                TokenResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        log.info("Valid login token: {}", response.getBody().getToken());
    }

    @Test
    @SneakyThrows
    public void loginInvalidEmail() {
        LoginRequest request = new LoginRequest("notfound@example.com", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_PATH,
                createJsonEntity(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid credentials");
        log.info("Invalid email response: {}", response.getBody());
    }

    @Test
    @SneakyThrows
    public void loginInvalidPassword() {
        LoginRequest request = new LoginRequest("login.client@example.com", "wrongPassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_PATH,
                createJsonEntity(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid credentials");
        log.info("Invalid password response: {}", response.getBody());
    }
}
