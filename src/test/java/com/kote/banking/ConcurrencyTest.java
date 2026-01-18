package com.kote.banking;

import com.kote.banking.dto.TransactionRequestDto;
import com.kote.banking.entity.Account;
import com.kote.banking.entity.User;
import com.kote.banking.entity.enums.*;
import com.kote.banking.repository.AccountRepository;
import com.kote.banking.repository.UserRepository;
import com.kote.banking.security.TenantContext;
import com.kote.banking.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ConcurrencyTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionService transactionService;

    private final String TEST_TENANT = "tenant_1";
    private final String SOURCE_ACC = "ACC_SOURCE";
    private final String TARGET_ACC = "ACC_TARGET";

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + TEST_TENANT);
        }
        TenantContext.setTenantName(TEST_TENANT);

        User user = new User();
        user.setName("TestUser");
        user.setEmail("test.ge");
        user.setPassword("pass");
        user.setRole(UserRole.ROLE_USER);
        userRepository.save(user);

        Account source = new Account();
        source.setAccountNumber(SOURCE_ACC);
        source.setCurrency(Currency.GEL);
        source.setBalance(BigDecimal.valueOf(100));
        source.setAccountStatus(AccountStatus.ACTIVE);
        source.setUser(user);
        accountRepository.save(source);

        Account target = new Account();
        target.setAccountNumber(TARGET_ACC);
        target.setCurrency(Currency.GEL);
        target.setBalance(BigDecimal.ZERO);
        target.setAccountStatus(AccountStatus.ACTIVE);
        target.setUser(user);
        accountRepository.save(target);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testParallelTransferRaceCondition() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Future<String> future = executorService.submit(() -> {
                try {
                    latch.await();
                    TenantContext.setTenantName(TEST_TENANT);
                    TransactionRequestDto request = new TransactionRequestDto();
                    request.setSourceAccountId(SOURCE_ACC);
                    request.setTargetAccountId(TARGET_ACC);
                    request.setAmount(BigDecimal.valueOf(20));
                    request.setCurrency(Currency.GEL);
                    request.setTransactionType(TransactionType.TRANSFER);
                    transactionService.transfer(request);

                    return "SUCCESS";
                } catch (Exception e) {
                    return "FAILED";
                } finally {
                    TenantContext.clear();
                }
            });
            futures.add(future);
        }
        latch.countDown();

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        int successCount = 0;
        int failCount = 0;

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                if (result.equals("SUCCESS")) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Successful transfers: " + successCount);
        System.out.println("Failed transfers: " + failCount);

        TenantContext.setTenantName(TEST_TENANT);
        Account updatedSource = accountRepository.findByAccountNumber(SOURCE_ACC).orElseThrow();

        Assertions.assertEquals(0, updatedSource.getBalance().intValue());
        Assertions.assertEquals(5, successCount);
    }
}