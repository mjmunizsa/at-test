package com.example.attest.validator;

import com.example.attest.model.api.TransactionApi;

public interface TransactionValidator {

	void validateUniqueReference(String reference);

	void validateOptionalsToCreate(TransactionApi transactionApi);

}
