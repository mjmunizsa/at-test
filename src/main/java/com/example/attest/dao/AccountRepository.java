package com.example.attest.dao;

import com.example.attest.model.domain.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

	Optional<Account> findByIban(String iban);

}
