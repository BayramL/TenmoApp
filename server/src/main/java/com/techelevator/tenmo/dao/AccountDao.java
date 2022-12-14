package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

public interface AccountDao {

    boolean createAccount(User user);

    Account getAccount(User user);
}
