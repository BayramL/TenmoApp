package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountDaoTest extends BaseDaoTests{

    private JdbcAccountDao sut;
    private JdbcUserDao userSut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
        userSut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void createAccount_creates_account() {
        userSut.create("Test", "123");
        User testUser = userSut.findByUsername("Test");
        boolean createdAccount = sut.createAccount(testUser);
        Assert.assertTrue(createdAccount);
    }

    @Test
    public void getAccount_returns_correct_account() {
        Account createdAccount = sut.getAccount(userSut.findByUsername("bob"));
        Account testAccount = new Account();
        testAccount.setUserId(1001);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setAccountId(2001);
        assertAccountsTheSame(testAccount, createdAccount);
    }

    private void assertAccountsTheSame(Account acc, Account acc2) {
        Assert.assertEquals(acc.getBalance(), acc2.getBalance());
        Assert.assertEquals(acc.getAccountId(), acc2.getAccountId());
        Assert.assertEquals(acc.getUserId(), acc2.getUserId());
    }
}
