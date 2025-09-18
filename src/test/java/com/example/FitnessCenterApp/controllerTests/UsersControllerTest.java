package com.example.FitnessCenterApp.controllerTests;

import com.example.FitnessCenterApp.FitnessCenterAppApplication;
import com.example.FitnessCenterApp.controller.login.LoginRequest;
import com.example.FitnessCenterApp.controller.login.TokenResponse;
import com.example.FitnessCenterApp.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@TestPropertySource(properties = "auth.enabled=true")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FitnessCenterAppApplication.class)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@Sql(scripts = "/sql/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UsersControllerTest {

    private static final Logger log = LoggerFactory.getLogger(UsersControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtUtil jwtUtil;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @SneakyThrows
    private HttpEntity<String> createJsonEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = objectMapper.writeValueAsString(body);
        log.info("Request body: {}", json);
        return new HttpEntity<>(json, headers);
    }

    @SneakyThrows
    private <T> T toDto(String body, Class<T> clazz) {
        return objectMapper.readValue(body, clazz);
    }

    @Test
    @SneakyThrows
    public void loginSuccessfully() {
        LoginRequest request = new LoginRequest("berksLoginTest@gmail.com", "krompir96");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users/token",
                createJsonEntity(request),
                String.class
        );

        log.info("loginSuccessfully - HTTP Status: {}", response.getStatusCode());
        log.info("loginSuccessfully - Response body: {}", response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        TokenResponse tokenResponse = toDto(response.getBody(), TokenResponse.class);
        assertThat(tokenResponse.getToken()).isNotBlank();

        String userId = String.valueOf(jwtUtil.validateTokenAndGetUserId(tokenResponse.getToken()));
        log.info("loginSuccessfully - Validated userId: {}", userId);
        assertThat(userId).isEqualTo("2");
    }

    @Test
    @SneakyThrows
    public void loginFailWrongPassword() {
        LoginRequest request = new LoginRequest("berksLoginTest@gmail.com", "wrongpass");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users/token",
                createJsonEntity(request),
                String.class
        );

        log.info("loginFailWrongPassword - HTTP Status: {}", response.getStatusCode());
        log.info("loginFailWrongPassword - Response body: {}", response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @SneakyThrows
    public void loginFailUserNotFound() {
        LoginRequest request = new LoginRequest("does.not.exist@example.com", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users/token",
                createJsonEntity(request),
                String.class
        );

        log.info("loginFailUserNotFound - HTTP Status: {}", response.getStatusCode());
        log.info("loginFailUserNotFound - Response body: {}", response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @SneakyThrows
    public void getCurrentUserSuccessfully() {
        LoginRequest loginRequest = new LoginRequest("berksLoginTest@gmail.com", "krompir96");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/users/token",
                createJsonEntity(loginRequest),
                String.class
        );

        log.info("getCurrentUserSuccessfully - Login HTTP Status: {}", loginResponse.getStatusCode());
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        TokenResponse tokenResponse = toDto(loginResponse.getBody(), TokenResponse.class);
        log.info("getCurrentUserSuccessfully - Token: {}", tokenResponse.getToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getToken());

        ResponseEntity<String> meResponse = restTemplate.exchange(
                "/users/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        log.info("getCurrentUserSuccessfully - /me HTTP Status: {}", meResponse.getStatusCode());
        log.info("getCurrentUserSuccessfully - /me Response body: {}", meResponse.getBody());

        assertThat(meResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(meResponse.getBody()).contains("Current user : 2");
    }

    @Test
    @SneakyThrows
    public void getCurrentUserFailWithoutToken() {
        ResponseEntity<String> meResponse = restTemplate.getForEntity("/users/me", String.class);

        log.info("getCurrentUserFailWithoutToken - HTTP Status: {}", meResponse.getStatusCode());
        log.info("getCurrentUserFailWithoutToken - Response body: {}", meResponse.getBody());

        assertThat(meResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(meResponse.getBody()).contains("Unauthorized");
    }
}
