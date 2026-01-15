package com.kote.banking.security;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class TenantConnectionProvider implements MultiTenantConnectionProvider<String> {
    private final DataSource dataSource;

    public TenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        if (tenantIdentifier != null && tenantIdentifier.matches("^[a-zA-Z0-9_]+$")) {
            connection.createStatement().execute("SET search_path TO " + tenantIdentifier);
        } else {
            connection.createStatement().execute("SET search_path TO public");
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.createStatement().execute("SET search_path TO public");
        } catch (SQLException e) {
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() { return false; }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) { return false; }

    @Override
    public <T> T unwrap(Class<T> unwrapType) { return null; }
}