package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Account;

public interface AccountService {


	Account updateBalance(TransactionApi transactionApi);

}
