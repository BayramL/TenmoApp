package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private AccountDao accountDao;
    private UserDao userDao;
    private TransferDao transferDao;

    public TransferController(AccountDao accountDao, UserDao userDao, TransferDao transferDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public List<Transfer> listTransfers(Principal principal) {
        User user = userDao.findByUsername(principal.getName());
        return transferDao.listTransfers(user);
    }

    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfer(Principal principal, @PathVariable int transferId) {
        User user = userDao.findByUsername(principal.getName());
        return transferDao.getTransferById(user, transferId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean createSendTransfer(@RequestBody Transfer transfer, Principal principal) {
        int receiverId = transfer.getReceiverId();
        BigDecimal transferAmount = transfer.getTransferAmount();
        return transferDao.createSend(userDao.findByUsername(principal.getName()), receiverId, transferAmount);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean createRequestTransfer(@RequestBody Transfer transfer, Principal principal) {
        int senderId = transfer.getSenderId();
        return transferDao.createRequest(userDao.findByUsername(principal.getName()), senderId, transfer.getTransferAmount());
    }

    @RequestMapping(path = "/request/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfers(Principal principal) {
        return transferDao.getPendingTransfers(userDao.findByUsername(principal.getName()));
    }

    @RequestMapping(path = "/request/accept/{transferId}", method = RequestMethod.PUT)
    public boolean acceptPendingTransfer(Principal principal, @PathVariable int transferId) {
        return transferDao.acceptRequest(userDao.findByUsername(principal.getName()), transferId);
    }

    @RequestMapping(path = "/request/reject/{transferId}", method = RequestMethod.PUT)
    public void rejectPendingTransfer(Principal principal, @PathVariable int transferId) {
        transferDao.rejectRequest(userDao.findByUsername(principal.getName()), transferId);
    }

}
