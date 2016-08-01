/**
 * Copyright 2013 Simeon Malchev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vibur.dbcp.integration;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.vibur.dbcp.ViburDBCPDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>A connection provider for Hibernate org.vibur.dbcp.integration.
 *
 * <p>To use this connection provider set:<br>
 * <code>hibernate.connection.provider_class&nbsp;org.vibur.dbcp.org.vibur.dbcp.integration.ViburDBCPConnectionProvider</code>
 *
 * <pre>
 * Supported Hibernate properties:<br>
 *   hibernate.connection.driver_class
 *   hibernate.connection.url
 *   hibernate.connection.username
 *   hibernate.connection.password
 *   hibernate.connection.isolation
 *   hibernate.connection.autocommit
 * </pre>
 *
 * All {@link org.vibur.dbcp.ViburConfig} properties are also supported via using the
 * {@code hibernate.vibur} prefix.
 *
 * @see ConnectionProvider
 *
 * @author Simeon Malchev
 */
public class ViburDBCPConnectionProvider implements ConnectionProvider {

    private static final String VIBUR_PREFIX = "hibernate.vibur.";

    private ViburDBCPDataSource dataSource = null;

    @Override
    public void configure(Properties props) throws HibernateException {
        dataSource = new ViburDBCPDataSource(transform(props));
        dataSource.start();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    @Override
    public void close() throws HibernateException {
        if (dataSource != null) {
            dataSource.terminate();
            dataSource = null;
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    private Properties transform(Properties props) {
        Properties result  = new Properties();

        String driverClassName = props.getProperty(Environment.DRIVER);
        if (driverClassName != null)
            result.setProperty("driverClassName", driverClassName);
        String jdbcUrl = props.getProperty(Environment.URL);
        if (jdbcUrl != null)
            result.setProperty("jdbcUrl", jdbcUrl);

        String username = props.getProperty(Environment.USER);
        if (username != null)
            result.setProperty("username", username);
        String password = props.getProperty(Environment.PASS);
        if (password != null)
            result.setProperty("password", password);

        String defaultTransactionIsolationValue = props.getProperty(Environment.ISOLATION);
        if (defaultTransactionIsolationValue != null)
            result.setProperty("defaultTransactionIsolationValue", defaultTransactionIsolationValue);
        String defaultAutoCommit = props.getProperty(Environment.AUTOCOMMIT);
        if (defaultAutoCommit != null)
            result.setProperty("defaultAutoCommit", defaultAutoCommit);

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (key.startsWith(VIBUR_PREFIX)) {
                key = key.substring(VIBUR_PREFIX.length());
                result.setProperty(key, value);
            }
        }

        return result;
    }

    public ViburDBCPDataSource getDataSource() {
        return dataSource;
    }
}
