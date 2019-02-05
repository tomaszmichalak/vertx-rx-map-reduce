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
import java.util.HashMap;
import java.util.Map;
import pl.tomaszmichalak.flowgraph.engine.FragmentEvent;

public class FragmentProcessorProxy {

  private Map<String, FragmentProcessor> jobs;

  public FragmentProcessorProxy() {
    this.jobs = init();
  }

  public Single<FragmentEvent> callProcessor(String address, FragmentEvent event) {
    return jobs.get(address).process(event);
  }

  private Map<String, FragmentProcessor> init() {
    Map<String, FragmentProcessor> registered = new HashMap<>();
    registerProcessorProxy(registered, "task-1");
    registerProcessorProxy(registered, "task-2");
    registerProcessorProxy(registered, "task-3");
    registerFailingProcessorProxy(registered, "failing-1");
    return registered;
  }

  public void registerProcessorProxy(Map<String, FragmentProcessor> jobs, String address) {
    jobs.put(address, flowContext -> new Single<FragmentEvent>() {
      @Override
      protected void subscribeActual(SingleObserver<? super FragmentEvent> observer) {
        String body = flowContext.getFragment().getBody();
        // do some logic with flow context
        flowContext.getPayload().put(address, address + "-value");
        System.out.println("[SuccessProcessor][" + address + "] responds with value: " + address + "-value");
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

}
