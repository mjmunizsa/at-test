package com.example.attest.controller;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController extends AbstractApiController {

	static final String RESOURCE_MAPPING = "/transaction";
	static final String RESOURCE_ID = "/{reference}";

	private TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {

		this.transactionService = transactionService;
	}


	@GetMapping(RESOURCE_MAPPING + RESOURCE_ID)
	public TransactionApi getTransactionByReference(@PathVariable("reference") String reference) {

		return transactionService.findByReference(reference);
	}

	@PostMapping(RESOURCE_MAPPING)
	public ResponseEntity<TransactionApi> postTransaction(@RequestBody TransactionApi transactionApi) {

		return new ResponseEntity<>(transactionService.create(transactionApi), HttpStatus.CREATED);
	}

}
