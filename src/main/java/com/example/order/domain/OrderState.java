package com.example.order.domain;

public record OrderState(String customerId, int amount, Status status) {

  public enum Status {
    PAYMENT_PENDING,
    SHIPPING,
    COMPLETED
  }

  public OrderState withStatus(Status newStatus) {
    return new OrderState(customerId, amount, newStatus);
  }
}
