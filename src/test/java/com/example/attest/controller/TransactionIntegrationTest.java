package com.example.attest.controller;


import static com.example.attest.controller.AbstractApiController.BASE_MAPPING;
import static com.example.attest.controller.TransactionController.RESOURCE_TRANSACTION_MAPPING;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

import com.example.attest.model.api.TransactionApi;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
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
public class TransactionIntegrationTest {

	@LocalServerPort
	protected int port;

	private static final String FAKE_TRANSACTION_REFERENCE = "FAKE_REFERENCE";
	private static final String FAKE_TRANSACTION_ALREADY_STORED = "FAKE_TRANSACTION_ALREADY_STORED";
	private static final String FAKE_TRANSACTION_NOT_EXISTS = "FAKE_TRANSACTION_NOT_EXISTS";
	private static final String FAKE_ACCOUNT_IBAN = "FAKE_ACCOUNTIBAN";
	private static final BigDecimal FAKE_AMOUNT_IBAN = new BigDecimal(100.50);

	TransactionApi transactionApiExists;

	TransactionApi transactionApiNotExists;


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

	}


	@Test
	public void postTransactionMinimumDataShouldStoreTransaction() {

		TransactionApi transactionApi = new TransactionApi();
		transactionApi.setReference(FAKE_TRANSACTION_REFERENCE);
		transactionApi.setAmount(FAKE_AMOUNT_IBAN);
		transactionApi.setAccountIban(FAKE_ACCOUNT_IBAN);

		// @formatter:off
		given()
			.contentType(ContentType.JSON)
			.body(transactionApi)

        .when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.CREATED.value())
			.body("$",hasKey("reference"))
			.body("$",hasKey("amount"))
			.body("$",hasKey("account_iban"));

		// @formatter:on
	}

	@Test
	public void postTransactionShouldReturnBadRequestWhenNotMandatoryFields() {

		TransactionApi transactionApi = new TransactionApi();
		transactionApi.setReference(FAKE_TRANSACTION_REFERENCE);

		// @formatter:off
		given()
			.contentType(ContentType.JSON)
			.body(transactionApi)

        .when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.BAD_REQUEST.value());

		// @formatter:on
	}

	@Test
	public void postTransactionShouldReturnUnprocessableEntityWhenDuplicatedTransaction() {

		// @formatter:off
		given()
			.contentType(ContentType.JSON)
			.body(transactionApiExists)

        .when()
			.post(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING)

        .then()
			.statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

		// @formatter:on
	}

	@Test
	public void getTransactionShouldReturnTransactionAlreadyStored() {

		// @formatter:off
		given()
			.contentType(ContentType.JSON)

        .when()
			.get(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING + "/" + FAKE_TRANSACTION_ALREADY_STORED)

        .then()
			.statusCode(HttpStatus.OK.value())
			.body("reference",equalTo(FAKE_TRANSACTION_ALREADY_STORED));

		// @formatter:on
	}

	@Test
	public void getTransactionShouldReturnNotFoundWhenTransactionIsNotFound() {

		// @formatter:off
		given()
			.contentType(ContentType.JSON)

        .when()
			.get(BASE_MAPPING + RESOURCE_TRANSACTION_MAPPING + "/" + FAKE_TRANSACTION_NOT_EXISTS)

        .then()
			.statusCode(HttpStatus.NOT_FOUND.value());

		// @formatter:on
	}


}
