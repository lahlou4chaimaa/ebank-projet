
package org.lahlou.net.ebankservice.services;

import org.lahlou.net.ebankservice.entities.BankAccount;
import org.lahlou.net.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {

    private final BankAccountRepository accountRepository;

    public EbankService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Récupérer tous les comptes
    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    // Récupérer les comptes d'un client
    public List<BankAccount> getAllBankAccounts(Long id) {
        return accountRepository.findByCustomerId(id);
    }

    // Récupérer un compte par son identifiant
    public BankAccount getBankAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bank account not found"));
    }

    // Ajouter un compte
    public BankAccount save(BankAccount bankAccount) {
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        return accountRepository.save(bankAccount);
    }
}
