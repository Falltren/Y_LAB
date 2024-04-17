package org.fallt.audit;

/**
 * Вывод данных о действии пользователя в консоль
 */
public class AuditWriter {
    public void write(Audit audit) {
        System.out.println(audit);
    }
}
