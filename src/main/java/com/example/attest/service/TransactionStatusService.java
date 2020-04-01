package com.example.attest.service;

import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.api.TransactionStatusApiResponse;

public interface TransactionStatusService {

	TransactionStatusApiResponse getTransactionStatus(TransactionStatusApiRequest transactionStatusApiRequest);
}
