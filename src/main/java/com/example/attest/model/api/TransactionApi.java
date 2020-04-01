package com.example.attest.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class TransactionApi {

	@Size(max = 34)
	private String reference;

	@NotNull
	@Size(max = 34)
	@JsonProperty("account_iban")
	private String accountIban;

	private LocalDateTime date;

	@NotNull
	private BigDecimal amount;

	private BigDecimal fee;

	private String description;

}
