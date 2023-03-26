package com.atm.backend.unittest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.*;

class MockObjects {
    protected Connection connection;
    protected Statement statement;
    protected PreparedStatement preStatement;
    protected ResultSet resultSet;

    public MockObjects() throws SQLException {
        this.connection = mock(Connection.class);
        this.statement = mock(Statement.class);
        this.preStatement = mock(PreparedStatement.class);
        this.resultSet = mock(ResultSet.class);

        // ---- No fields preparation ---- //
        // when the Connection.createStatement method is called with
        // return the mock Statement object
        when(connection.createStatement()).thenReturn(statement);
        // when the Statement.executeQuery method is called with
        // return the mock ResultSet object
        when(statement.executeQuery(any(String.class))).thenReturn(resultSet);

        // ---- With fields preparation ---- //
        // When the Connection.prepareStatement method is called with any
        // string argument, return the mock PreparedStatement object
        when(connection.prepareStatement(any(String.class))).thenReturn(preStatement);
    }
}
