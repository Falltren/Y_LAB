package org.fallt.audit;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Аудит действий пользователя. Фиксирует информацию о регистрации, авторизации, добавлении новых пользователей в хранилище,
 * а также добавление, удаление, редактирование тренировок
 */
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
