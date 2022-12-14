package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> listTransfers(User user);

    Transfer getTransferById(User user, int transferId);

    boolean createSend(User user, int receiverId, BigDecimal transferAmount);

    boolean createRequest(User user, int senderId, BigDecimal transferAmount);


}
