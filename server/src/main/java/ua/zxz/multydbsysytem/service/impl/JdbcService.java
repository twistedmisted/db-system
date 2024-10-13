package ua.zxz.multydbsysytem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JdbcService {

    private final JdbcTemplate jdbcTemplate;

    public void batchUpdate(Long dbId, String sql) {
        try {
            jdbcTemplate.batchUpdate(
                    "USE db_" + dbId + ";",
                    sql,
                    "USE \"USER\";"
            );
        } catch (Exception e) {
            rollbackNamespace();
            throw e;
        }
    }

    public void batchUpdate(Long dbId, List<String> sqls) {
        StringBuilder sql = new StringBuilder();
        for (String sqlStr : sqls) {
            sql.append(sqlStr);
        }
        batchUpdate(dbId, sql.toString());
    }

    public void rollbackNamespace() {
        jdbcTemplate.update("USE \"USER\";");
    }
}
