package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;

public interface TransactionService {

	TransactionApi findByReference(String reference);

	TransactionApi create(TransactionApi transactionApi);

}
