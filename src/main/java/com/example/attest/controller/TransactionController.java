package com.example.attest.controller;

import com.example.attest.exception.ServiceException;
import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.api.TransactionStatusApiResponse;
import com.example.attest.service.TransactionService;
import com.example.attest.service.TransactionStatusService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TransactionController extends AbstractApiController {

	public static final String RESOURCE_TRANSACTION_MAPPING = "/transaction";
	public static final String RESOURCE_TRANSACTION_ID = "/{reference}";

	public static final String RESOURCE_TRANSACTION_STATUS_MAPPING = "/transactionStatus";

	public static final String REQUEST_PARAM_ACCOUNT_IBAN = "account_iban";

	private TransactionService transactionService;

	private TransactionStatusService transactionStatusService;


	public TransactionController(TransactionService transactionService, TransactionStatusService transactionStatusService) {

		this.transactionService = transactionService;
		this.transactionStatusService = transactionStatusService;
	}


	@GetMapping(RESOURCE_TRANSACTION_MAPPING + RESOURCE_TRANSACTION_ID)
	public ResponseEntity<TransactionApi> getTransactionByReference(@PathVariable("reference") String reference) {

		try {
			return new ResponseEntity<>(transactionService.findByReference(reference), HttpStatus.OK);
		} catch (ServiceException ex) {
			throw new ResponseStatusException(
				ex.getHttpStatus(), ex.getMessage(), ex);
		}
	}

	@GetMapping(RESOURCE_TRANSACTION_MAPPING)
	public ResponseEntity<Page<TransactionApi>> finTransactionbyAccountIban(
		@RequestParam(REQUEST_PARAM_ACCOUNT_IBAN) String accountIban,
		Pageable pageable) {

		return new ResponseEntity<>(transactionService.findByAccountIban(accountIban, pageable),
			HttpStatus.OK);
	}

	@PostMapping(RESOURCE_TRANSACTION_MAPPING)
	public ResponseEntity<TransactionApi> postTransaction(@RequestBody @Valid TransactionApi transactionApi) {

		try {
			return new ResponseEntity<>(transactionService.create(transactionApi), HttpStatus.CREATED);
		} catch (ServiceException ex) {
			throw new ResponseStatusException(
				ex.getHttpStatus(), ex.getMessage(), ex);
		}
	}

	@PostMapping(RESOURCE_TRANSACTION_STATUS_MAPPING)
	public ResponseEntity<TransactionStatusApiResponse> postTransactionStatus(
		@RequestBody TransactionStatusApiRequest transactionStatusApiRequest) {

		return new ResponseEntity<>(transactionStatusService.getTransactionStatus(transactionStatusApiRequest),
			HttpStatus.OK);
	}


}
