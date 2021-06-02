package com.razzzil.sphynx.coordinator.tests.service;

import com.razzzil.sphynx.coordinator.app.CoordinatorApplication;
import com.razzzil.sphynx.coordinator.repository.IterationRepository;
import com.razzzil.sphynx.coordinator.repository.TestRepository;
import com.razzzil.sphynx.coordinator.repository.UserRepository;
import com.razzzil.sphynx.coordinator.service.AuthUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = CoordinatorApplication.class)
@RunWith(SpringRunner.class)
public class MainServiceTests {


    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IterationRepository iterationRepository;

    @Autowired
    private TestRepository testRepository;

    @Test
    public void test(){
        //mainService.testDbInteraction();
       // String loginOrEmail = "ruKBaIXmOr";

//        LoginForm loginForm = LoginForm.builder()
//                .loginOrEmail("ruKBaIXmOr")
//                .password("passwpord")
//                .build();
//
//        AuthSuccessForm form = authService.login(loginForm);
//
//        int i = 0;
//
//        Optional<UserModel> userModel = userRepository.getById(1);
//        int i = 0;





    }


}
