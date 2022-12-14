package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> list() {
        return userDao.findAll();
    }

    @RequestMapping(path = "available", method = RequestMethod.GET)
    public List<User> listAllBut(Principal principal) {
        return userDao.findAllBut(userDao.findByUsername(principal.getName()));
    }

}
