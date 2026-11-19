package com.example.order.application;

import static akka.Done.done;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.workflow.Workflow;
import com.example.order.domain.OrderState;
import com.example.order.domain.OrderState.Status;

@Component(id = "order-workflow")
public class OrderWorkflow extends Workflow<OrderState> {

  public record StartOrder(String customerId, int amount) {}

  public Effect<Done> start(StartOrder cmd) {
    if (currentState() != null) {
      return effects().reply(done());
    }
    var initialState = new OrderState(cmd.customerId(), cmd.amount(), Status.PAYMENT_PENDING);
    return effects()
        .updateState(initialState)
        .transitionTo(OrderWorkflow::chargePaymentStep)
        .thenReply(done());
  }

  private StepEffect chargePaymentStep() {
    return stepEffects()
        .updateState(currentState().withStatus(Status.SHIPPING))
        .thenTransitionTo(OrderWorkflow::shipOrderStep);
  }

  private StepEffect shipOrderStep() {
    return stepEffects()
        .updateState(currentState().withStatus(Status.COMPLETED))
        .thenEnd();
  }
}
