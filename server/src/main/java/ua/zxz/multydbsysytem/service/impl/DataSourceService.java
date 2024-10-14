package ua.zxz.multydbsysytem.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Setter
@Service
public class DataSourceService {

    private static final Cache<String, DataSource> DATA_SOURCE_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .maximumSize(3)
            .build();

    @Value("${db.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

//    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize = 1;

    // username_databaseName == dbName (in namespaces)
    // db_id
    public JdbcTemplate getJdbcTemplateByDb(Long dbId) {
        return new JdbcTemplate(getDataSource("db_" + dbId));
    }

    private DataSource getDataSource(String dbName) {
        return DATA_SOURCE_CACHE.get(dbName, key -> {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(getUrl(dbName));
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setMaximumPoolSize(maximumPoolSize);
            dataSource.setIdleTimeout(600000); // 10 minutes
            dataSource.setMaxLifetime(1800000); // 30 minutes
            return dataSource;
        });
    }

    private String getUrl(String dbName) {
        return dbUrl + dbName;
    }
}
