package com.example.order.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.order.domain.OrderState;
import java.util.Collection;

@Component(id = "orders-view")
public class OrdersView extends View {

  public record OrderRow(String orderId, String customerId, int amount, String status) {}

  public record OrderRows(Collection<OrderRow> orders) {}

  @Query("SELECT * AS orders FROM orders WHERE customerId = :customerId")
  public QueryEffect<OrderRows> getByCustomer(String customerId) {
    return queryResult();
  }

  @Query("SELECT * AS orders FROM orders WHERE status = :status")
  public QueryEffect<OrderRows> getByStatus(String status) {
    return queryResult();
  }

  @Consume.FromWorkflow(OrderWorkflow.class)
  public static class OrdersUpdater extends TableUpdater<OrderRow> {

    public Effect<OrderRow> onUpdate(OrderState state) {
      var orderId = updateContext().eventSubject().orElse("");
      return effects().updateRow(
          new OrderRow(orderId, state.customerId(), state.amount(), state.status().name())
      );
    }
  }
}
