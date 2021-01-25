package pl.kontomatik.challenge.client.ipko.mapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.kontomatik.challenge.client.ipko.dto.AuthRequest;
import pl.kontomatik.challenge.client.ipko.dto.AuthResponse;
import pl.kontomatik.challenge.client.ipko.dto.BaseRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpBodyMapperTest {

  private static final String SESSION_TOKEN_HEADER = "X-Session-Id";
  private static final String SESSION_TOKEN = "session_token";
  private static final String FLOW_ID = "flow_id";
  private static final String FLOW_TOKEN = "token";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  private final HttpBodyMapper mapper = new HttpBodyMapper();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public HttpBodyMapperTest() {
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Test
  public void mapsAuthResponseFromJson() {
    boolean hasErrors = false;
    AuthResponse expectedResponse = new AuthResponse(SESSION_TOKEN, FLOW_ID, FLOW_TOKEN, hasErrors);
    String loginResponse = "{\"flow_id\":\"flow_id\",\"token\":\"token\",\"finished\":true}";
    AuthResponse actualResponse = mapper.getAuthResponseFrom(Map.of(SESSION_TOKEN_HEADER, SESSION_TOKEN), loginResponse);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void mapsAuthResponseWithErrorOnGeneralError() {
    boolean hasErrors = true;
    AuthResponse expectedResponse = new AuthResponse(SESSION_TOKEN, FLOW_ID, FLOW_TOKEN, hasErrors);
    String errorResponse = "{\"response\":{\"flow_id\":\"flow_id\",\"token\":\"token\",\"fields\":{\"errors\":{}}}}";
    AuthResponse actualResponse = mapper.getAuthResponseFrom(Map.of(SESSION_TOKEN_HEADER, SESSION_TOKEN), errorResponse);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void mapsAuthResponseWithErrorOnCredentialError() {
    boolean hasErrors = true;
    AuthResponse expectedResponse = new AuthResponse(SESSION_TOKEN, FLOW_ID, FLOW_TOKEN, hasErrors);
    String errorResponse = "{\"response\":" +
      "{\"flow_id\":\"flow_id\",\"token\":\"token\",\"fields\":" +
      "{\"login\":{\"errors\":{}},\"password\":{\"errors\":{}}}" +
      "}" +
      "}";
    AuthResponse actualResponse = mapper.getAuthResponseFrom(Map.of(SESSION_TOKEN_HEADER, SESSION_TOKEN), errorResponse);
    assertEquals(expectedResponse, actualResponse);
  }


  @Test
  public void mapsJsonFromAuthRequest() throws JsonProcessingException {
    AuthRequest expectedRequest = AuthRequest.authBuilder()
      .setStateId("login")
      .putData("login", USERNAME)
      .build();
    String expectedRequestJson = objectMapper.writeValueAsString(expectedRequest);
    String actualRequestJson = mapper.getAuthRequestBodyFor(USERNAME);
    assertEquals(expectedRequestJson, actualRequestJson);
  }

  @Test
  public void mapsJsonFromSessionAuthRequest() throws JsonProcessingException {
    AuthRequest expectedRequest = AuthRequest.authBuilder()
      .setStateId("password")
      .setFlowId(FLOW_ID)
      .setToken(FLOW_TOKEN)
      .putData("password", PASSWORD)
      .build();
    String expectedJsonBody = objectMapper.writeValueAsString(expectedRequest);
    String actualJsonBody = mapper.getSessionAuthRequestBodyFor(FLOW_ID, FLOW_TOKEN, PASSWORD);
    assertEquals(expectedJsonBody, actualJsonBody);
  }

  @Test
  public void mapsJsonFromBaseRequest() throws JsonProcessingException {
    BaseRequest accountsRequest = BaseRequest.accountsRequest();
    String expectedJsonBody = objectMapper.writeValueAsString(accountsRequest);
    String actualJsonBody = mapper.accountsRequestBody();
    assertEquals(expectedJsonBody, actualJsonBody);
  }

  @Test
  public void mapsAccountsFromJson() {
    Double balance = 0.5;
    String accountNumber = "123456789";
    Map<String, Double> expectedAccounts = Map.of(accountNumber, balance);
    String jsonAccounts = "{\"accounts\":{\"acc1\":{\"number\":{\"value\":\"123456789\"},\"balance\":0.5}}}";
    Map<String, Double> actualAccounts = mapper.getAccountsFromJson(jsonAccounts);
    assertEquals(expectedAccounts, actualAccounts);
  }

}