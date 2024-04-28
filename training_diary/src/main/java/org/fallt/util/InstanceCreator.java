package org.fallt.util;

import lombok.Getter;
import org.fallt.repository.TrainingDao;
import org.fallt.repository.TrainingTypeDao;
import org.fallt.repository.UserDao;
import org.fallt.repository.impl.TrainingDaoImpl;
import org.fallt.repository.impl.TrainingTypeDaoImpl;
import org.fallt.repository.impl.UserDaoImpl;
import org.fallt.security.Authentication;
import org.fallt.security.Registration;
import org.fallt.service.TrainingService;
import org.fallt.service.UserService;

public class InstanceCreator {
    @Getter
    private static Registration registration;
    @Getter
    private static UserService userService;
    @Getter
    private static LiquibaseRunner liquibaseRunner;
    @Getter
    private static UserDao userDao;
    @Getter
    private static TrainingDao trainingDao;
    @Getter
    private static TrainingTypeDao trainingTypeDao;
    @Getter
    private static Authentication authentication;
    @Getter
    private static TrainingService trainingService;


    public static void injectDependency() {
        liquibaseRunner = new LiquibaseRunner();
        liquibaseRunner.run();
        userDao = new UserDaoImpl(DBUtils.getConnection());
        trainingDao = new TrainingDaoImpl(DBUtils.getConnection());
        trainingTypeDao = new TrainingTypeDaoImpl(DBUtils.getConnection());
        userService = new UserService(userDao);
        registration = new Registration(userService);
        authentication = new Authentication(userService);
        trainingService = new TrainingService(trainingDao, trainingTypeDao, userService);
    }

}
