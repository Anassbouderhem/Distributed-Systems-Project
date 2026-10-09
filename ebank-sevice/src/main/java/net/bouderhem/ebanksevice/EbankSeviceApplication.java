package net.bouderhem.ebanksevice;

import net.bouderhem.ebanksevice.entities.BankAccount;
import net.bouderhem.ebanksevice.services.EbankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class EbankSeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankSeviceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(EbankService ebankService){
        return args -> {
            for (int i = 1; i<=3; i++){
               for (int j = 0; j<5; j++){
                   ebankService.save(BankAccount.builder()
                                   .type(Math.random()>0.5? "CURRENT-ACCOUNT":"SAVING-ACCOUNT")
                                   .balance(1200+Math.random()*70000)
                                   .customerId(i)
                           .build());
               }
            }
        };
    }
}
