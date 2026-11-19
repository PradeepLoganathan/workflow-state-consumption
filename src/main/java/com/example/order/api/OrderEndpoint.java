package com.example.order.api;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import com.example.order.application.OrderWorkflow;
import com.example.order.application.OrderWorkflow.StartOrder;
import com.example.order.application.OrdersView;
import com.example.order.application.OrdersView.OrderRows;

@HttpEndpoint("/orders")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class OrderEndpoint {

  private final ComponentClient componentClient;

  public OrderEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{orderId}")
  public Done createOrder(String orderId, StartOrder body) {
    return componentClient
        .forWorkflow(orderId)
        .method(OrderWorkflow::start)
        .invoke(body);
  }

  @Get("/by-customer/{customerId}")
  public OrderRows findByCustomer(String customerId) {
    return componentClient
        .forView()
        .method(OrdersView::getByCustomer)
        .invoke(customerId);
  }

  @Get("/by-status/{status}")
  public OrderRows findByStatus(String status) {
    return componentClient
        .forView()
        .method(OrdersView::getByStatus)
        .invoke(status);
  }
}
