package com.example.attest.controller;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.service.TransactionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController extends AbstractApiController {

	private static final String RESOURCE_MAPPING = "/transaction";
	private static final String RESOURCE_ID = "/{reference}";

	private static final String PAGE_NUMBER_DEFAULT = "0";
	private static final String PAGE_SIZE_DEFAULT = "10";
	private static final String SORT_BY_DEFAULT = "amount";
	private static final String DIRECTION_BY_DEFAULT = "ASC";

	private TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {

		this.transactionService = transactionService;
	}


	@GetMapping(RESOURCE_MAPPING + RESOURCE_ID)
	public TransactionApi getTransactionByReference(@PathVariable("reference") String reference) {

		return transactionService.findByReference(reference);
	}

//	@GetMapping(RESOURCE_MAPPING)
//	public ResponseEntity<List<TransactionApi>> getTransactionByAccountIban(@RequestParam("account_iban") String account_iban,
//		@RequestParam(defaultValue = "0", required = false) Integer pageNo,
//		@RequestParam(defaultValue = "10", required = false) Integer pageSize,
//		@RequestParam(defaultValue = "amount", required = false) String sortBy,
//		@RequestParam(defaultValue = "asc", required = false) String order) {
//
//		return transactionService.findByReference(reference);
//	}

//	@GetMapping(RESOURCE_MAPPING)
//	public Page<Transaction> finTransactionbyAccountIban(@RequestParam("account_iban") String account_iban, Pageable pageable) {
//
//		return new PageImpl<>(Collections.emptyList());
//	}

	@GetMapping(RESOURCE_MAPPING)
	public ResponseEntity<List<TransactionApi>> finTransactionsByAccountIbanOrderAmount(
		@RequestParam("account_iban") String account_iban,
		@RequestParam(defaultValue = DIRECTION_BY_DEFAULT, required = false) String direction) {

		return new ResponseEntity<List<TransactionApi>>(transactionService.findByAccountIbanOrderByAmount(account_iban,
			direction),HttpStatus.OK);
	}


	@PostMapping(RESOURCE_MAPPING)
	public ResponseEntity<TransactionApi> postTransaction(@RequestBody TransactionApi transactionApi) {

		return new ResponseEntity<>(transactionService.create(transactionApi), HttpStatus.CREATED);
	}

}
