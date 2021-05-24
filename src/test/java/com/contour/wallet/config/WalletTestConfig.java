package com.contour.wallet.config;

import com.contour.wallet.controller.WalletController;
import com.contour.wallet.persistance.AssignmentWalletDao;
import com.contour.wallet.persistance.WalletRepository;
import com.contour.wallet.service.AssignmentWalletService;
import com.contour.wallet.service.WalletService;
import com.contour.wallet.utils.Calculator;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class WalletTestConfig {

    @Bean
    public WalletController walletController(){
        return new WalletController();
    }

    @Bean
    public AssignmentWalletService assignmentWalletService(){
        return new AssignmentWalletService();
    }

    @Bean
    public AssignmentWalletDao assignmentWalletDao() { return new AssignmentWalletDao(); }

    @Bean
    public WalletRepository walletRepository() { return Mockito.mock(WalletRepository.class); }

    @Bean
    public Calculator calculator() { return new Calculator(); }
}
