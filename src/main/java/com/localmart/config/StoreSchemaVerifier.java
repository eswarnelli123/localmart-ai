package com.localmart.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class StoreSchemaVerifier implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StoreSchemaVerifier.class);
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public StoreSchemaVerifier(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            if (!tableExists(connection, "store")) {
                logger.warn("Store table does not exist; skipping store schema verification.");
                return;
            }

            ensureColumn(connection, "store", "latitude", "DOUBLE NULL");
            ensureColumn(connection, "store", "longitude", "DOUBLE NULL");
        } catch (Exception ex) {
            logger.warn("Failed to verify or update store schema at startup. Continuing without schema changes.", ex);
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        try (ResultSet tables = metadata.getTables(connection.getCatalog(), connection.getSchema(), tableName, null)) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                if (name != null && name.equalsIgnoreCase(tableName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        Set<String> columns = new HashSet<>();
        try (ResultSet rs = metadata.getColumns(connection.getCatalog(), connection.getSchema(), tableName, null)) {
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                if (name != null) {
                    columns.add(name.toLowerCase(Locale.ROOT));
                }
            }
        }
        return columns.contains(columnName.toLowerCase(Locale.ROOT));
    }

    private void ensureColumn(Connection connection, String tableName, String columnName, String columnDefinition) {
        try {
            if (!columnExists(connection, tableName, columnName)) {
                String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnName, columnDefinition);
                logger.info("Applying missing schema change: {}", sql);
                jdbcTemplate.execute(sql);
            }
        } catch (Exception ex) {
            logger.warn("Unable to create missing column {} on table {}", columnName, tableName, ex);
        }
    }
}
