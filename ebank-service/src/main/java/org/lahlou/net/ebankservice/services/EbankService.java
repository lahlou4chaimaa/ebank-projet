package org.lahlou.net.ebankservice.services;

import org.lahlou.net.ebankservice.EbankServiceApplication;
import org.lahlou.net.ebankservice.entities.BankAccount;
import org.lahlou.net.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbankService

{
    private BankAccountRepository accountRepository;
    public EbankService(BankAccountRepository accountRepository)
    {
        this.accountRepository = accountRepository;
    }
    public List<BankAccount> getAllBankAccounts(Long id)
    {
        return accountRepository.findByCustomerId(id);
    }
    public BankAccount getBankAccountById (String id)
    {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
    }
}
