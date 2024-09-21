package ua.zxz.multydbsysytem.dto;

import lombok.Getter;

@Getter
public enum DbTokenLifeTime {
    DAY(1000L * 60 * 60 * 24),
    WEEK(1000L * 60 * 60 * 24 * 7),
    MONTH(1000L * 60 * 60 * 24 * 30),
    YEAR(1000L * 60 * 60 * 24 * 365),;

    private final long lifeTime;

    DbTokenLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }
}
