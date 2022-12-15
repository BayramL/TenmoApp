package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transfer> listTransfers(User user) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_date, transfer_amount, transfer_type, transfer_status " +
                "FROM transfer WHERE sender_id = ? OR receiver_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId(), user.getId());
        while (results.next()) {
            list.add(mapToTransfer(results));
        }
        return list;
    }

    public Transfer getTransferById(User user, int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_date, transfer_amount, transfer_type, transfer_status " +
                "FROM transfer WHERE (sender_id = ? OR receiver_id = ?) AND transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId(), user.getId(), transferId);
        if (results.next()) {
            transfer = mapToTransfer(results);
        }
        return transfer;
    }

    public boolean createSend(@Valid User user, int receiverId, BigDecimal transferAmount) {
        // First check if they have enough money to send
        // Pull account balance from database
        String sql1 = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql1, user.getId());
        if (result.next()) {
            BigDecimal balance = result.getBigDecimal("balance");
            if (balance.compareTo(transferAmount) >= 0) {
                // add the money and stuff...
                String removeMoneyFromSender = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
                jdbcTemplate.update(removeMoneyFromSender, transferAmount, user.getId());
                String addMoneyToReceiver = "UPDATE account SET balance = balance + ? WHERE user_id = ?";
                jdbcTemplate.update(addMoneyToReceiver, transferAmount, receiverId);
            }
            else {
                return false;
            }
        }
        String sql = "INSERT INTO transfer (sender_id, receiver_id, transfer_date, transfer_amount, transfer_type, transfer_status) " +
                "VALUES (?, ?, NOW(), ?, 'Send', 'Approved')";
        return jdbcTemplate.update(sql, user.getId(), receiverId, transferAmount) == 1;
    }

    public boolean createRequest(@Valid User user, int senderId, BigDecimal transferAmount) {
        String sql = "INSERT INTO transfer (sender_id, receiver_id, transfer_date, transfer_amount, transfer_type, transfer_status) " +
                "VALUES (?, ?, NOW(), ?, 'Request', 'Pending')";
        return jdbcTemplate.update(sql, senderId, user.getId(), transferAmount) == 1;
    }

    public List<Transfer> getPendingTransfers(User user) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_date, transfer_amount, transfer_type, transfer_status " +
                     "FROM transfer WHERE sender_id = ? AND transfer_status = 'Pending'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId());
        while (results.next()) {
            list.add(mapToTransfer(results));
        }
        return list;
    }

    public boolean acceptRequest(User user, int transactionId) {
        String sql1 = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql1, user.getId());
        String sql2 = "SELECT transfer_amount, receiver_id FROM transfer WHERE transfer_id = ?";
        SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql2, transactionId);
        if (result.next() && result2.next()) {
            BigDecimal balance = result.getBigDecimal("balance");
            BigDecimal transferAmount = result2.getBigDecimal("transfer_amount");
            int receiverId = result2.getInt("receiver_id");
            if (balance.compareTo(transferAmount) >= 0) {
                // add the money and stuff...
                String removeMoneyFromSender = "BEGIN TRANSACTION; UPDATE account SET balance = balance - ? WHERE user_id = ?; " +
                        "UPDATE account SET balance = balance + ? WHERE user_id = ?; " +
                        "UPDATE transfer SET transfer_status = 'Approved' WHERE transfer_id = ?; COMMIT;";
                jdbcTemplate.update(removeMoneyFromSender, transferAmount, user.getId(), transferAmount, receiverId, transactionId);
//                String addMoneyToReceiver = "UPDATE account SET balance = balance + ? WHERE user_id = ?";
//                jdbcTemplate.update(addMoneyToReceiver, transferAmount, receiverId);
//                String lastSql = "UPDATE transfer SET transfer_status = 'Approved' WHERE transfer_id = ?";
//                jdbcTemplate.update(lastSql, transactionId);
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void rejectRequest(User user, int transactionId) {
        String sql = "UPDATE transfer SET transfer_status = 'Rejected' WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transactionId);
    }

    public Transfer mapToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setReceiverId(result.getInt("receiver_id"));
        transfer.setSenderId(result.getInt("sender_id"));
        transfer.setTransferId(result.getInt("transfer_id"));
        transfer.setTransferAmount(result.getBigDecimal("transfer_amount"));
        transfer.setTransferDate(result.getDate("transfer_date").toLocalDate());
        transfer.setTransferType(result.getString("transfer_type"));
        transfer.setTransferStatus(result.getString("transfer_status"));
        return transfer;
    }
}
