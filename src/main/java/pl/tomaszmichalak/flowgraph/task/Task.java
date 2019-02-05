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
package pl.tomaszmichalak.flowgraph.task;

import java.util.List;
import java.util.Objects;
import pl.tomaszmichalak.flowgraph.fragment.Fragment;

public class Task {

  private List<Fragment> fragments;

  public Task(List<Fragment> fragments) {
    this.fragments = fragments;
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Task task = (Task) o;
    return Objects.equals(fragments, task.fragments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fragments);
  }

  @Override
  public String toString() {
    return "Task{" +
        "fragments=" + fragments +
        '}';
  }
}
