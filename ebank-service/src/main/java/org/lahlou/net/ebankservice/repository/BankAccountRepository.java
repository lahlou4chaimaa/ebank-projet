package org.lahlou.net.ebankservice.repository;

import org.lahlou.net.ebankservice.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String>
{
    List<BankAccount> findByCustomerId(Long id);
}
