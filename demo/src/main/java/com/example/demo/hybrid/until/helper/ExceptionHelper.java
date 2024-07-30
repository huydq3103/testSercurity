package com.example.demo.hybrid.until.helper;

import com.example.demo.hybrid.exception.EncryptionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to handle exceptions.
 */
@Slf4j
public class ExceptionHelper {

    /**
     * Executes a callable and handles any exceptions by throwing a custom exception with a message.
     *
     * @param callable     The callable to be executed.
     * @param errorMessage The error message to be used if an exception is thrown.
     * @param <T>          The type of the result.
     * @return The result of the callable.
     * @throws EncryptionException If an exception occurs during the callable execution.
     */
    public static <T> T executeWithExceptionHandling(Callable<T> callable, String errorMessage) {
        try {
            return callable.call();
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new EncryptionException(errorMessage, e);
        }
    }

    @FunctionalInterface
    public interface Callable<T> {
        T call() throws Exception;
    }
}
