package org.lahlou.net.ebankservice.controllers;

import org.lahlou.net.ebankservice.entities.BankAccount;
import org.lahlou.net.ebankservice.services.EbankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class EbankController {

    private final EbankService ebankService;

    public EbankController(EbankService ebankService) {
        this.ebankService = ebankService;
    }
    @GetMapping
    public List<BankAccount> getAllBankAccounts(
            @RequestParam(required = false) Long customerId) {
        if (customerId == null) {
            return ebankService.getAllBankAccounts();
        }
        return ebankService.getAllBankAccounts(customerId);
    }

    @GetMapping("/{id}")
    public BankAccount getBankAccountById(@PathVariable String id) {
        return ebankService.getBankAccountById(id);
    }

    @PostMapping
    public BankAccount save(@RequestBody BankAccount bankAccount) {
        return ebankService.save(bankAccount);
    }
}