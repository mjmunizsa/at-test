package com.example.attest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.example.attest.dao.TransactionRepository;
import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.api.TransactionStatusApiResponse;
import com.example.attest.model.domain.ChannelType;
import com.example.attest.model.domain.Transaction;
import com.example.attest.model.domain.TransactionStatus;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Before;
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
public class TransactionStatusServiceImplTest {

	@Configuration
	static class ContextConfiguration {


		@Bean
		public TransactionStatusServiceImpl transactionStatusService(TransactionRepository transactionRepository) {

			return new TransactionStatusServiceImpl(transactionRepository);
		}

	}

	@MockBean
	private TransactionRepository transactionRepository;

	@Autowired
	private TransactionStatusServiceImpl transactionStatusService;

	private static final String FAKE_TRANSACTION_REFERENCE = "FAKE_REFERENCE";

	TransactionStatusApiRequest transactionRequestCLIENT;

	TransactionStatusApiRequest transactionRequestATM;

	TransactionStatusApiRequest transactionRequestINTERNAL;

	TransactionStatusApiRequest transactionRequestWithOutChannel;


	Transaction transactionBeforeMock;

	Transaction transactionTodayMock;

	Transaction transactionAfterMock;

	@Before
	public void setUp() throws IOException {

		transactionBeforeMock = new Transaction();
		transactionBeforeMock.setDate(LocalDateTime.now()
			.minusDays(1));
		transactionBeforeMock.setAmount(new BigDecimal(-80.55));
		transactionBeforeMock.setFee(new BigDecimal(3.50));

		transactionTodayMock = new Transaction();
		transactionTodayMock.setDate(LocalDateTime.now());
		transactionTodayMock.setAmount(new BigDecimal(80.55));
		transactionTodayMock.setFee(new BigDecimal(3.50));

		transactionAfterMock = new Transaction();
		transactionAfterMock.setDate(LocalDateTime.now().plusDays(1));
		transactionAfterMock.setAmount(new BigDecimal(80.55));

		transactionRequestCLIENT = new TransactionStatusApiRequest();
		transactionRequestCLIENT.setReference(FAKE_TRANSACTION_REFERENCE);
		transactionRequestCLIENT.setChannel(ChannelType.CLIENT);

		transactionRequestATM = new TransactionStatusApiRequest();
		transactionRequestATM.setReference(FAKE_TRANSACTION_REFERENCE);
		transactionRequestATM.setChannel(ChannelType.ATM);

		transactionRequestINTERNAL = new TransactionStatusApiRequest();
		transactionRequestINTERNAL.setReference(FAKE_TRANSACTION_REFERENCE);
		transactionRequestINTERNAL.setChannel(ChannelType.INTERNAL);

		transactionRequestWithOutChannel = new TransactionStatusApiRequest();
		transactionRequestWithOutChannel.setReference(FAKE_TRANSACTION_REFERENCE);

	}


	@Test
	public void getTransactionStatusShouldReturnStatusInvalid() {

		//given
		TransactionStatusApiRequest t = new TransactionStatusApiRequest();
		t.setReference(FAKE_TRANSACTION_REFERENCE);
		when(transactionRepository.findByReference(Mockito.anyString())).thenReturn(Optional.empty());
		//when
		TransactionStatusApiResponse response = transactionStatusService.getTransactionStatus(t);

		//then
		assertEquals(response.getStatus()
			.name(), TransactionStatus.INVALID.name());
	}

	@Test
	public void getTransactionStatusShouldReturnStatusCorrectBeforeDay() {

		//given
		when(transactionRepository.findByReference(Mockito.anyString())).thenReturn(Optional.of(transactionBeforeMock));

		//when
		TransactionStatusApiResponse responseWithOutChannel = transactionStatusService.getTransactionStatus(
			transactionRequestWithOutChannel);
		TransactionStatusApiResponse responseCLIENT = transactionStatusService.getTransactionStatus(transactionRequestCLIENT);
		TransactionStatusApiResponse responseATM = transactionStatusService.getTransactionStatus(transactionRequestATM);
		TransactionStatusApiResponse responseINTERNAL = transactionStatusService.getTransactionStatus(transactionRequestINTERNAL);

		//then

		assertEquals(responseWithOutChannel.getStatus()
			.name(), TransactionStatus.SETTLED.name());
		assertEquals(responseWithOutChannel.getAmount()
			,
			transactionBeforeMock.getAmount()
				.subtract(transactionBeforeMock.getFee()));
		assertNull("Fee should be NULL", responseWithOutChannel.getFee());

		assertEquals(responseCLIENT.getStatus()
			.name(), TransactionStatus.SETTLED.name());
		assertEquals(responseCLIENT.getAmount()
			,
			transactionBeforeMock.getAmount()
				.subtract(transactionBeforeMock.getFee()));
		assertNull("Fee should be NULL", responseCLIENT.getFee());

		assertEquals(responseATM.getStatus()
			.name(), TransactionStatus.SETTLED.name());
		assertEquals(responseATM.getAmount()
			,
			transactionBeforeMock.getAmount()
				.subtract(transactionBeforeMock.getFee()));
		assertNull("Fee should be NULL", responseATM.getFee());

		assertEquals(responseINTERNAL.getStatus()
			.name(), TransactionStatus.SETTLED.name());
		assertEquals(responseINTERNAL.getAmount()
			, transactionBeforeMock.getAmount());
		assertEquals(responseINTERNAL.getFee()
			, transactionBeforeMock.getFee());

	}

	@Test
	public void getTransactionStatusShouldReturnStatusCorrectToday() {

		//given
		when(transactionRepository.findByReference(Mockito.anyString())).thenReturn(Optional.of(transactionTodayMock));

		//when
		TransactionStatusApiResponse responseWithOutChannel = transactionStatusService.getTransactionStatus(
			transactionRequestWithOutChannel);
		TransactionStatusApiResponse responseCLIENT = transactionStatusService.getTransactionStatus(transactionRequestCLIENT);
		TransactionStatusApiResponse responseATM = transactionStatusService.getTransactionStatus(transactionRequestATM);
		TransactionStatusApiResponse responseINTERNAL = transactionStatusService.getTransactionStatus(transactionRequestINTERNAL);

		//then

		assertEquals(responseWithOutChannel.getStatus()
			.name(), TransactionStatus.PENDING.name());
		assertEquals(responseWithOutChannel.getAmount()
			,
			transactionTodayMock.getAmount()
				.subtract(transactionTodayMock.getFee()));
		assertNull("Fee should be NULL", responseWithOutChannel.getFee());

		assertEquals(responseCLIENT.getStatus()
			.name(), TransactionStatus.PENDING.name());
		assertEquals(responseCLIENT.getAmount()
			,
			transactionTodayMock.getAmount()
				.subtract(transactionTodayMock.getFee()));
		assertNull("Fee should be NULL", responseCLIENT.getFee());

		assertEquals(responseATM.getStatus()
			.name(), TransactionStatus.PENDING.name());
		assertEquals(responseATM.getAmount()
			,
			transactionTodayMock.getAmount()
				.subtract(transactionTodayMock.getFee()));
		assertNull("Fee should be NULL", responseATM.getFee());

		assertEquals(responseINTERNAL.getStatus()
			.name(), TransactionStatus.PENDING.name());
		assertEquals(responseINTERNAL.getAmount()
			, transactionTodayMock.getAmount());
		assertEquals(responseINTERNAL.getFee()
			, transactionTodayMock.getFee());

	}

	@Test
	public void getTransactionStatusShouldReturnStatusCorrectAfterDay() {

		//given
		when(transactionRepository.findByReference(Mockito.anyString())).thenReturn(Optional.of(transactionAfterMock));

		//when
		TransactionStatusApiResponse responseWithOutChannel = transactionStatusService.getTransactionStatus(
			transactionRequestWithOutChannel);
		TransactionStatusApiResponse responseCLIENT = transactionStatusService.getTransactionStatus(transactionRequestCLIENT);
		TransactionStatusApiResponse responseATM = transactionStatusService.getTransactionStatus(transactionRequestATM);
		TransactionStatusApiResponse responseINTERNAL = transactionStatusService.getTransactionStatus(transactionRequestINTERNAL);

		BigDecimal fee = transactionAfterMock.getFee()== null ? BigDecimal.ZERO : transactionAfterMock.getFee();

		//then

		assertEquals(responseWithOutChannel.getStatus()
			.name(), TransactionStatus.FUTURE.name());
		assertEquals(responseWithOutChannel.getAmount()
			,
			transactionAfterMock.getAmount()
				.subtract(fee));
		assertNull("Fee should be NULL", responseWithOutChannel.getFee());

		assertEquals(responseCLIENT.getStatus()
			.name(), TransactionStatus.FUTURE.name());
		assertEquals(responseCLIENT.getAmount()
			,
			transactionAfterMock.getAmount()
				.subtract(fee));
		assertNull("Fee should be NULL", responseCLIENT.getFee());

		assertEquals(responseATM.getStatus()
			.name(), TransactionStatus.PENDING.name());
		assertEquals(responseATM.getAmount()
			,
			transactionAfterMock.getAmount()
				.subtract(fee));
		assertNull("Fee should be NULL", responseATM.getFee());

		assertEquals(responseINTERNAL.getStatus()
			.name(), TransactionStatus.FUTURE.name());
		assertEquals(responseINTERNAL.getAmount()
			, transactionAfterMock.getAmount());
		assertEquals(responseINTERNAL.getFee()
			, fee);

	}

}
