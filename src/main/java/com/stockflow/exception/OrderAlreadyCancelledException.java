
package com.stockflow.exception;

public class OrderAlreadyCancelledException extends RuntimeException {

    public OrderAlreadyCancelledException(String message) {
        super(message);
    }
}
