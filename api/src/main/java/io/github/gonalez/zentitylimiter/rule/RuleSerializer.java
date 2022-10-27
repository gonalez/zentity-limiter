/*
 * Copyright 2022 - Gaston Gonzalez (Gonalez). and contributors.
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
package io.github.gonalez.zentitylimiter.rule;

import javax.annotation.Nullable;
import java.io.IOException;

/** Interface for serialize and deserialize rules. */
public interface RuleSerializer {
  @FunctionalInterface
  interface Visitor {
    static Visitor of(Visitor... visitors) {
      return (rule, name, valueType, value) -> {
        for (Visitor visitor : visitors) {
          visitor.visitValue(rule, name, valueType, value);
        }
      };
    }

    void visitValue(
        Rule rule, String valueName,
        Class<?> valueType, Object value);
  }

  default void addListener(RuleSerializerListener listener) {}

  void serialize(Rule rule, RuleSerializerContext context) throws IOException;

  Rule deserialize(RuleSerializerContext context, @Nullable Visitor visitor) throws IOException;
}
