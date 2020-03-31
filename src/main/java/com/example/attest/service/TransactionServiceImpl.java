package com.example.attest.service;

import brave.Tracer;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.api.TransactionStatusApiResponse;
import com.example.attest.model.domain.ChannelType;
import com.example.attest.model.domain.Transaction;
import com.example.attest.model.domain.TransactionStatus;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
		}

		transaction = transactionRepository.save(transaction);

		return transactionConverter.toApiModel(transaction, TransactionApi.class);
	}

	@Override
	public TransactionStatusApiResponse getTransactionStatus(TransactionStatusApiRequest transactionStatusApiRequest) {

		Optional<Transaction> transactionOptional =
			transactionRepository.findByReference(transactionStatusApiRequest.getReference());
		if (!transactionOptional.isPresent()) {
			return TransactionStatusApiResponse.builder()
				.reference(transactionStatusApiRequest.getReference())
				.status(TransactionStatus.INVALID)
				.build();
		} else {
			Transaction transactionStored = transactionOptional.get();
			// I suppose the date is stored always in UTC zone
			LocalDate localDate = transactionStored.getDate()
				.toLocalDate();
			LocalDate today = LocalDate.now(ZoneId.of("UTC"));
			if (localDate.isBefore(today)) {
				if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.CLIENT) || transactionStatusApiRequest.getChannel()
					.equals(ChannelType.ATM)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.SETTLED)
						.amount(transactionStored.getAmount() - transactionStored.getFee())
						.build();
				} else if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.INTERNAL)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.SETTLED)
						.amount(transactionStored.getAmount())
						.fee(transactionStored.getFee())
						.build();
				} else {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.INVALID)
						.build();
				}

			} else if (localDate.isEqual(today)) {
				if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.CLIENT) || transactionStatusApiRequest.getChannel()
					.equals(ChannelType.ATM)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.PENDING)
						.amount(transactionStored.getAmount() - transactionStored.getFee())
						.build();
				} else if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.INTERNAL)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.PENDING)
						.amount(transactionStored.getAmount())
						.fee(transactionStored.getFee())
						.build();
				} else {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.INVALID)
						.build();
				}

			} else {
				if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.CLIENT)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.FUTURE)
						.amount(transactionStored.getAmount() - transactionStored.getFee())
						.build();
				} else if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.ATM)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.PENDING)
						.amount(transactionStored.getAmount() - transactionStored.getFee())
						.build();

				} else if (transactionStatusApiRequest.getChannel()
					.equals(ChannelType.INTERNAL)) {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.FUTURE)
						.amount(transactionStored.getAmount())
						.fee(transactionStored.getFee())
						.build();

				} else {
					return TransactionStatusApiResponse.builder()
						.reference(transactionStatusApiRequest.getReference())
						.status(TransactionStatus.INVALID)
						.build();
				}
			}

		}
	}
}
