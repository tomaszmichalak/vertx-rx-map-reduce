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
package pl.tomaszmichalak.flowgraph.engine;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import pl.tomaszmichalak.flowgraph.fragment.Fragment;
import pl.tomaszmichalak.flowgraph.fragment.FragmentProcessorProxy;

public class FragmentEvent {

  private Fragment fragment;

  private Status status;

  private JsonObject payload;

  FragmentEvent(Fragment fragment) {
    this.fragment = fragment;
    this.status = Status.UNPROCESSED;
    this.payload = new JsonObject();
  }

  public Fragment getFragment() {
    return fragment;
  }

  public Status getStatus() {
    return status;
  }

  public FragmentEvent setStatus(Status status) {
    this.status = status;
    return this;
  }

  public JsonObject getPayload() {
    return payload;
  }

  public Single<FragmentEvent> execute() {
    return Single.just(this)
        .flatMap(
            currentEvent -> new FragmentProcessorProxy()
                .callProcessor(currentEvent.getFragment().getFlow().getProcessor(), currentEvent)
                .doOnSuccess(processedEvent -> processedEvent.getFragment()
                    .setBody(processedEvent.getPayload().encode()))
                .doOnSuccess(processedEvent -> processedEvent.setStatus(Status.SUCCESS))
                .onErrorResumeNext(error -> {
                  currentEvent.setStatus(Status.FAILED);
                  return Single.just(currentEvent);
                }))
        .map(event -> {
          event.getFragment().nextFlow();
          return event;
        })
        .flatMap(event -> {
          if (event.getStatus() == Status.FAILED || event.getFragment().getFlow() == null) {
            return Single.just(event);
          } else {
            return event.execute();
          }
        });
  }

  @Override
  public String toString() {
    return "FragmentEvent{" +
        "fragment=" + fragment +
        ", status=" + status +
        ", payload=" + payload +
        '}';
  }

  public enum Status {
    UNPROCESSED, SUCCESS, FAILED
  }
}
