package com.example.attest.model.api;

import com.example.attest.model.domain.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class TransactionStatusApiResponse {

	private String reference;

	private TransactionStatus status;

	private BigDecimal amount;

	private BigDecimal fee;

}
