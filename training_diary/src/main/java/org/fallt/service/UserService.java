package org.fallt.service;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.User;
import org.fallt.repository.UserBase;

import java.util.List;

public class UserService {

    private UserBase userBase;
    private AuditWriter auditWriter;

    public UserService(UserBase userBase) {
        this.userBase = userBase;
        auditWriter = new AuditWriter();
    }

    public User getUserByName(String name) {
        auditWriter.write(new Audit(name, "get user"));
        return userBase.getUserByName(name).orElse(null);
    }

    public List<User> getAllUsers() {
        return userBase.getAllUser();
    }

    public void addUser(User user) {
        userBase.addUser(user);
        auditWriter.write(new Audit(user.getName(), "save user"));
    }

}
