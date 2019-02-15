/*
 * Copyright (C) 2019 Tomasz Michalak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.tomaszmichalak.flowgraph.fragment;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;
import java.util.HashMap;
import java.util.Map;
import pl.tomaszmichalak.flowgraph.engine.FragmentEvent;

public class FragmentProcessorProxy {

  private Map<String, FragmentProcessor> jobs;

  public FragmentProcessorProxy(Vertx vertx) {
    this.jobs = init(vertx);
  }

  public Single<FragmentEvent> callProcessor(String address, FragmentEvent event) {

    return jobs.get(address).process(event);
  }

  private Map<String, FragmentProcessor> init(Vertx vertx) {
    Map<String, FragmentProcessor> registered = new HashMap<>();
    registerProcessorProxy(registered, "task-1");
    registerProcessorProxy(registered, "task-2");
    registerProcessorProxy(registered, "task-3");
    registerFailingProcessorProxy(registered, "failing-1");
    registerLongRunningProcessorWithFallbackProxy(registered, "circuit-breaker-fallback", vertx);
    return registered;
  }

  public void registerProcessorProxy(Map<String, FragmentProcessor> jobs, String address) {
    jobs.put(address, flowContext -> new Single<FragmentEvent>() {
      @Override
      protected void subscribeActual(SingleObserver<? super FragmentEvent> observer) {
        String body = flowContext.getFragment().getBody();
        // do some logic with flow context
        flowContext.getPayload().put(address, address + "-value");
        System.out.println(
            "[SuccessProcessor][" + address + "] responds with value: " + address + "-value");
        observer.onSuccess(flowContext);
      }
    });
  }

  public void registerFailingProcessorProxy(Map<String, FragmentProcessor> jobs, String address) {
    jobs.put(address, flowContext -> new Single<FragmentEvent>() {
      @Override
      protected void subscribeActual(SingleObserver<? super FragmentEvent> observer) {
        System.out.println("[FailingProcessor][" + address + "] called!");
        observer.onError(new IllegalStateException());
      }
    });
  }

  public void registerLongRunningProcessorWithFallbackProxy(Map<String, FragmentProcessor> jobs,
      String address, Vertx vertx) {
    jobs.put(address, flowContext -> new Single<FragmentEvent>() {
      CircuitBreaker breaker = CircuitBreaker.create("my-circuit-breaker", vertx,
          new CircuitBreakerOptions()
              .setMaxFailures(1) // number of failure before opening the circuit
              .setTimeout(500) // consider a failure if the operation does not succeed in time
              .setFallbackOnFailure(true) // do we call the fallback on failure
              .setResetTimeout(10000) // time spent in open state before attempting to re-try
      ).openHandler(v -> {
        System.out.println("Circuit opened");
      }).closeHandler(v -> {
        System.out.println("Circuit closed");
      });

      @Override
      protected void subscribeActual(SingleObserver<? super FragmentEvent> observer) {
        breaker.executeWithFallback(future -> vertx.setTimer(2000, time -> {
          flowContext.getPayload().put(address, address + "-value");
          System.out.println(
              "[SuccessProcessor][" + address + "] responds with value: " + address + "-value");
          observer.onSuccess(flowContext);
          future.complete(flowContext);
        }), v -> {
          flowContext.getPayload().put(address, "my-fallback-value");
          observer.onSuccess(flowContext);
          return flowContext;
        });
      }
    });
  }

}
