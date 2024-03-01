package heavenboards.board.service.board.integration;

import feign.FeignException;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import security.service.util.test.SecurityTestUtil;
import transfer.contract.api.ProjectApi;
import transfer.contract.api.UserApi;
import transfer.contract.domain.board.BoardOperationErrorCode;
import transfer.contract.domain.board.BoardOperationResultTo;
import transfer.contract.domain.board.BoardTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.project.ProjectTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.UUID;

/**
 * Тест создания доски.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
public class BoardCreateIntegrationTest {
    /**
     * Utility-класс с настройкой security для тестов.
     */
    @Autowired
    private SecurityTestUtil securityTestUtil;

    /**
     * Mock api-клиента для сервиса пользователей.
     */
    @MockBean
    private UserApi userApi;

    /**
     * Mock api-клиента для сервиса проектов.
     */
    @MockBean
    private ProjectApi projectApi;

    /**
     * Порт приложения.
     */
    @LocalServerPort
    private int port;

    /**
     * Конфигурация перед тестами.
     */
    @BeforeAll
    public void init() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1";
        RestAssured.defaultParser = Parser.JSON;
    }

    /**
     * Тест валидного создания доски.
     */
    @Test
    @DisplayName("Тест валидного создания доски")
    public void validBoardCreateTest() {
        securityTestUtil.securityContextHelper();

        ProjectTo project = ProjectTo.builder()
            .id(UUID.randomUUID())
            .name("Existing project")
            .build();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());
        Mockito.when(projectApi.findProjectById(Mockito.any()))
            .thenReturn(project);

        Response response = createBoardAndGetResponse(project);
        BoardOperationResultTo operationResult = response
            .getBody()
            .as(BoardOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(OperationStatus.OK, operationResult.getStatus());
        Assertions.assertNotNull(operationResult.getBoardId());
    }

    /**
     * Тест валидного создания доски.
     */
    @Test
    @DisplayName("Тест создания доски с несуществующим проектом")
    public void projectNonExistCreateTest() {
        securityTestUtil.securityContextHelper();

        ProjectTo project = ProjectTo.builder()
            .id(UUID.randomUUID())
            .name("Not existing project")
            .build();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());
        Mockito.when(projectApi.findProjectById(Mockito.any()))
            .thenThrow(FeignException.FeignServerException.class);

        Response response = createBoardAndGetResponse(project);
        ClientApplicationException applicationException = response
            .getBody()
            .as(ClientApplicationException.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        Assertions.assertEquals(BaseErrorCode.NOT_FOUND, applicationException.getErrorCode());
    }

    /**
     * Тест создания доски с уже существующим названием.
     */
    @Test
    @DisplayName("Тест создания доски с уже существующим названием")
    public void existingBoardNameCreateTest() {
        securityTestUtil.securityContextHelper();

        ProjectTo project = ProjectTo.builder()
            .id(UUID.randomUUID())
            .name("Existing project")
            .build();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());
        Mockito.when(projectApi.findProjectById(Mockito.any()))
            .thenReturn(project);

        Response successResponse = createBoardAndGetResponse(project);
        BoardOperationResultTo successOperationResult = successResponse
            .getBody()
            .as(BoardOperationResultTo.class);

        Response errorResponse = createBoardAndGetResponse(project);
        BoardOperationResultTo errorOperationResult = errorResponse
            .getBody()
            .as(BoardOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), errorResponse.getStatusCode());
        Assertions.assertEquals(OperationStatus.FAILED, errorOperationResult.getStatus());
        Assertions.assertEquals(List.of(BoardOperationResultTo.BoardOperationErrorTo.builder()
            .errorCode(BoardOperationErrorCode.BOARD_NAME_ALREADY_EXIST_IN_PROJECT)
            .failedBoardId(successOperationResult.getBoardId())
            .build()), errorOperationResult.getErrors());
    }

    /**
     * Оправить запрос на создание доски и получить ответ.
     *
     * @param project - проект
     * @return ответ
     */
    private Response createBoardAndGetResponse(ProjectTo project) {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .body(BoardTo.builder()
                .name("Board name")
                .project(project)
                .build())
            .when()
            .post("/board");
    }
}
