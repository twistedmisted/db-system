package ua.zxz.multydbsysytem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

@Data
@NoArgsConstructor
public class DbTokenDto {

    private Long dbId;
    private String token;
    private DbTokenLifeTime lifeTime;
    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private Timestamp createdAt;

    public DbTokenDto(DbTokenLifeTime lifeTime) {
        this.lifeTime = lifeTime;
    }

    @JsonProperty("expirationDate")
    public String getExpirationDate() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt.getTime()), TimeZone.getDefault().toZoneId())
                .plus(lifeTime.getLifeTime(), ChronoUnit.MILLIS)
                .format(DateTimeFormatter.ofPattern("hh:mm:ss dd-MM-yyyy"));
    }
}
