package com.example.FitnessCenterApp.controllerTests;

import com.example.FitnessCenterApp.controller.ClientController;
import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.model.ClientDB;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@Transactional
@Rollback
public class ClientControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ClientControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    private static int counter = 0;


    private HttpEntity<?> createJsonEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.debug("Creating HttpEntity with body: {}", body);
        return new HttpEntity<>(body, headers);
    }

    private CreateClientRequest createValidClientRequest() {
        counter++;
        String uniqueEmail = "berina+" + counter + "@test.com";
        return new CreateClientRequest(
                "Berina",
                "Dizdarevic",
                LocalDate.of(1996, 4, 27),
                uniqueEmail,
                "123456789",
                "plainPassword123"
        );
    }

    private List<ClientDB> findClientsByEmail(String email) {
        return entityManager.createQuery(
                        "SELECT c FROM ClientDB c WHERE c.email = :email", ClientDB.class)
                .setParameter("email", email)
                .getResultList();
    }

    private long getClientCount() {
        return entityManager.createQuery("SELECT COUNT(c) FROM ClientDB c", Long.class).getSingleResult();
    }

    private <T> ResponseEntity<T> postClient(CreateClientRequest request, Class<T> responseType) {
        log.info("Sending POST request: {}", request);
        ResponseEntity<T> response = restTemplate.postForEntity(ClientController.BASE_PATH, createJsonEntity(request), responseType);
        log.info("Response status: {}", response.getStatusCode());
        log.info("Response body: {}", response.getBody());
        return response;
    }

    private ClientDto createAndSaveValidClient() {
        CreateClientRequest request = createValidClientRequest();
        ResponseEntity<ClientDto> response = restTemplate.postForEntity(ClientController.BASE_PATH, createJsonEntity(request), ClientDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ClientDto client = response.getBody();
        assertThat(client).isNotNull();
        return client;
    }

    private void validateInvalidClientRequest(String fieldName, Object invalidValue, String expectedMessage) {
        CreateClientRequest request = createValidClientRequest();

        try {
            Field field = CreateClientRequest.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(request, invalidValue);
            log.info("Set invalid field '{}' to value: {}", fieldName, invalidValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set invalid value", e);
        }

        ResponseEntity<String> response = postClient(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(expectedMessage);

        List<ClientDB> clients = findClientsByEmail(request.getEmail());
        assertThat(clients).isEmpty();
    }

    private void validateNotFoundForNonExistingId(HttpMethod method, int nonExistingId) {
        ResponseEntity<ProblemDetail> response;
        if (method == HttpMethod.DELETE || method == HttpMethod.PUT) {
            HttpEntity<?> entity = method == HttpMethod.PUT ? createJsonEntity(createValidClientRequest()) : null;
            response = restTemplate.exchange(ClientController.BASE_PATH + "/" + nonExistingId, method, entity, ProblemDetail.class);
        } else if (method == HttpMethod.GET) {
            response = restTemplate.getForEntity(ClientController.BASE_PATH + "/" + nonExistingId, ProblemDetail.class);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method for this test");
        }

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualToIgnoringCase("Resource not found");
    }

    /**
     * POST client tests
     */
    @Test
    public void validateClientCreatedSuccessfully() {
        CreateClientRequest request = createValidClientRequest();

        ResponseEntity<String> response = postClient(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<ClientDB> clients = findClientsByEmail(request.getEmail());

        log.info("Database records found with email '{}': {}", request.getEmail(), clients.size());

        assertThat(clients).hasSize(1);

        ClientDB savedClient = clients.get(0);

        assertThat(savedClient.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(savedClient.getLastName()).isEqualTo(request.getLastName());
        assertThat(savedClient.getDateOfBirth()).isEqualTo(request.getDateOfBirth());
        assertThat(savedClient.getEmail()).isEqualTo(request.getEmail());
        assertThat(savedClient.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
    }

    @Test
    public void validateClientRejectedWhenFirstNameExceedsMaxLength() {
        validateInvalidClientRequest("firstName", "A".repeat(31), "First name must not exceed 30 characters");
    }

    @Test
    public void validateClientRejectedWhenLastNameExceedsMaxLength() {
        validateInvalidClientRequest("lastName", "B".repeat(31), "Last name must not exceed 30 characters");
    }

    @Test
    public void validateClientRejectedWhenEmailInvalid() {
        validateInvalidClientRequest("email", "berina-email", "Email must be valid");
    }

    @Test
    public void validateClientRejectedWhenEmailDoesNotContainCom() {
        validateInvalidClientRequest("email", "berina@email.org", "Email must contain '@' and end with '.com'");
    }

    @Test
    public void validateClientRejectedWhenDateOfBirthInFuture() {
        validateInvalidClientRequest("dateOfBirth", LocalDate.now().plusDays(1), "Date of birth must be in the past");
    }

    @Test
    public void validateClientRejectedWhenPhoneNumberIsBlank() {
        validateInvalidClientRequest("phoneNumber", "", "Phone number is required");
    }

    @Test
    public void validateClientRejectedWhenEmailIsDuplicate() {
        CreateClientRequest request = createValidClientRequest();

        ResponseEntity<String> initialClientResponse = postClient(request, String.class);
        assertThat(initialClientResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<ProblemDetail> duplicatedClientResponse = postClient(request, ProblemDetail.class);
        assertThat(duplicatedClientResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ProblemDetail body = duplicatedClientResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Email Already Exists");
        assertThat(body.getStatus()).isEqualTo(409);
        assertThat(body.getDetail()).isEqualTo("Email already exists");

        List<ClientDB> clients = findClientsByEmail(request.getEmail());
        assertThat(clients).hasSize(1);
    }

    /**
     * GET client tests
     */
    @Test
    public void validateGetAllClients() {
        int initialCount = (int) getClientCount();

        CreateClientRequest client1 = createValidClientRequest();
        CreateClientRequest client2 = createValidClientRequest();

        postClient(client1, String.class);
        postClient(client2, String.class);

        ResponseEntity<ClientDto[]> response = restTemplate.getForEntity(ClientController.BASE_PATH, ClientDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ClientDto[] clientsFromResponse = response.getBody();

        assertThat(clientsFromResponse).isNotNull();

        List<ClientDB> clientsInDb = entityManager.createQuery("SELECT c FROM ClientDB c", ClientDB.class)
                .getResultList();

        assertThat(clientsFromResponse.length).isEqualTo(clientsInDb.size());

        assertThat(clientsInDb.size()).isEqualTo(initialCount + 2);

        for (ClientDto clientDto : clientsFromResponse) {
            boolean matchFound = clientsInDb.stream().anyMatch(clientDB ->
                    clientDB.getId().equals(clientDto.getId()) &&
                            clientDB.getFirstName().equals(clientDto.getFirstName()) &&
                            clientDB.getLastName().equals(clientDto.getLastName()) &&
                            clientDB.getEmail().equals(clientDto.getEmail()) &&
                            clientDB.getPhoneNumber().equals(clientDto.getPhoneNumber()) &&
                            clientDB.getDateOfBirth().equals(clientDto.getDateOfBirth())
            );
            assertThat(matchFound).isTrue();
        }
    }

    /**
     * GET by ID client tests
     */
    @Test
    public void validateGetByIdReturnsCorrectClient() {
        ClientDto createdClient = createAndSaveValidClient();

        Integer clientId = createdClient.getId();

        ResponseEntity<ClientDto> response = restTemplate.getForEntity(ClientController.BASE_PATH + "/" + clientId, ClientDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ClientDto client = response.getBody();
        assertThat(client).isNotNull();
        assertThat(client.getEmail()).isEqualTo(createdClient.getEmail());
    }

    @Test
    public void validateGetByIdWithNonExistingId() {
        validateNotFoundForNonExistingId(HttpMethod.GET, 99999);
    }

    /**
     * DELETE by ID client tests
     */
    @Test
    public void validateDeleteRemovedClient() {
        ClientDto createdClient = createAndSaveValidClient();

        Integer clientId = createdClient.getId();

        restTemplate.delete(ClientController.BASE_PATH + "/" + clientId);

        List<ClientDB> clients = findClientsByEmail(createdClient.getEmail());
        assertThat(clients).isEmpty();
    }

    @Test
    public void validateDeleteWithNonExistingId() {
        validateNotFoundForNonExistingId(HttpMethod.DELETE, 88888);
    }

    /**
     * PUT client tests
     */
    @Test
    public void validateUpdateClient() {
        ClientDto createdClient = createAndSaveValidClient();

        Integer clientId = createdClient.getId();

        CreateClientRequest updateRequest = new CreateClientRequest(
                "UpdatedFirstName",
                "UpdatedLastName",
                LocalDate.of(1990, 1, 1),
                createdClient.getEmail(),
                "987654321",
                "plainPassword123" //  add new model with just phone num update ?
        );

        ResponseEntity<ClientDto> updateResponse = restTemplate.exchange(
                ClientController.BASE_PATH + "/" + clientId,
                HttpMethod.PUT,
                createJsonEntity(updateRequest),
                ClientDto.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ClientDto updatedClientResponse = updateResponse.getBody();
        assertThat(updatedClientResponse).isNotNull();
        assertThat(updatedClientResponse.getFirstName()).isEqualTo(updateRequest.getFirstName());
        assertThat(updatedClientResponse.getLastName()).isEqualTo(updateRequest.getLastName());
        assertThat(updatedClientResponse.getDateOfBirth()).isEqualTo(updateRequest.getDateOfBirth());
        assertThat(updatedClientResponse.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(updatedClientResponse.getPhoneNumber()).isEqualTo(updateRequest.getPhoneNumber());

        List<ClientDB> clientsInDb = entityManager.createQuery(
                        "SELECT c FROM ClientDB c WHERE c.id = :id", ClientDB.class)
                .setParameter("id", clientId)
                .getResultList();

        assertThat(clientsInDb).hasSize(1);

        ClientDB clientInDb = clientsInDb.get(0);
        assertThat(clientInDb.getFirstName()).isEqualTo(updateRequest.getFirstName());
        assertThat(clientInDb.getLastName()).isEqualTo(updateRequest.getLastName());
        assertThat(clientInDb.getDateOfBirth()).isEqualTo(updateRequest.getDateOfBirth());
        assertThat(clientInDb.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(clientInDb.getPhoneNumber()).isEqualTo(updateRequest.getPhoneNumber());
    }

    @Test
    public void validateUpdateClientWithNonExistingId() {
        int nonExistingId = 999999;
        CreateClientRequest updateRequest = createValidClientRequest();

        long countBefore = getClientCount();

        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                ClientController.BASE_PATH + "/" + nonExistingId,
                HttpMethod.PUT,
                createJsonEntity(updateRequest),
                ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualToIgnoringCase("Resource not found");

        long countAfter = getClientCount();

        assertThat(countAfter).isEqualTo(countBefore);
    }
}
