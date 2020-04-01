package com.example.attest.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import brave.Tracer;
import com.example.attest.converter.TransactionConverter;
import com.example.attest.dao.TransactionRepository;
import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import com.example.attest.validator.TransactionValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("TEST")
public class TransactionServiceImplTest {

	@Configuration
	static class ContextConfiguration {


		@Bean
		public TransactionServiceImpl transactionService(TransactionRepository transactionRepository,
			TransactionConverter transactionConverter, TransactionValidator transactionValidator, AccountService accountService) {

			return new TransactionServiceImpl(transactionRepository,
				transactionConverter,
				transactionValidator,
				accountService);
		}

	}

	@MockBean
	private TransactionRepository transactionRepository;

	@MockBean
	private TransactionConverter transactionConverter;

	@MockBean
	TransactionValidator transactionValidator;

	@MockBean
	private AccountService accountService;

	private ObjectMapper objectMapper;

	@Value("classpath:mocks/TransactionApi.json")
	private URL transactionApiUrl;

	@Captor
	private ArgumentCaptor<Transaction> transactionAgumentCaptor;

	@Captor
	private ArgumentCaptor<Class> classArgumentCaptor;

	@Autowired
	private TransactionService transactionService;

	private static final String FAKE_TRANSACTION_REFERENCE = "FAKE_REFERENCE";
	private static final String FAKE_ACCOUNT_IBAN = "FAKE_ACCOUNTIBAN";
	private static final BigDecimal FAKE_AMOUNT_IBAN = new BigDecimal(100.0);

	@Before
	public void setUp() throws IOException {
		objectMapper = new ObjectMapper();
	}

	@Test
	public void findByReferenceShouldCallConverterIfTransactionExists() {

		//given
		Transaction t = new Transaction();
		t.setReference(FAKE_TRANSACTION_REFERENCE);
		t.setAccountIban(FAKE_ACCOUNT_IBAN);
		t.setAmount(FAKE_AMOUNT_IBAN);
		when(transactionRepository.findByReference(Mockito.anyString())).thenReturn(Optional.of(t));
		//when
		transactionService.findByReference(FAKE_TRANSACTION_REFERENCE);

		//then
		verify(transactionConverter, times(1))
			.toApiModel(transactionAgumentCaptor.capture(), classArgumentCaptor.capture());
		assertEquals("Transaction reference should not been modified",
			FAKE_TRANSACTION_REFERENCE,
			transactionAgumentCaptor.getValue()
				.getReference());
		assertEquals("Transaction account_iban should not been modified",
			FAKE_ACCOUNT_IBAN,
			transactionAgumentCaptor.getValue()
				.getAccountIban());
		assertEquals("Transaction amount should not been modified",
			FAKE_AMOUNT_IBAN,
			transactionAgumentCaptor.getValue()
				.getAmount());
	}

	@Test(expected = ServiceException.class)
	public void findByReferenceShouldThrowsServiceExceptionIfTransactionNotExits() {

		//given
		when(transactionRepository.findByReference(Mockito.anyString())).thenReturn(Optional.empty());

		//when
		transactionService.findByReference(Mockito.anyString());

		//then
		verifyNoInteractions(transactionConverter);
	}

	@Test(expected = ServiceException.class)
	public void createNotShouldCallRepositoryCreateIfTransactionNotValidate() throws IOException {

		//given
		TransactionApi transactionApiMock = objectMapper.readValue(transactionApiUrl, TransactionApi.class);
		doThrow(ServiceException.class).when(transactionValidator).validateUniqueReference(Mockito.anyString());

		//when
		transactionService.create(transactionApiMock);

		//then
		verifyNoInteractions(transactionRepository);
		verifyNoInteractions(transactionConverter);
	}
}
