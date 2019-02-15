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
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.tomaszmichalak.flowgraph.engine.FragmentEvent.Status;
import pl.tomaszmichalak.flowgraph.fragment.Flow;
import pl.tomaszmichalak.flowgraph.fragment.Fragment;
import pl.tomaszmichalak.flowgraph.task.Task;

@ExtendWith(VertxExtension.class)
class TaskEngineTest {

  private TaskEngine engine;

  @BeforeEach
  public void setUp() {
    engine = new TaskEngine();
  }

  @Test
  public void execute_whenOneFragmentWithOneProcessor_expectOneSuccessFragmentEventWithOneEntryInPayload(
      VertxTestContext testContext, Vertx vertx) throws Throwable {
    // given
    Flow flow = new Flow().setProcessor("task-1");
    Task divisibleTask = new Task(Collections.singletonList(new Fragment(flow, "body")));

    // when
    Single<List<FragmentEvent>> execute = engine.execute(vertx.getDelegate(), divisibleTask);

    // then
    execute.subscribe(
        onSuccess -> {
          try {
            Assertions.assertEquals(1, onSuccess.size());
            Assertions.assertEquals(Status.SUCCESS, onSuccess.get(0).getStatus());
            Assertions.assertTrue(onSuccess.get(0).getPayload().containsKey("task-1"));
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, onError -> {
          testContext.failNow(onError);
        });

    Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void execute_whenOneFragmentWithTwoProcessors_expectOneSuccessFragmentEventWithTwoEntriesInPayload(
      VertxTestContext testContext, Vertx vertx) throws Throwable {
    // given
    Flow flow = new Flow().setProcessor("task-1").setNext(new Flow().setProcessor("task-2"));
    Task divisibleTask = new Task(Collections.singletonList(new Fragment(flow, "body")));

    // when
    Single<List<FragmentEvent>> execute = engine.execute(vertx.getDelegate(), divisibleTask);

    // then
    execute.subscribe(
        onSuccess -> {
          try {
            Assertions.assertEquals(1, onSuccess.size());
            Assertions.assertEquals(Status.SUCCESS, onSuccess.get(0).getStatus());
            Assertions.assertTrue(onSuccess.get(0).getPayload().containsKey("task-1"));
            Assertions.assertTrue(onSuccess.get(0).getPayload().containsKey("task-2"));
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, onError -> {
          testContext.failNow(onError);
        });

    Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void execute_whenOneFragmentWithFirstFailingProcessor_expectOneFailedFragmentEventWithEmptyPayload(
      VertxTestContext testContext, Vertx vertx) throws Throwable {
    // given
    Flow flow = new Flow().setProcessor("failing-1").setNext(new Flow().setProcessor("task-2"));
    Task divisibleTask = new Task(Collections.singletonList(new Fragment(flow, "body")));

    // when
    Single<List<FragmentEvent>> execute = engine.execute(vertx.getDelegate(), divisibleTask);

    // then
    execute.subscribe(
        onSuccess -> {
          try {
            Assertions.assertEquals(1, onSuccess.size());
            Assertions.assertEquals(Status.FAILED, onSuccess.get(0).getStatus());
            Assertions.assertTrue(onSuccess.get(0).getPayload().isEmpty());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, onError -> {
          testContext.failNow(onError);
        });

    Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void execute_whenOneFragmentWithSecondFailingProcessor_expectOneFailedFragmentEventWithEmptyPayload(
      VertxTestContext testContext, Vertx vertx) throws Throwable {
    // given
    Flow flow = new Flow().setProcessor("task-1").setNext(
        new Flow().setProcessor("failing-1").setNext(
            new Flow().setProcessor("task-2")));
    Task divisibleTask = new Task(Collections.singletonList(new Fragment(flow, "body")));

    // when
    Single<List<FragmentEvent>> execute = engine.execute(vertx.getDelegate(), divisibleTask);

    // then
    execute.subscribe(
        onSuccess -> {
          try {
            Assertions.assertEquals(1, onSuccess.size());
            Assertions.assertEquals(Status.FAILED, onSuccess.get(0).getStatus());
            Assertions.assertTrue(onSuccess.get(0).getPayload().containsKey("task-1"));
            Assertions.assertFalse(onSuccess.get(0).getPayload().containsKey("task-2"));
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, onError -> {
          testContext.failNow(onError);
        });

    Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void execute_whenOneFragmentWithOneProcessor_expectFallbackEntryInPayload(
      VertxTestContext testContext, Vertx vertx) throws Throwable {
    // given
    engine = new TaskEngine();
    Flow flow = new Flow().setProcessor("circuit-breaker-fallback").setNext(
        new Flow().setProcessor("circuit-breaker-fallback").setNext(
            new Flow().setProcessor("task-2")));

    Task divisibleTask = new Task(Collections.singletonList(new Fragment(flow, "body")));

    // when
    Single<List<FragmentEvent>> execute = engine.execute(vertx.getDelegate(), divisibleTask);

    // then
    execute.subscribe(
        onSuccess -> testContext.verify(() -> {
          try {
            Assertions.assertEquals(1, onSuccess.size());
            Assertions
                .assertEquals("my-fallback-value",
                    onSuccess.get(0).getPayload().getString("circuit-breaker-fallback"));
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }), testContext::failNow);
    Assertions.assertTrue(testContext.awaitCompletion(120, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }


}