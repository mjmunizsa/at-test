package com.example.attest.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionApi {

	@Size(max = 6)
	private String reference;

	@NotNull
	@Size(max = 34)
	@JsonProperty("account_iban")
	private String accountIban;

	private LocalDateTime date;

	@NotNull
	private Double amount;

	private Double fee;

	private String description;

}
