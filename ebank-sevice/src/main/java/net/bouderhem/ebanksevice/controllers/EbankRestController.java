package net.bouderhem.ebanksevice.controllers;

import net.bouderhem.ebanksevice.entities.BankAccount;
import net.bouderhem.ebanksevice.services.EbankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EbankRestController {
    private EbankService ebankService;
    public EbankRestController(EbankService ebankService) {
        this.ebankService = ebankService;
    }

    @GetMapping("/accounts")
    public List<BankAccount> getAllBankAccounts(){
        return ebankService.getAllBankAccounts();
    }

    @GetMapping("/accounts/{id}")
    public BankAccount getBankAccountById(@PathVariable String id){
        return ebankService.getBankAccountById(id);
    }
    @PostMapping("/accounts")
    public BankAccount save(@RequestBody BankAccount bankAccount){
        return ebankService.save(bankAccount);
    }
}
