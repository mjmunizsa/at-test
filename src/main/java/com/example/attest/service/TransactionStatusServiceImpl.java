package com.example.attest.service;

import com.example.attest.dao.TransactionRepository;
import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.api.TransactionStatusApiResponse;
import com.example.attest.model.domain.ChannelType;
import com.example.attest.model.domain.Transaction;
import com.example.attest.model.domain.TransactionStatus;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatusServiceImpl implements TransactionStatusService {

	private TransactionRepository transactionRepository;


	public TransactionStatusServiceImpl(TransactionRepository transactionRepository) {

		this.transactionRepository = transactionRepository;
	}

	@Override
	public TransactionStatusApiResponse getTransactionStatus(TransactionStatusApiRequest transactionStatusApiRequest) {

		/**
		 * NOTE IMPORTANT:
		 * As the channel is not mandatory and the requirements do not say anything about the behaviour in this case,
		 * It has been considered that if we do not have the channel, it is interpreted as CLIENT
		 */
		if (transactionStatusApiRequest.getChannel() == null) {
			transactionStatusApiRequest.setChannel(ChannelType.CLIENT);
		}

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
				return getStatusIfTransactionIsBeforeToday(transactionStatusApiRequest, transactionStored);
			} else if (localDate.isEqual(today)) {
				return getStatusIfTransactionIsToday(transactionStatusApiRequest, transactionStored);
			} else {
				return getStatusIfTransactionIsAfterToday(transactionStatusApiRequest, transactionStored);
			}
		}
	}

	private TransactionStatusApiResponse getStatusIfTransactionIsToday(TransactionStatusApiRequest transactionStatusApiRequest,
		Transaction transactionStored) {

		if (transactionStatusApiRequest.getChannel()
			.equals(ChannelType.INTERNAL)) {
			return TransactionStatusApiResponse.builder()
				.reference(transactionStatusApiRequest.getReference())
				.status(TransactionStatus.PENDING)
				.amount(transactionStored.getAmount())
				.fee(transactionStored.getFee())
				.build();
		} else {
			// Regardless of the channel, we return the PENDING status with the fee deducted
			return TransactionStatusApiResponse.builder()
				.reference(transactionStatusApiRequest.getReference())
				.status(TransactionStatus.PENDING)
				.amount(transactionStored.getAmount() - transactionStored.getFee())
				.build();
		}

	}

	private TransactionStatusApiResponse getStatusIfTransactionIsBeforeToday(
		TransactionStatusApiRequest transactionStatusApiRequest,
		Transaction transactionStored) {

		if (transactionStatusApiRequest.getChannel()
			.equals(ChannelType.INTERNAL)) {
			return TransactionStatusApiResponse.builder()
				.reference(transactionStatusApiRequest.getReference())
				.status(TransactionStatus.SETTLED)
				.amount(transactionStored.getAmount())
				.fee(transactionStored.getFee())
				.build();
		} else {
			// Regardless of the channel, we return the SETTLED status with the fee deducted
			return TransactionStatusApiResponse.builder()
				.reference(transactionStatusApiRequest.getReference())
				.status(TransactionStatus.SETTLED)
				.amount(transactionStored.getAmount() - transactionStored.getFee())
				.build();
		}

	}

	private TransactionStatusApiResponse getStatusIfTransactionIsAfterToday(
		TransactionStatusApiRequest transactionStatusApiRequest,
		Transaction transactionStored) {

		if (transactionStatusApiRequest.getChannel()
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
				.status(TransactionStatus.FUTURE)
				.amount(transactionStored.getAmount() - transactionStored.getFee())
				.build();
		}
	}
}
