package pl.kontomatik.challenge.navigator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.client.MockServerClient;
import pl.kontomatik.challenge.exception.InvalidCredentials;
import pl.kontomatik.challenge.exception.NotAuthenticated;
import pl.kontomatik.challenge.mapper.IpkoMapperImpl;
import pl.kontomatik.challenge.mockserver.MockNavigatorServer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpkoNavigatorTest extends MockNavigatorServer {
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String WRONG_USERNAME = "WRONG_USERNAME";
    private static final String WRONG_PASSWORD = "WRONG_PASSWORD";
    private static final String ACCOUNT_NUMBER = "123456789";
    private static final double ACCOUNT_BALANCE = 0.5;

    @BeforeAll
    public static void setupMocks(MockServerClient mockServerClient) {
        setupMockedServer(mockServerClient);
    }

    @Test
    public void signInSucceedsOnValidCredentials() {
        // given
        BankNavigator bankNavigator = getProxiedNavigator();

        // when/then
        assertDoesNotThrow(() -> bankNavigator.login(USERNAME, PASSWORD));
    }

    @Test
    public void signInFailsOnInvalidCredentials() {
        // given
        BankNavigator bankNavigator = getProxiedNavigator();

        // when/then
        assertThrows(InvalidCredentials.class, () -> bankNavigator.login(WRONG_USERNAME, WRONG_PASSWORD));
    }

    @Test
    public void afterSignInCanFetchAccounts() {
        // given
        BankNavigator bankNavigator = getProxiedNavigator();

        Map<String, Double> expectedAccounts = Map.of(ACCOUNT_NUMBER, ACCOUNT_BALANCE);

        bankNavigator.login(USERNAME, PASSWORD);

        // when
        Map<String, Double> accounts = bankNavigator.getAccounts();

        // then
        assertEquals(expectedAccounts, accounts);
    }

    @Test
    public void accountFetchingFailsWhenNotAuthenticated() {
        // given
        BankNavigator bankNavigator = getProxiedNavigator();

        // when/then
        assertThrows(NotAuthenticated.class, bankNavigator::getAccounts);
    }

    private BankNavigator getProxiedNavigator() {
        return new IpkoNavigator(new IpkoMapperImpl(), proxy);
    }

}
