package com.example.attest.service;

public interface AccountService {

	Double getBalance(String iban);

	void updateBalance(String iban, Double balance);

}
