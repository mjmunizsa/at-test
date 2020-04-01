package com.example.attest.validator;

import brave.Tracer;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidatorImpl implements TransactionValidator {

	private TransactionRepository transactionRepository;

	private Tracer tracer;

	public TransactionValidatorImpl(TransactionRepository transactionRepository,
		Tracer tracer) {

		this.transactionRepository = transactionRepository;
		this.tracer = tracer;
	}


	@Override
	public void validateUniqueReference(String reference) {

		Optional<Transaction> optionalTransaction = transactionRepository.findByReference(reference);
		if (optionalTransaction.isPresent()) {
			throw new ServiceException.Builder(ServiceException.ERROR_TRANSACTION_DUPLICATED)
				.withMessage(String.format("It exists a transaction with the same reference %s",
					optionalTransaction.get()
						.getReference()))
				.withHttpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
				.build();
		}

	}

	@Override
	public void validateOptionalsToCreate(TransactionApi transactionApi) {

		if (transactionApi.getReference() == null) {
			transactionApi.setReference(String.format("%s-%s",
				tracer.currentSpan()
					.context()
					.traceIdString(),
				tracer.currentSpan()
					.context()
					.spanIdString()));
		}

		if (transactionApi.getDate() == null) {
			transactionApi.setDate(LocalDateTime.now());
		}

		if (transactionApi.getFee() == null) {
			transactionApi.setFee(BigDecimal.ZERO);
		}

	}
}
