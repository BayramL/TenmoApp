package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean createAccount(User user) {
        String sql = "INSERT INTO account (user_id, balance) " +
                "VALUES (?, 1000.0)";
        return jdbcTemplate.update(sql, user.getId()) == 1;
    }

    public Account getAccount(User user) {
        Account account = null;
        String sql = "SELECT balance, account_id, user_id FROM account WHERE user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user.getId());
        if (result.next()) {
            account = mapToAccount(result);
        }

        return account;
    }

    public Account mapToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccountId(result.getInt("account_id"));
        account.setBalance(result.getBigDecimal("balance"));
        account.setUserId(result.getInt("user_id"));
        return account;
    }
}
