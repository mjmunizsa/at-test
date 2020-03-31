package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import java.util.List;

public interface TransactionService {

	TransactionApi findByReference(String reference);

	List<TransactionApi> findByAccountIbanOrderByAmount(String accountIban, String directiono);


	TransactionApi create(TransactionApi transactionApi);

}
