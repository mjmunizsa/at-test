package com.example.attest.validator;

import com.example.attest.dao.AccountRepository;
import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Account;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AccountValidatorImpl implements AccountValidator {

	private AccountRepository accountRepository;

	public AccountValidatorImpl(AccountRepository accountRepository) {

		this.accountRepository = accountRepository;
	}

	@Override
	public Account validateAccountBalance(TransactionApi transactionApi) {

		/**
		 * To avoid having to create a new endpoint for the accounts,
		 * when the balance of an account is requested if we don't have it,
		 * we create it with a zero balance.
		 */
		BigDecimal fee = transactionApi.getFee()== null ? BigDecimal.ZERO : transactionApi.getFee();
		Optional<Account> account = accountRepository.findByIban(transactionApi.getAccountIban());

		if (!account.isPresent()) {
			Account account1 = new Account();
			account1.setIban(transactionApi.getAccountIban());
			account1 = accountRepository.saveAndFlush(account1);
			account = Optional.of(account1);
		}

		/**
		 * I have assumed that the balance will only be checked for negative transactions,
		 * which are those that can bring the account below zero.
		 */
		BigDecimal newBalance = account.get()
			.getBalance()
			.add(transactionApi.getAmount())
			.subtract(fee);
		if (newBalance.doubleValue() < 0) {
			throw new ServiceException.Builder(ServiceException.ERROR_BALANCE_NEGATIVE_IS_NOT_ALLOWED_)
				.withHttpStatus(HttpStatus.PRECONDITION_FAILED)
				.withMessage(String.format("The transaction causes the negative account balance = %s. The current balance is %s",
					newBalance, account.get()
						.getBalance()))
				.build();
		}

		account.get()
			.setBalance(newBalance);
		return account.get();


	}


}
