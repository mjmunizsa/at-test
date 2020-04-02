package com.example.attest.controller;


import static com.example.attest.controller.AbstractApiController.BASE_MAPPING;
import static com.example.attest.controller.TransactionController.REQUEST_PARAM_ACCOUNT_IBAN;
import static com.example.attest.controller.TransactionController.RESOURCE_TRANSACTION_MAPPING;
import static com.example.attest.controller.TransactionController.RESOURCE_TRANSACTION_STATUS_MAPPING;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.domain.ChannelType;
import com.example.attest.model.domain.TransactionStatus;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("TEST")
public class TransactionIntegrationTest {

	@LocalServerPort
	protected int port;

	private static final String FAKE_TRANSACTION_REFERENCE = "FAKE_REFERENCE";
	private static final String FAKE_TRANSACTION_ALREADY_STORED = "FAKE_TRANSACTION_ALREADY_STORED";
	private static final String FAKE_TRANSACTION_NOT_EXISTS = "FAKE_TRANSACTION_NOT_EXISTS";
	private static final String FAKE_TRANSACTION_BEFORE = "FAKE_TRANSACTION_BEFORE";
	private static final String FAKE_TRANSACTION_TODAY = "FAKE_TRANSACTION_TODAY";
	private static final String FAKE_TRANSACTION_AFTER = "FAKE_TRANSACTION_AFTER";
	private static final String FAKE_ACCOUNT_IBAN = "FAKE_ACCOUNTIBAN";
	private static final BigDecimal FAKE_AMOUNT_IBAN = new BigDecimal(100.50);
	private static final BigDecimal FAKE_FEE_IBAN = new BigDecimal(10.25);

	private static final String SORT_PARAMETER = "sort";

	TransactionApi transactionApiExists;

	TransactionApi transactionApiBefore;

	TransactionApi transactionApiToday;

	TransactionApi transactionApiAfter;


	@Before
	public void setUp() {

		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = this.port;

		transactionApiExists = new TransactionApi();
		transactionApiExists.setReference(FAKE_TRANSACTION_ALREADY_STORED);
		transactionApiExists.setAmount(FAKE_AMOUNT_IBAN);
		transactionApiExists.setAccountIban(FAKE_ACCOUNT_IBAN);

		RequestSpecification request = RestAssured.given();
		request.contentType(ContentType.JSON);
		request.body(transactionApiExists);
		request.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING);

		// Three transactions are stored, one before today, one today and one after today
		// These transaction are used by integration test transactionStatus

		transactionApiBefore = new TransactionApi();
		transactionApiBefore.setReference(FAKE_TRANSACTION_BEFORE);
		transactionApiBefore.setAmount(FAKE_AMOUNT_IBAN);
		transactionApiBefore.setAccountIban(FAKE_ACCOUNT_IBAN);
		transactionApiBefore.setFee(FAKE_FEE_IBAN);
		transactionApiBefore.setDate(LocalDateTime.now()
			.minusDays(1));
		request.body(transactionApiBefore);
		request.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING);

		transactionApiToday = new TransactionApi();
		transactionApiToday.setReference(FAKE_TRANSACTION_TODAY);
		transactionApiToday.setAmount(FAKE_AMOUNT_IBAN);
		transactionApiToday.setAccountIban(FAKE_ACCOUNT_IBAN);
		transactionApiToday.setFee(FAKE_FEE_IBAN);
		transactionApiToday.setDate(LocalDateTime.now());
		request.body(transactionApiToday);
		request.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING);

		transactionApiAfter = new TransactionApi();
		transactionApiAfter.setReference(FAKE_TRANSACTION_AFTER);
		transactionApiAfter.setAmount(FAKE_AMOUNT_IBAN);
		transactionApiAfter.setAccountIban(FAKE_ACCOUNT_IBAN);
		transactionApiAfter.setFee(FAKE_FEE_IBAN);
		transactionApiAfter.setDate(LocalDateTime.now()
			.plusDays(1));
		request.body(transactionApiAfter);
		request.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING);


	}


	@Test
	public void postTransactionMinimumDataShouldStoreTransaction() {

		TransactionApi transactionApi = new TransactionApi();
		transactionApi.setAmount(FAKE_AMOUNT_IBAN);
		transactionApi.setAccountIban(FAKE_ACCOUNT_IBAN);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionApi)
			.log().uri()
			.log().body();


        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.CREATED.value())
			.body("$",hasKey("reference"))
			.body("$",hasKey("amount"))
			.body("$",hasKey("account_iban"))
			.log().status()
			.log().body();

		// @formatter:on
	}

	@Test
	public void postTransactionShouldReturnBadRequestWhenNotMandatoryFields() {

		TransactionApi transactionApi = new TransactionApi();
		transactionApi.setReference(FAKE_TRANSACTION_REFERENCE);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionApi).log().uri().log().body();

        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.log().status()
			.log().body();

		// @formatter:on
	}

	@Test
	public void postTransactionShouldReturnUnprocessableEntityWhenDuplicatedTransaction() {

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionApiExists).log().uri().log().body();

        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
			.log().status()
			.log().body();

		// @formatter:on
	}

	@Test
	public void getTransactionShouldReturnTransactionAlreadyStored() {

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.log().uri();

        requestSpecification.when()
			.get(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING + "/" + FAKE_TRANSACTION_ALREADY_STORED)

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("reference",equalTo(FAKE_TRANSACTION_ALREADY_STORED))
			.log().status()
			.log().body();

		// @formatter:on
	}

	@Test
	public void getTransactionShouldReturnNotFoundWhenTransactionIsNotFound() {

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.log().uri();

        requestSpecification.when()
			.get(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING + "/" + FAKE_TRANSACTION_NOT_EXISTS)

        .then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.log().status()
			.log().body();

		// @formatter:on
	}

	@Test
	public void getTransactionByIbanShouldReturnListTransactions() {

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.param(REQUEST_PARAM_ACCOUNT_IBAN, FAKE_ACCOUNT_IBAN)
			.param(SORT_PARAMETER , "amount,desc")
			.log().uri()
			.log().params();

        requestSpecification.when()
			.get(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.OK.value())
			.log().status()
			.log().body();

		// @formatter:on
	}

	@Test
	public void testINVALID() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_NOT_EXISTS);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.INVALID.name()))
			.log().status().log().body();
	}

	@Test
	public void testBeforeChannelCLIENT() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_BEFORE);
		transactionStatusApiRequest.setChannel(ChannelType.CLIENT);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.SETTLED.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testBeforeChannelATM() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_BEFORE);
		transactionStatusApiRequest.setChannel(ChannelType.ATM);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.SETTLED.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testBeforeChannelEMPTY() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_BEFORE);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.SETTLED.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testBeforeChannelINTERNAL() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_BEFORE);
		transactionStatusApiRequest.setChannel(ChannelType.INTERNAL);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.SETTLED.name()))
			.body("$", hasKey("amount"))
			.body("$", hasKey("fee"))
			.log().status().log().body();
	}

	@Test
	public void testTodayChannelCLIENT() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_TODAY);
		transactionStatusApiRequest.setChannel(ChannelType.CLIENT);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.PENDING.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testTodayChannelATM() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_TODAY);
		transactionStatusApiRequest.setChannel(ChannelType.ATM);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.PENDING.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testTodayChannelEMTPY() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_TODAY);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.PENDING.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testTodayChannelINTERNAL() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_TODAY);
		transactionStatusApiRequest.setChannel(ChannelType.INTERNAL);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.PENDING.name()))
			.body("$", hasKey("amount"))
			.body("$", hasKey("fee"))
			.log().status().log().body();
	}

	@Test
	public void testAfterChannelCLIENT() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_AFTER);
		transactionStatusApiRequest.setChannel(ChannelType.CLIENT);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.FUTURE.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testAfterChannelATM() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_AFTER);
		transactionStatusApiRequest.setChannel(ChannelType.ATM);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.PENDING.name()))
			.body("$", hasKey("amount"))
			.body("$", not(hasKey("fee")))
			.log().status().log().body();
	}

	@Test
	public void testAfterChannelINTERNAL() {

		TransactionStatusApiRequest transactionStatusApiRequest = new TransactionStatusApiRequest();
		transactionStatusApiRequest.setReference(FAKE_TRANSACTION_AFTER);
		transactionStatusApiRequest.setChannel(ChannelType.INTERNAL);

		// @formatter:off
		RequestSpecification requestSpecification = given()
			.contentType(ContentType.JSON)
			.body(transactionStatusApiRequest).log().uri().log().body();
        requestSpecification.when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_STATUS_MAPPING )

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("status",equalTo(TransactionStatus.FUTURE.name()))
			.body("$", hasKey("amount"))
			.body("$", hasKey("fee"))
			.log().status().log().body();
	}



}
