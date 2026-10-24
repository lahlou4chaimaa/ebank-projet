
package org.lahlou.net.ebankservice.services;

import org.lahlou.net.ebankservice.entities.BankAccount;
import org.lahlou.net.ebankservice.feign.CustomerRestClient;
import org.lahlou.net.ebankservice.model.Customer;
import org.lahlou.net.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Service
public class EbankService {

    private final BankAccountRepository accountRepository;
    private final CustomerRestClient customerRestClient;

    public EbankService(BankAccountRepository accountRepository,
                        CustomerRestClient customerRestClient) {
        this.accountRepository = accountRepository;
        this.customerRestClient = customerRestClient;
    }

    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    public List<BankAccount> getAllBankAccounts(Long id) {
        return accountRepository.findByCustomerId(id);
    }

    public BankAccount getBankAccountById(String id) {
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bank account not found"));

        bankAccount.setCustomer(
                customerRestClient.getCustomerById(bankAccount.getCustomerId()));

        return bankAccount;
    }

    public BankAccount save(BankAccount bankAccount) {
        try {
            Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            return accountRepository.save(bankAccount);
        }catch (Exception e ){
            throw new RuntimeException(e.getMessage());

        }


    }
}