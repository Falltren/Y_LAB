package org.fallt.audit;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class Audit {
    private final String userName;
    private final String action;
    private final LocalDateTime timestamp;

    public Audit(String userName, String action) {
        this.userName = userName;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "userId='" + userName + '\'' +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
