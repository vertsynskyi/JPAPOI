package com.coreteka.service.impl;

import com.coreteka.dao.*;
import com.coreteka.dao.impl.*;
import com.coreteka.entities.*;
import com.coreteka.exceptions.DriverProfileExceptions.DriverProfileNotFoundException;
import com.coreteka.service.*;
import com.coreteka.util.PersistenceUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DriverProfileServiceImpl implements DriverProfileService {

    @Override
    public DriverProfile create(DriverProfile driverProfile) {
        DriverProfileDAO driverProfileDAO = new DriverProfileDAOImpl();

        EntityManager entityManager = PersistenceUtil.getEntityManager();
        entityManager.getTransaction().begin();

        setDriverProfileProperties(driverProfile);
        DriverProfile createdDriverProfile = driverProfileDAO.create(driverProfile);

        entityManager.getTransaction().commit();
        entityManager.close();

        return createdDriverProfile;
    }


    private void setDriverProfileProperties(DriverProfile driverProfile){
        UserService userService = new UserServiceImpl();
        AuthoritiesService authoritiesService = new AuthoritiesServiceImpl();

        Authorities authority = authoritiesService.getByName("ROLE_DRIVER");
        Set<Authorities> authoritiesSet = new HashSet<>();
        authoritiesSet.add(authority);

        User user = driverProfile.getUser();
        user.setAuthorities(authoritiesSet);

        User createdUser = userService.create(user);
        driverProfile.setUser(createdUser);
    }


    @Override
    public void create(File file) throws IOException, InvalidFormatException {
        ExcelParserService excelParserService = new ExcelParserServiceImpl();
        List<DriverProfile> driverProfiles = excelParserService.parse(file);
        Iterator<DriverProfile> iterator = driverProfiles.iterator();
        DriverProfileDAO driverProfileDAO = new DriverProfileDAOImpl();

        EntityManager entityManager = PersistenceUtil.getEntityManager();
        entityManager.getTransaction().begin();

        while (iterator.hasNext()) {
            DriverProfile driverProfile = iterator.next();
            setDriverProfileProperties(driverProfile);
            driverProfileDAO.create(driverProfile);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public DriverProfile getById(long id) {
        DriverProfileDAO driverProfileDAO = new DriverProfileDAOImpl();

        EntityManager entityManager = PersistenceUtil.getEntityManager();
        entityManager.getTransaction().begin();

        DriverProfile driverProfile = driverProfileDAO.getById(id);

        entityManager.getTransaction().commit();
        entityManager.close();

        return driverProfile;
    }

    @Override
    public DriverProfile update(DriverProfile driverProfile) {
        UserDAO userDAO = new UserDAOImpl();
        String userName = driverProfile.getUser().getUsername();

        EntityManager entityManager = PersistenceUtil.getEntityManager();
        entityManager.getTransaction().begin();

        User existingUser = userDAO.getByUserName(userName);

        if (null == existingUser) {
            throw new DriverProfileNotFoundException("Driver profile with user=" + userName + " not found.");
        }

        DriverProfileDAO driverProfileDAO = new DriverProfileDAOImpl();

        DriverProfile existingDriverProfile = driverProfileDAO.getById(existingUser.getDriverProfile().getId());

        existingDriverProfile.setPhone(driverProfile.getPhone());
        existingDriverProfile.setFullName(driverProfile.getFullName());

        DriverProfile updatedDriverProfile = driverProfileDAO.update(existingDriverProfile);

        entityManager.getTransaction().commit();
        entityManager.close();

        return updatedDriverProfile;
    }
}