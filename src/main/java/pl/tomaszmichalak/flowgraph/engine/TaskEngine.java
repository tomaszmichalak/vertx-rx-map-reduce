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

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import java.util.ArrayList;
import java.util.List;
import pl.tomaszmichalak.flowgraph.task.Task;

public class TaskEngine {

  public Single<List<FragmentEvent>> execute(Vertx vertx, Task payload) {
    return Flowable.fromIterable(payload.getFragments())
        .map(FragmentEvent::new)
        .map(event -> event.execute(vertx))
        .flatMap(Single::toFlowable)
        .reduce(new ArrayList<>(), (list, item) -> {
          list.add(item);
          return list;
        });
  }

}
