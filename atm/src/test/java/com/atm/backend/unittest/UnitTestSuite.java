package com.atm.backend.unittest;

import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.atm.backend.AccUserObj;
import com.atm.backend.Account;
import com.atm.backend.SQLQueries;
import com.atm.backend.User;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccUserObjTest.class,
        AuthenticateTest.class,
        SettingsTest.class,
        TransactionTest.class
})
public class UnitTestSuite {
    MockObjects mock;
    SQLQueries q;
    AccUserObj obj;
    Account acc;
    User user;

    Map<String, Method> protectedAccMethods;
    Map<String, Method> protectedUserMethods;

    UnitTestSuite() throws SQLException {
        this.mock = new MockObjects();

        when(mock.resultSet.next()).thenReturn(true).thenReturn(false);
        when(mock.resultSet.getLong("AccountNumber")).thenReturn(409000438620L);
        when(mock.resultSet.getString("FirstName")).thenReturn("Tia");
        when(mock.resultSet.getString("LastName")).thenReturn("Ackermann");
        when(mock.resultSet.getDouble("AvailableBalance")).thenReturn((double) 10000);
        when(mock.resultSet.getDouble("TotalBalance")).thenReturn((double) 150000);
        when(mock.resultSet.getDouble("TransferLimit")).thenReturn((double) 2000);
        when(mock.resultSet.getInt("IsAdmin")).thenReturn(0);

        this.q = new SQLQueries(mock.connection);
        this.obj = q.getAccountfromUsername("tia");
        this.acc = obj.getAccount();
        this.user = obj.getUser();
        this.protectedAccMethods = getProtectedAccMethods("account");
        this.protectedUserMethods = getProtectedAccMethods("user");
    }

    private Map<String, Method> getProtectedAccMethods(String typeClass) {
        Map<String, Method> protectedMethods = new HashMap<>();
        Method[] methods = null;

        if (typeClass == "account") {
            methods = Account.class.getDeclaredMethods();
        } else if (typeClass == "user") {
            methods = User.class.getDeclaredMethods();
        }

        methods = Arrays.stream(methods)
                .filter(method -> method.getName().startsWith("get"))
                .toArray(Method[]::new);

        for (Method method : methods) {
            method.setAccessible(true);
            protectedMethods.put(method.getName(), method);
            ;
        }

        return protectedMethods;
    }
}
