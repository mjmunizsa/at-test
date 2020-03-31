package com.example.attest.service;

import brave.Tracer;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
	public List<TransactionApi> findByAccountIbanOrderByAmount(String accountIban, String direction) {

		Sort sort = null;
		if (direction.equals(Direction.DESC.name())) {
			sort = Sort.by(Sort.Direction.DESC, "amount");
		} else {
			sort = Sort.by(Direction.ASC, "amount");
		}
		return transactionConverter.toApiList(transactionRepository.findByAccountIban(accountIban, sort));
	}

	@Override
	public Page<TransactionApi> findByAccountIban(String accountIban, Pageable pageable) {

		return transactionConverter.toApiPage(transactionRepository.findByAccountIban(accountIban, pageable));

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

		transaction = transactionRepository.save(transaction);

		return transactionConverter.toApiModel(transaction, TransactionApi.class);
	}
}
