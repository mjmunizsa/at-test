package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverterImpl implements TransactionConverter {

	@Override
	public TransactionApi toApiModel(Transaction transaction, Class<TransactionApi> apiClass) {

		return transaction != null ? getModelMapper().map(transaction, apiClass) : null;
	}

	@Override
	public Transaction toModelApi(TransactionApi transactionApi, Class<Transaction> domainClass) {

		return transactionApi != null ? getModelMapper().map(transactionApi, domainClass) : null;
	}

	private ModelMapper getModelMapper() {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
			.setMatchingStrategy(MatchingStrategies.LOOSE);
		return modelMapper;
	}
}
