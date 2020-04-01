package com.example.attest.converter;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionConverter {

	TransactionApi toApiModel(Transaction transaction, Class<TransactionApi> apiClass);

	Transaction toModelApi(TransactionApi transactionApi, Class<Transaction> domainClass);

	Page<TransactionApi> toApiPage(Page<Transaction> transactionPage, Pageable pageable);

	List<TransactionApi> toApiList(List<Transaction> transactions);
}
