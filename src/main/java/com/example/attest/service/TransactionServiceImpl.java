package com.example.attest.service;

import com.example.attest.converter.TransactionConverter;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Account;
import com.example.attest.model.domain.Transaction;
import com.example.attest.validator.TransactionValidator;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

	private TransactionRepository transactionRepository;

	private TransactionConverter transactionConverter;

	private TransactionValidator transactionValidator;

	private AccountService accountService;

	public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionConverter transactionConverter,
		TransactionValidator transactionValidator, AccountService accountService) {

		this.transactionRepository = transactionRepository;
		this.transactionConverter = transactionConverter;
		this.transactionValidator = transactionValidator;
		this.accountService = accountService;
	}


	@Override
	public TransactionApi findByReference(String reference) {

		Optional<Transaction> optionalTransaction = transactionRepository.findByReference(reference);

		if (optionalTransaction.isPresent()) {
			return transactionConverter.toApiModel(optionalTransaction.get(), TransactionApi.class);
		} else {
			throw new ServiceException.Builder(ServiceException.ERROR_TRANSACTION_NOT_FOUND)
				.withHttpStatus(HttpStatus.NOT_FOUND)
				.withMessage(String.format("Transaction with reference %s not found",
					reference))
				.build();
		}


	}


	@Override
	public Page<TransactionApi> findByAccountIban(String accountIban, Pageable pageable) {

		return transactionConverter.toApiPage(transactionRepository.findByAccountIban(accountIban, pageable), pageable);

	}


	@Override
	@Transactional
	public TransactionApi create(TransactionApi transactionApi) {

		transactionValidator.validateOptionalsToCreate(transactionApi);
		transactionValidator.validateUniqueReference(transactionApi.getReference());

		Transaction transactionIn = transactionConverter.toModelApi(transactionApi, Transaction.class);
		Transaction transactionOut = transactionRepository.save(transactionIn);
		accountService.updateBalance(transactionApi);
		transactionApi = transactionConverter.toApiModel(transactionOut, TransactionApi.class);
		return transactionApi;
	}
}
