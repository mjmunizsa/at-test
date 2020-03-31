package com.example.attest.service;

import brave.Tracer;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
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

		Transaction transaction = transactionRepository.findByReference(reference)
			.orElse(null);

		return transactionConverter.toApiModel(transaction, TransactionApi.class);

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
		}

		transaction = transactionRepository.saveAndFlush(transaction);

		return transactionConverter.toApiModel(transaction, TransactionApi.class);
	}
}
