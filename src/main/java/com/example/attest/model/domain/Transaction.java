package com.example.attest.model.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_TRANSACTIONS")
public class Transaction {


	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "ID", nullable = false, length = 36)
	private String id;

	@Column(name = "REFERENCE", nullable = false, unique = true, length = 6)
	private String reference;

	@Column(name = "IBAN", nullable = false, length = 34)
	private String accountIban;

	@Column(name = "DATE")
	private LocalDateTime date;

	@Column(name = "AMOUNT", nullable = false)
	private Double amount;

	@Column(name = "FEE")
	private Double fee;

	@Column(name = "DESCRIPTION")
	private String description;


}
