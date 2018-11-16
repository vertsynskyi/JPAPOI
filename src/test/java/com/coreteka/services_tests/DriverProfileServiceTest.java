package com.coreteka.services_tests;

import com.coreteka.entities.Authorities;
import com.coreteka.entities.DriverProfile;
import com.coreteka.entities.User;
import com.coreteka.service.AuthoritiesService;
import com.coreteka.service.DriverProfileService;
import com.coreteka.service.impl.AuthoritiesServiceImpl;
import com.coreteka.service.impl.DriverProfileServiceImpl;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class DriverProfileServiceTest {

    @Test
    public void createDriverProfile() {
        DriverProfileService driverProfileService = new DriverProfileServiceImpl();
        AuthoritiesService authoritiesService = new AuthoritiesServiceImpl();

        //Get token for all new driverProfile properties to be unique
        long token = System.currentTimeMillis();

        //Get authorities from database for new driverProfile's user
        Authorities authority = authoritiesService.getByName("ROLE_DRIVER", null);
        Set<Authorities> authoritiesSet = new HashSet<>();
        authoritiesSet.add(authority);

        //Create user for new driverProfile
        User user = new User();
        user.setPassword("qwerty");
        user.setUsername("userName" + token);
        user.setUserStatus(true);
        user.setAuthorities(authoritiesSet);

        //Create new driverProfile entity
        DriverProfile driverProfile = new DriverProfile();
        driverProfile.setFullName("Single");
        driverProfile.setPhone(String.valueOf(token));
        driverProfile.setUser(user);
        DriverProfile createdDriverProfile = driverProfileService.create(driverProfile, null);

        assertThat(createdDriverProfile.getId(), notNullValue());
    }

    @Test
    public void createDriverProfilesUsingDifferentServiceInstances(){

        //Insert 5 driverProfile entries into DB using different instances of DriverProfileService
        for (int i = 0; i < 5; i++){
            createDriverProfile();
        }
    }

    @Test
    public void createDriverProfilesUsingTheSameServiceInstance() {
        DriverProfileService driverProfileService = new DriverProfileServiceImpl();
        AuthoritiesService authoritiesService = new AuthoritiesServiceImpl();

        DriverProfile createdDriverProfile = null;

        //Insert 5 driverProfile entries into DB using the same instance of DriverProfileService
        for(int i = 0; i < 5; i++) {
            long token = System.currentTimeMillis();

            //Get authorities from database for new driverProfile's user
            Authorities authority = authoritiesService.getByName("ROLE_DRIVER", null);
            Set<Authorities> authoritiesSet = new HashSet<>();
            authoritiesSet.add(authority);


            //Create user for new driverProfile
            User user = new User();
            user.setPassword("qwerty");
            user.setUsername("userName" + token);
            user.setUserStatus(true);
            user.setAuthorities(authoritiesSet);

            //Create new driverProfile entity
            DriverProfile driverProfile = new DriverProfile();
            driverProfile.setFullName("FOR");
            driverProfile.setPhone(String.valueOf(token));
            driverProfile.setUser(user);

            createdDriverProfile = driverProfileService.create(driverProfile, null);
        }
        assertThat(createdDriverProfile.getId(), notNullValue());
    }

    @Test
    public void createDriverProfilesFromFile() throws IOException, InvalidFormatException {

        File file = new File("/home/kostique/IdeaProjects/JPAPOI/user_report.xlsx");

        DriverProfileService driverProfileService = new DriverProfileServiceImpl();

        driverProfileService.create(file, null);
    }


    @Test
    public void getDriverProfileById() {
        long driverProfileId = 26L;
        DriverProfileService driverProfileService = new DriverProfileServiceImpl();
        DriverProfile driverProfile = driverProfileService.getById(driverProfileId);
        assertThat(driverProfile.getId(), equalTo(26L));
    }

    @Test
    public void getDriverProfileList() {
        DriverProfileService driverProfileService = new DriverProfileServiceImpl();
        List<DriverProfile> driverProfiles = driverProfileService.getDriverProfiles();
        assertThat(driverProfiles.isEmpty(), is(false));
    }


    @Test
    public void updateDriverProfile() {
        Authorities authority = new Authorities();
        authority.setName("ROLE_DRIVER");
        Set<Authorities> authoritiesSet = new HashSet<>();
        authoritiesSet.add(authority);

        User user = new User();
        user.setUsername("Bortic");
        user.setUserStatus(true);
        user.setAuthorities(authoritiesSet);

        String newDriverProfileFullName = "FUHRER";
        String newDriverProfilePhone = "105";

        DriverProfile driverProfile = new DriverProfile();
        driverProfile.setFullName(newDriverProfileFullName);
        driverProfile.setPhone(newDriverProfilePhone);
        driverProfile.setUser(user);

        DriverProfileService driverProfileService = new DriverProfileServiceImpl();
        driverProfile = driverProfileService.update(driverProfile, null);
        assertThat(driverProfile.getFullName(), equalTo(newDriverProfileFullName));
        assertThat(driverProfile.getPhone(), equalTo(newDriverProfilePhone));
    }
}
