package com.example.attest.service;

import com.example.attest.dao.AccountRepository;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Account;
import com.example.attest.validator.AccountValidator;
import org.springframework.stereotype.Service;

/**
 * This class simulates Accounts managemente microservices. Although the requirements do not say anything, I think it is necessary
 * to simulate the increase and decrease of the balance in the accounts.
 */


@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;

	private AccountValidator accountValidator;


	public AccountServiceImpl(AccountRepository accountRepository, AccountValidator accountValidator) {

		this.accountRepository = accountRepository;
		this.accountValidator = accountValidator;
	}


	@Override
	public Account updateBalance(TransactionApi transactionApi) {

		Account account = accountValidator.validateAccountBalance(transactionApi);

		return accountRepository.save(account);
	}


}
