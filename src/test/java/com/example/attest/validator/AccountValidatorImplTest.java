package com.example.attest.validator;

import static org.mockito.Mockito.when;

import com.example.attest.dao.AccountRepository;
import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Account;
import com.example.attest.validator.AccountValidator;
import com.example.attest.validator.AccountValidatorImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("TEST")
public class AccountValidatorImplTest {

	@Configuration
	static class ContextConfiguration {


		@Bean
		public AccountValidatorImpl accountValidator(AccountRepository accountRepository) {

			return new AccountValidatorImpl(accountRepository);
		}

	}

	@Autowired
	private AccountValidator accountValidator;

	@MockBean
	private AccountRepository accountRepository;


	private static final String FAKE_ACCOUNT_IBAN = "FAKE_ACCOUNTIBAN";
	private static final BigDecimal FAKE_ACCOUNT_AMOUNT = new BigDecimal(10);
	private static final String FAKE_TRANSACTION_REFERENCE = "FAKE_REFERENCE";


	@Test(expected = ServiceException.class)
	public void validateAccountBalanceShouldThrowExceptionIfBalanceAccountIsBellowZero() {

		//given
		Account accountMock = new Account();
		accountMock.setIban(FAKE_ACCOUNT_IBAN);
		accountMock.setBalance(FAKE_ACCOUNT_AMOUNT);

		TransactionApi transactionApi = new TransactionApi();
		transactionApi.setAccountIban(FAKE_ACCOUNT_IBAN);
		transactionApi.setReference(FAKE_TRANSACTION_REFERENCE);
		transactionApi.setAmount(FAKE_ACCOUNT_AMOUNT.add(new BigDecimal(1))
			.negate());

		//Mockito.doReturn(Optional.of(accountMock)).when(accountRepository.findByIban(Mockito.anyString()));
		when(accountRepository.findByIban(Mockito.anyString())).thenReturn(Optional.of(accountMock));

		//when
		accountValidator.validateAccountBalance(transactionApi);

	}

}
