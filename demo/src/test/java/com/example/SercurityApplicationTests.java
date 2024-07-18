package com.example;

import com.example.demo.hybrid.entity.TransactionHistoryEntity;
import com.example.demo.hybrid.repository.TransactionHistoryRepository;
import com.example.demo.hybrid.service.iplm.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SercurityApplicationTests {

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveTransaction() {
        try {
            String transactionId = "TXN123456789";
            String sourceAccount = "1234567890";
            String destinationAccount = "0987654321";

            // Invoke the method to test
            transactionService.saveTransaction(transactionId, sourceAccount, destinationAccount);

            // Verify that the save method was called twice (for source and destination accounts)
            verify(transactionHistoryRepository, times(2)).save(any(TransactionHistoryEntity.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
