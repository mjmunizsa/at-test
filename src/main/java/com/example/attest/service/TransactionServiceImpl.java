package com.example.attest.service;

import brave.Tracer;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

	private TransactionRepository transactionRepository;

	private TransactionConverter transactionConverter;

	private Tracer tracer;

	public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionConverter transactionConverter,
		Tracer tracer) {

		this.transactionRepository = transactionRepository;
		this.transactionConverter = transactionConverter;
		this.tracer = tracer;
	}


	@Override
	public TransactionApi findByReference(String reference) {

		Optional<Transaction> optionalTransaction = transactionRepository.findByReference(reference);

		if (optionalTransaction.isPresent()) {
			return transactionConverter.toApiModel(optionalTransaction.get(), TransactionApi.class);
		} else {
			throw new ServiceException.Builder(ServiceException.ERROR_TRANSACTION_NOT_FOUND)
				.withHttpStatus(HttpStatus.BAD_REQUEST)
				.withMessage(String.format("Transaction with reference %s not found",
					reference))
				.withHttpStatus(HttpStatus.NOT_FOUND)
				.build();
		}


	}


	@Override
	public Page<TransactionApi> findByAccountIban(String accountIban, Pageable pageable) {

		return transactionConverter.toApiPage(transactionRepository.findByAccountIban(accountIban, pageable), pageable);

	}


	@Override
	public TransactionApi create(TransactionApi transactionApi) {

		Transaction transaction = transactionConverter.toModelApi(transactionApi, Transaction.class);

		if (transaction.getReference() == null) {
			transaction.setReference(String.format("%s-%s",
				tracer.currentSpan()
					.context()
					.traceIdString(),
				tracer.currentSpan()
					.context()
					.spanIdString()));
		} else {
			TransactionApi transactionStored = findByReference(transactionApi.getReference());
			if (transactionStored != null) {
				throw new ServiceException.Builder(ServiceException.ERROR_TRANSACTION_DUPLICATED)
					.withHttpStatus(HttpStatus.BAD_REQUEST)
					.withMessage(String.format("It exists a transaction with the same reference %s",
						transactionStored.getReference()))
					.withHttpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
			}
		}

		transaction = transactionRepository.save(transaction);

		return transactionConverter.toApiModel(transaction, TransactionApi.class);
	}
}
