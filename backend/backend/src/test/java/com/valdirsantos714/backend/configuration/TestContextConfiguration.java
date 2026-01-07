package com.valdirsantos714.backend.configuration;

import com.valdirsantos714.backend.adapters.out.repository.UserRepositoryAdapter;
import com.valdirsantos714.backend.application.service.ExpenseServiceImpl;
import com.valdirsantos714.backend.application.service.FinancialSummaryServiceImpl;
import com.valdirsantos714.backend.application.service.IncomeServiceImpl;
import com.valdirsantos714.backend.application.service.UserServiceImpl;
import com.valdirsantos714.backend.infrastructure.security.SecurityFilter;
import com.valdirsantos714.backend.infrastructure.security.TokenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestContextConfiguration {

    @Bean
    @Primary
    public UserServiceImpl userService() {
        return mock(UserServiceImpl.class);
    }

    @Bean
    @Primary
    public ExpenseServiceImpl expenseService() {
        return mock(ExpenseServiceImpl.class);
    }

    @Bean
    @Primary
    public FinancialSummaryServiceImpl financialSummaryService() {
        return mock(FinancialSummaryServiceImpl.class);
    }

    @Bean
    @Primary
    public IncomeServiceImpl incomeService() {
        return mock(IncomeServiceImpl.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return mock(AuthenticationManager.class);
    }

    @Bean
    public TokenService tokenService() {
        return mock(TokenService.class);
    }

    @Bean
    public SecurityFilter securityFilter() {
        return mock(SecurityFilter.class);
    }

    @Bean
    public UserRepositoryAdapter userRepositoryAdapter() {
        return mock(UserRepositoryAdapter.class);
    }
}