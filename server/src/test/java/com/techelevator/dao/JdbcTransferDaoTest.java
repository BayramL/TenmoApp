package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransferDaoTest extends BaseDaoTests{

    JdbcTransferDao transferDao;
    JdbcAccountDao accountDao;
    JdbcUserDao userDao;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        transferDao = new JdbcTransferDao(jdbcTemplate);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        userDao = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void sendTransfer_works() {
        boolean test = transferDao.createSend(userDao.findByUsername("bob"), 1002, new BigDecimal("10.00"));
        Assert.assertTrue(test);
    }

    @Test
    public void sendTransfer_cant_send_more_than_you_have() {
        boolean test = transferDao.createSend(userDao.findByUsername("bob"), 1002, new BigDecimal("100000.00"));
        Assert.assertFalse(test);
    }

    @Test
    public void listAllTransfer_Returns_Proper_List() {
        transferDao.createSend(userDao.findByUsername("bob"), 1002, new BigDecimal("10.00"));
        transferDao.createSend(userDao.findByUsername("bob"), 1002, new BigDecimal("20.00"));
        List<Transfer> testList = new ArrayList<>();
        testList = transferDao.listTransfers(userDao.findByUsername("bob"));
        Assert.assertEquals(2, testList.size());
    }

    @Test
    public void getTransfer_gets_the_correct_transfer() {
        transferDao.createSend(userDao.findByUsername("bob"), 1002, new BigDecimal("1.00"));
        Transfer transfer = transferDao.getTransferById(userDao.findByUsername("bob"),3001);
        Transfer testTransfer = new Transfer();
        transfer.setTransferType("Send");
        transfer.setTransferStatus("Approved");
        transfer.setTransferAmount(new BigDecimal("1.00"));
        transfer.setTransferId(3001);
        transfer.setSenderId(1001);
        transfer.setReceiverId(1002);
        assertTransferAreSame(transfer, testTransfer);
    }

    private void assertTransferAreSame(Transfer t1, Transfer t2) {
        Assert.assertEquals(t1.getTransferAmount(), t2.getTransferAmount());
        Assert.assertEquals(t1.getReceiverId(), t2.getReceiverId());
        Assert.assertEquals(t1.getSenderId(), t2.getSenderId());
        Assert.assertEquals(t1.getTransferStatus(), t1.getTransferStatus());
        Assert.assertEquals(t1.getTransferType(), t1.getTransferType());
        Assert.assertEquals(t1.getTransferId(), t2.getTransferId());
    }





}
