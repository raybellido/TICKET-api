package com.helpdesk.app.integration;

import com.helpdesk.app.application.port.out.PasswordEncoder;
import com.helpdesk.app.application.port.out.TokenGenerator;
import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    private static final String PASSWORD = "secreto123";


    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("No autenticado"));
    }

    @Test
    void shouldRejectRequestWithGarbageToken() throws Exception {
        mockMvc.perform(get("/api/tickets").header(HttpHeaders.AUTHORIZATION, "Bearer no-es-un-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnTokenOnValidLogin() throws Exception {
        String email = createUser("login", UserRole.CUSTOMER);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void shouldRejectLoginWithWrongPassword() throws Exception {
        String email = createUser("badpass", UserRole.CUSTOMER);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"incorrecta"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales invalidas"));
    }


    @Test
    void customerShouldOwnTheTicketItCreates() throws Exception {
        String email = createUser("owner", UserRole.CUSTOMER);
        long customerId = userRepository.findByEmail(email).orElseThrow().getId();
        String token = tokenFor(email);

        String victimEmail = createUser("victim", UserRole.CUSTOMER);
        long victimId = userRepository.findByEmail(victimEmail).orElseThrow().getId();

        mockMvc.perform(post("/api/tickets")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"No puedo iniciar sesion","description":"Rechaza mis credenciales","priority":"LOW","userId":%d}
                                """.formatted(victimId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(customerId))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void agentShouldCreateTicketForAnotherUser() throws Exception {
        String token = tokenFor(createUser("agent", UserRole.AGENT));
        long customerId = userRepository.findByEmail(createUser("cliente", UserRole.CUSTOMER))
                .orElseThrow().getId();

        mockMvc.perform(post("/api/tickets")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Sin internet","description":"Se corta la conexion","userId":%d}
                                """.formatted(customerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(customerId));
    }


    @Test
    void customerShouldGetOwnTicketById() throws Exception {
        String token = tokenFor(createUser("reader", UserRole.CUSTOMER));
        long ticketId = createTicket(token);

        mockMvc.perform(get("/api/tickets/" + ticketId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.title").value("No puedo iniciar sesion"));
    }

    @Test
    void customerShouldNotGetAnotherCustomersTicket() throws Exception {
        String ownerToken = tokenFor(createUser("dueno", UserRole.CUSTOMER));
        long ticketId = createTicket(ownerToken);

        String otherToken = tokenFor(createUser("ajeno", UserRole.CUSTOMER));

        mockMvc.perform(get("/api/tickets/" + ticketId).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Acceso denegado"));
    }

    @Test
    void agentShouldGetAnyTicketById() throws Exception {
        String customerToken = tokenFor(createUser("cliente2", UserRole.CUSTOMER));
        long ticketId = createTicket(customerToken);

        String agentToken = tokenFor(createUser("soporte", UserRole.AGENT));

        mockMvc.perform(get("/api/tickets/" + ticketId).header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId));
    }

    @Test
    void shouldReturn404WhenTicketDoesNotExist() throws Exception {
        String token = tokenFor(createUser("buscador", UserRole.AGENT));

        mockMvc.perform(get("/api/tickets/999999").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket no encontrado"));
    }


    @Test
    void customerShouldOnlyListOwnTickets() throws Exception {
        String customerToken = tokenFor(createUser("listador", UserRole.CUSTOMER));
        createTicket(customerToken);

        String otherToken = tokenFor(createUser("listador-ajeno", UserRole.CUSTOMER));
        createTicket(otherToken);

        mockMvc.perform(get("/api/tickets").header(HttpHeaders.AUTHORIZATION, bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void customerShouldNotWidenListingWithUserIdParam() throws Exception {
        String customerToken = tokenFor(createUser("sondador", UserRole.CUSTOMER));
        createTicket(customerToken);

        String victimToken = tokenFor(createUser("sondado", UserRole.CUSTOMER));
        createTicket(victimToken);

        long victimId = userRepository.findByEmail("sondado@test.local").orElseThrow().getId();

        mockMvc.perform(get("/api/tickets").param("userId", String.valueOf(victimId))
                        .header(HttpHeaders.AUTHORIZATION, bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(
                        userRepository.findByEmail("sondador@test.local").orElseThrow().getId()));
    }

    @Test
    void agentShouldFilterListByStatusAndPriority() throws Exception {
        String agentToken = tokenFor(createUser("filtro", UserRole.AGENT));

        createTicket(agentToken, "LOW");
        createTicket(agentToken, "CRITICAL");

        mockMvc.perform(get("/api/tickets").param("status", "OPEN").param("priority", "CRITICAL")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].priority").value("CRITICAL"));
    }

    @Test
    void shouldBindQueryParamsIntoListTicketsRequest() throws Exception {
        String email = createUser("filtro-dto", UserRole.AGENT);
        long agentId = userRepository.findByEmail(email).orElseThrow().getId();
        String agentToken = tokenFor(email);

        createTicket(agentToken, "LOW");
        createTicket(agentToken, "CRITICAL");

        mockMvc.perform(get("/api/tickets").param("userId", String.valueOf(agentId))
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/tickets")
                        .param("userId", String.valueOf(agentId))
                        .param("status", "OPEN")
                        .param("priority", "CRITICAL")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].priority").value("CRITICAL"));

        mockMvc.perform(get("/api/tickets").header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    void shouldRejectUnknownStatusFilterWithBadRequest() throws Exception {
        String agentToken = tokenFor(createUser("filtro-malo", UserRole.AGENT));

        mockMvc.perform(get("/api/tickets").param("status", "NO_EXISTE")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNonNumericUserIdFilter() throws Exception {
        String agentToken = tokenFor(createUser("filtro-userid", UserRole.AGENT));

        mockMvc.perform(get("/api/tickets").param("userId", "no-es-un-numero")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void customerShouldNotUpdatePriority() throws Exception {
        String token = tokenFor(createUser("sin-permiso", UserRole.CUSTOMER));
        long ticketId = createTicket(token);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/priority")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority":"CRITICAL"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void agentShouldUpdatePriority() throws Exception {
        String customerToken = tokenFor(createUser("cliente3", UserRole.CUSTOMER));
        long ticketId = createTicket(customerToken);

        String agentToken = tokenFor(createUser("agente-prio", UserRole.AGENT));

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/priority")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority":"CRITICAL"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.priority").value("CRITICAL"));
    }

    @Test
    void adminShouldUpdatePriority() throws Exception {
        String customerToken = tokenFor(createUser("cliente4", UserRole.CUSTOMER));
        long ticketId = createTicket(customerToken);

        String adminToken = tokenFor(createUser("admin-prio", UserRole.ADMIN));

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/priority")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority":"HIGH"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldRejectUpdatePriorityWithoutBody() throws Exception {
        String agentToken = tokenFor(createUser("agente-body", UserRole.AGENT));
        long ticketId = createTicket(agentToken);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/priority")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.priority").exists());
    }

    @Test
    void shouldRejectUnknownPriorityValue() throws Exception {
        String agentToken = tokenFor(createUser("agente-prio-malo", UserRole.AGENT));
        long ticketId = createTicket(agentToken);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/priority")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority":"URGENTE"}
                                """))
                .andExpect(status().isBadRequest());
    }


    @Test
    void customerShouldNotStartTicketProgress() throws Exception {
        String token = tokenFor(createUser("sin-start", UserRole.CUSTOMER));
        long ticketId = createTicket(token);

        mockMvc.perform(post("/api/tickets/" + ticketId + "/start")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void agentShouldRunFullTicketLifecycle() throws Exception {
        String agentToken = tokenFor(createUser("agente-ciclo", UserRole.AGENT));
        long ticketId = createTicket(agentToken);

        mockMvc.perform(post("/api/tickets/" + ticketId + "/start")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(post("/api/tickets/" + ticketId + "/resolve")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));

        mockMvc.perform(post("/api/tickets/" + ticketId + "/close")
                        .header(HttpHeaders.AUTHORIZATION, bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }


    @Test
    void customerShouldNotCreateUsers() throws Exception {
        String token = tokenFor(createUser("registrador", UserRole.CUSTOMER));

        mockMvc.perform(post("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Intruso","email":"intruso@test.local","password":"%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldCreateUserWithCustomerRole() throws Exception {
        String token = tokenFor(createUser("admin-creador", UserRole.ADMIN));

        mockMvc.perform(post("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Nuevo","email":"nuevo@test.local","password":"%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void adminShouldPromoteCustomerToAgent() throws Exception {
        String targetEmail = createUser("promovible", UserRole.CUSTOMER);
        long targetId = userRepository.findByEmail(targetEmail).orElseThrow().getId();

        String token = tokenFor(createUser("admin-promotor", UserRole.ADMIN));

        mockMvc.perform(patch("/api/users/" + targetId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"AGENT"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(targetId))
                .andExpect(jsonPath("$.role").value("AGENT"));
    }

    @Test
    void agentShouldNotChangeRoles() throws Exception {
        long targetId = userRepository.findByEmail(createUser("intocable", UserRole.CUSTOMER))
                .orElseThrow().getId();

        String token = tokenFor(createUser("agente-roles", UserRole.AGENT));

        mockMvc.perform(patch("/api/users/" + targetId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"ADMIN"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldStorePasswordHashedNotInPlainText() {
        String email = createUser("hasheado", UserRole.CUSTOMER);

        String storedPassword = userRepository.findByEmail(email).orElseThrow().getPassword();

        assertNotEquals(PASSWORD, storedPassword, "El password nunca debe persistirse en texto plano");
        assertTrue(storedPassword.startsWith("$2"), "Se espera un hash BCrypt");
    }


    @Test
    void shouldExposeCreatedAtAndAnEmptyHistoryOnANewTicket() throws Exception {
        String token = tokenFor(createUser("nuevo-ticket", UserRole.CUSTOMER));
        long ticketId = createTicket(token);

        mockMvc.perform(get("/api/tickets/{id}", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.statusHistory").isArray())
                .andExpect(jsonPath("$.statusHistory").isEmpty());
    }

    @Test
    void shouldExposeTheWholeLifecycleInTheDetailEndpoint() throws Exception {
        String token = tokenFor(createUser("ciclo", UserRole.AGENT));
        long ticketId = createTicket(token);

        for (String step : new String[]{"start", "resolve", "close"}) {
            mockMvc.perform(post("/api/tickets/{id}/{step}", ticketId, step)
                            .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/tickets/{id}", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.statusHistory.length()").value(3))
                .andExpect(jsonPath("$.statusHistory[0].fromStatus").value("OPEN"))
                .andExpect(jsonPath("$.statusHistory[0].toStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.statusHistory[0].occurredAt").isNotEmpty())
                .andExpect(jsonPath("$.statusHistory[1].fromStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.statusHistory[1].toStatus").value("RESOLVED"))
                .andExpect(jsonPath("$.statusHistory[2].fromStatus").value("RESOLVED"))
                .andExpect(jsonPath("$.statusHistory[2].toStatus").value("CLOSED"));
    }

    @Test
    void shouldNotCarryTheHistoryInTheListEndpoint() throws Exception {
        String token = tokenFor(createUser("listado", UserRole.AGENT));
        createTicket(token);

        mockMvc.perform(get("/api/tickets")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].statusHistory").doesNotExist());
    }

    @Test
    void shouldReturnConflictWhenSkippingAStepOfTheLifecycle() throws Exception {
        String token = tokenFor(createUser("saltar-paso", UserRole.AGENT));
        long ticketId = createTicket(token);

        mockMvc.perform(post("/api/tickets/{id}/resolve", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Estado de ticket invalido"));
    }

    @Test
    void shouldReturnConflictWhenChangingPriorityOfAClosedTicket() throws Exception {
        String token = tokenFor(createUser("cerrado", UserRole.AGENT));
        long ticketId = createTicket(token);

        for (String step : new String[]{"start", "resolve", "close"}) {
            mockMvc.perform(post("/api/tickets/{id}/{step}", ticketId, step)
                            .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(patch("/api/tickets/{id}/priority", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority":"CRITICAL"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Estado de ticket invalido"));
    }

    @Test
    void shouldReturnBadRequestWhenPriorityIsMissing() throws Exception {
        String token = tokenFor(createUser("prioridad-nula", UserRole.AGENT));
        long ticketId = createTicket(token);

        mockMvc.perform(patch("/api/tickets/{id}/priority", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority":null}
                                """))
                .andExpect(status().isBadRequest());
    }

    private String createUser(String name, UserRole role) {
        String email = name + "@test.local";

        User user = new User(name, new Email(email), passwordEncoder.encode(PASSWORD));
        user.changeRole(role);

        return userRepository.save(user).getEmail().getValue();
    }

    private String tokenFor(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return tokenGenerator.generate(user.getId(), user.getEmail().getValue(), user.getRole());
    }

    private long createTicket(String token) throws Exception {
        return createTicket(token, "LOW");
    }

    private long createTicket(String token, String priority) throws Exception {

        String response = mockMvc.perform(post("/api/tickets")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"No puedo iniciar sesion","description":"Rechaza mis credenciales","priority":"%s"}
                                """.formatted(priority)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
