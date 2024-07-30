package com.example.demo.hybrid.until.log;

import com.example.demo.hybrid.until.constant.LogConstant;
import com.example.demo.hybrid.until.constant.RegexConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class CustomLogger {


    /**
     * Logs a transaction with masked sensitive information.
     *
     * @param transactionId      The transaction ID.
     * @param sourceAccount      The source account number.
     * @param destinationAccount The destination account number.
     */
    public static void logTransaction(String transactionId, String sourceAccount, String destinationAccount ) {
        String maskedTransactionId = maskSensitiveInfo(transactionId);
        String maskedSourceAccount = maskSensitiveInfo(sourceAccount);
        String maskedDestinationAccount = maskSensitiveInfo(destinationAccount);

        MDC.put(LogConstant.LOG_TRANSACTION_ID, maskedTransactionId);
        MDC.put(LogConstant.LOG_ACCOUNT, maskedSourceAccount);
        MDC.put(LogConstant.LOG_DESTINATION_ACCOUNT, maskedDestinationAccount);

        log.info("Saving transaction: TransactionID={}, SourceAccount={}, DestinationAccount={}",
                maskedTransactionId, maskedSourceAccount, maskedDestinationAccount);

        MDC.clear();
    }


    /**
     * Masks sensitive information by replacing with '?'.
     *
     * @param sensitiveData The sensitive data to mask.
     * @return The masked sensitive data.
     */
    private static String maskSensitiveInfo(String sensitiveData) {
        return sensitiveData.replaceAll(RegexConstant.REGEX_DOT, RegexConstant.MASK_CHAR);
    }
}
