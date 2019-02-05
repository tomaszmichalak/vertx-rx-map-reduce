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

import java.util.Objects;

public class Fragment {

  private Flow flow;

  private String body;

  public Fragment(Flow flow, String body) {
    this.flow = flow;
    this.body = body;
  }

  public Flow getFlow() {
    return flow;
  }

  public Fragment nextFlow() {
    this.flow = flow.getNext();
    return this;
  }

  public String getBody() {
    return body;
  }

  public Fragment setBody(String body) {
    this.body = body;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Fragment fragment = (Fragment) o;
    return Objects.equals(flow, fragment.flow) &&
        Objects.equals(body, fragment.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(flow, body);
  }

  @Override
  public String toString() {
    return "Fragment{" +
        "flow=" + flow +
        ", body='" + body + '\'' +
        '}';
  }
}
