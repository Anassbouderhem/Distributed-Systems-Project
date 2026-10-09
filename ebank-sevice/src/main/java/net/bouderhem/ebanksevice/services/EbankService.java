package net.bouderhem.ebanksevice.services;

import net.bouderhem.ebanksevice.entities.BankAccount;
import net.bouderhem.ebanksevice.repository.BankAccountRepository;

import java.util.List;

public class EbankService {
    private BankAccountRepository accountRepository;

    public EbankService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll();
    }
    public BankAccount getBankAccountById(String id){
        return accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account not found"));
    }

    public BankAccount save(BankAccount bankAccount){
        return accountRepository.save(bankAccount);
    }
}
