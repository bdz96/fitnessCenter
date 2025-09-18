package com.example.FitnessCenterApp.controllerTests;

import com.example.FitnessCenterApp.FitnessCenterAppApplication;
import com.example.FitnessCenterApp.controller.ClientMembershipController;
import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.controller.clientMembership.CreateClientMembershipRequest;
import com.example.FitnessCenterApp.controller.clientMembership.SessionsRemainingDto;
import com.example.FitnessCenterApp.model.ClientMembershipDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FitnessCenterAppApplication.class)
@Sql(scripts = "/sql/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ClientMembershipControllerTest {
    private static final Logger log = LoggerFactory.getLogger(ClientMembershipControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @SneakyThrows
    private HttpEntity<String> createJsonEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = OBJECT_MAPPER.writeValueAsString(body);
        log.debug("Creating HttpEntity with body: {}", json);
        return new HttpEntity<>(json, headers);
    }

    @SneakyThrows
    private <T> T toDto(String body, Class<T> clazz) {
        return OBJECT_MAPPER.readValue(body, clazz);
    }

    @Test
    @SneakyThrows
    public void assignMembershipToClientSuccessfully() {
        CreateClientMembershipRequest request = new CreateClientMembershipRequest(1, 1);

        ResponseEntity<String> response = restTemplate.postForEntity(
                ClientMembershipController.BASE_PATH,
                createJsonEntity(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        log.info("Response: {}", response.getBody());

        ClientMembershipDto dto = toDto(response.getBody(), ClientMembershipDto.class);

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getClient().getId()).isEqualTo(1);
        assertThat(dto.getClient().getFirstName()).isEqualTo("Test");
        assertThat(dto.getClient().getLastName()).isEqualTo("User");
        assertThat(dto.getClient().getEmail()).isEqualTo("test.user@example.com");
        assertThat(dto.getMembership().getId()).isEqualTo(1);
        assertThat(dto.getMembership().getName()).isEqualTo("Basic");
        assertThat(dto.getMembership().getPrice()).isEqualTo(100);
        assertThat(dto.getMembership().getDuration()).isEqualTo("1");
        assertThat(dto.getMembership().getTimeUnit().name()).isEqualTo("MONTHS");
        assertThat(dto.getMembership().getSessionsAvailable()).isEqualTo(10);

        LocalDate today = LocalDate.now();
        assertThat(dto.getCreatedAt()).isEqualTo(today);
        assertThat(dto.getExpiresAt()).isEqualTo(today.plusMonths(1));
        assertThat(dto.getSessionsRemaining()).isEqualTo(10);

        List<ClientMembershipDB> memberships = entityManager
                .createQuery("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = 1", ClientMembershipDB.class)
                .getResultList();

        assertThat(memberships).hasSize(1);
        ClientMembershipDB db = memberships.get(0);
        assertThat(db.getId()).isEqualTo(dto.getId());
        assertThat(db.getClientDB().getId()).isEqualTo(1);
        assertThat(db.getMembershipDB().getId()).isEqualTo(1);
        assertThat(db.getCreatedAt()).isEqualTo(dto.getCreatedAt());
        assertThat(db.getExpiresAt()).isEqualTo(dto.getExpiresAt());
        assertThat(db.getSessionsRemaining()).isEqualTo(dto.getSessionsRemaining());
    }

    @Test
    @SneakyThrows
    public void assignMembershipToClientFailWhenClientHasActiveMembership() {
        CreateClientMembershipRequest request = new CreateClientMembershipRequest(2, 2);

        ResponseEntity<String> response = restTemplate.postForEntity(
                ClientMembershipController.BASE_PATH,
                createJsonEntity(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        log.info("Active membership response: {}", response.getBody());

        List<ClientMembershipDB> memberships = entityManager
                .createQuery("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = 2", ClientMembershipDB.class)
                .getResultList();
        assertThat(memberships).hasSize(1);
    }

    @Test
    @SneakyThrows
    public void assignMembershipToClientFailWhenClientNotFound() {
        CreateClientMembershipRequest request = new CreateClientMembershipRequest(9999, 1);

        ResponseEntity<String> response = restTemplate.postForEntity(
                ClientMembershipController.BASE_PATH,
                createJsonEntity(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.info("Client not found response: {}", response.getBody());

        List<ClientMembershipDB> memberships = entityManager
                .createQuery("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = 9999", ClientMembershipDB.class)
                .getResultList();
        assertThat(memberships).isEmpty();
    }

    @Test
    @SneakyThrows
    public void assignMembershipToClientFailWhenMembershipNotFound() {
        CreateClientMembershipRequest request = new CreateClientMembershipRequest(3, 9999);

        ResponseEntity<String> response = restTemplate.postForEntity(
                ClientMembershipController.BASE_PATH,
                createJsonEntity(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.info("Membership not found response: {}", response.getBody());

        List<ClientMembershipDB> memberships = entityManager
                .createQuery("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = 3", ClientMembershipDB.class)
                .getResultList();
        assertThat(memberships).hasSize(1); // pre-existing membership
    }

    @Test
    @SneakyThrows
    public void validateClientMembershipIsActive() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                ClientMembershipController.ACTIVE_MEMBERSHIP_PATH.replace("{clientId}", "2"),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("active membership");
        log.info("Active membership response: {}", response.getBody());
    }

    @Test
    @SneakyThrows
    public void validateClientMembershipIsNotActive() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                ClientMembershipController.ACTIVE_MEMBERSHIP_PATH.replace("{clientId}", "1"),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.info("No active membership response: {}", response.getBody());
    }

    @Test
    @SneakyThrows
    public void validateGetRemainingSessionsActiveMembership() {
        ResponseEntity<SessionsRemainingDto> response = restTemplate.getForEntity(
                ClientMembershipController.SESSIONS_REMAINING_PATH.replace("{clientId}", "2"),
                SessionsRemainingDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSessionsRemaining()).isEqualTo(5);
        log.info("Remaining sessions response: {}", response.getBody().getSessionsRemaining());
    }

    @Test
    @SneakyThrows
    public void validateGetRemainingSessionsNotActiveMembership() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                ClientMembershipController.SESSIONS_REMAINING_PATH.replace("{clientId}", "1"),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.info("No remaining sessions response: {}", response.getBody());
    }

    @Test
    @SneakyThrows
    public void validateUseSessionsSuccessfully() {
        ResponseEntity<SessionsRemainingDto> response = restTemplate.postForEntity(
                ClientMembershipController.USE_SESSION_PATH.replace("{clientId}", "2"),
                null,
                SessionsRemainingDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSessionsRemaining()).isEqualTo(4);
        log.info("Sessions after use response: {}", response.getBody().getSessionsRemaining());

        ClientMembershipDB db = entityManager
                .createQuery("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = 2", ClientMembershipDB.class)
                .getSingleResult();
        assertThat(db.getSessionsRemaining()).isEqualTo(4);
    }

    @Test
    @SneakyThrows
    public void validateUseSessionsWhenNoRemainingSessions() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                ClientMembershipController.USE_SESSION_PATH.replace("{clientId}", "3"),
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        log.info("No remaining sessions response: {}", response.getBody());

        ClientMembershipDB db = entityManager
                .createQuery("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = 3", ClientMembershipDB.class)
                .getSingleResult();
        assertThat(db.getSessionsRemaining()).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    public void validateUseSessionsWhenMembershipNotActive() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                ClientMembershipController.USE_SESSION_PATH.replace("{clientId}", "1"),
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.info("No active membership response: {}", response.getBody());
    }
}