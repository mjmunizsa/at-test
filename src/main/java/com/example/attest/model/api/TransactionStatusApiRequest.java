package com.example.attest.model.api;

import com.example.attest.model.domain.ChannelType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionStatusApiRequest {

	@NotNull
	@Size(max = 34)
	private String reference;

	@Valid
	@Enumerated(EnumType.STRING)
	private ChannelType channel;


}
