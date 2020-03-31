package com.example.attest.model.api;

import com.example.attest.model.domain.TransactionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionStatusApiResponse {

	private String reference;

	private TransactionStatus status;

	private Double amount;

	private Double fee;

}
