package pl.kontomatik.challenge.navigator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.kontomatik.challenge.mapper.IpkoMapper;
import pl.kontomatik.challenge.mapper.IpkoMapperImpl;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IpkoNavigatorIT {
    private static IpkoNavigatorProperties ipkoNavigatorProperties;

    @BeforeAll
    public static void setup() throws IOException {
        ipkoNavigatorProperties = new IpkoNavigatorProperties();
    }

    @Test
    public void givenLoggingIn_whenCorrectCredentials_thenIsAuthenticated() throws IOException {
        // given
        IpkoMapper ipkoMapper = new IpkoMapperImpl();
        BankNavigator bankNavigator = new IpkoNavigator(ipkoMapper);

        String username = ipkoNavigatorProperties.getUsername();
        String password = ipkoNavigatorProperties.getPassword();

        bankNavigator.login(username, password);

        // when
        boolean authenticated = bankNavigator.isAuthenticated();

        // then
        assertTrue(authenticated);
    }

    @Test
    public void givenGettingAccounts_whenCorrectCredentials_thenReturnsAccounts() throws IOException {
        // given
        IpkoMapper ipkoMapper = new IpkoMapperImpl();
        BankNavigator bankNavigator = new IpkoNavigator(ipkoMapper);

        String username = ipkoNavigatorProperties.getUsername();
        String password = ipkoNavigatorProperties.getPassword();

        bankNavigator.login(username, password);

        Map<String, Double> expectedAccounts = ipkoNavigatorProperties.getAccounts();

        // when
        Map<String, Double> actualAccounts = bankNavigator.getAccounts();

        // then
        assertEquals(expectedAccounts, actualAccounts);
    }

}
