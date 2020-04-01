package com.example.attest.validator;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Account;

public interface AccountValidator {

	Account validateAccountBalance(TransactionApi transactionApi);
}
