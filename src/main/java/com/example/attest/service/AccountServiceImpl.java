package com.example.attest.service;

import com.example.attest.dao.AccountRepository;
import com.example.attest.model.domain.Account;
import java.util.Optional;
import org.springframework.stereotype.Service;

// This class simulates Accounts managemente microservices

@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;

	public AccountServiceImpl(AccountRepository accountRepository) {

		this.accountRepository = accountRepository;
	}


	@Override
	public Double getBalance(String iban) {

		Optional<Account> account = accountRepository.findByIban(iban);

		if (!account.isPresent()) {
			Account account1 = new Account();
			account1.setIban(iban);
			accountRepository.saveAndFlush(account1);
			return account1.getBalance();
		} else {
			return account.get()
				.getBalance();
		}
	}

	@Override
	public void updateBalance(String iban, Double balance) {

		Optional<Account> account = accountRepository.findByIban(iban);

		if (account.isPresent()) {
			account.get()
				.setBalance(balance);
			accountRepository.saveAndFlush(account.get());
		}
	}


}
