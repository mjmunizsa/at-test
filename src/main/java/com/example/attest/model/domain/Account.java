package com.example.attest.model.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TB_ACCOUNTS")
public class Account {

	@Id
	@Column(name = "IBAN", nullable = false, length = 34)
	private String iban;

	@Column(name = "balance")
	private BigDecimal balance = new BigDecimal(0);


}
