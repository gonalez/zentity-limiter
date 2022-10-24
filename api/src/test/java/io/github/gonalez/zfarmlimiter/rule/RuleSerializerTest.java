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
package io.github.gonalez.zfarmlimiter.rule;

import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zfarmlimiter.registry.ObjectRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/** Tests for {@link RuleSerializer}. */
public class RuleSerializerTest {

  @Test
  public void testWithFieldSerializer() throws Exception {
    ImmutableSet<String> expectedEntities =
        ImmutableSet.of(
            "ZOMBIE",
            "PIG");

    ObjectRegistry objectRegistry =
        ObjectRegistry.newBuilder()
            .add("entities", ImmutableSet.class, expectedEntities)
            .add("allowedWorlds", ImmutableSet.class, ImmutableSet.of())
            .add("radius", double.class, 5.00)
            .add("maxAmount", int.class, 0)
            .build();

    RuleSerializer ruleSerializer = new AbstractBuilderRuleSerializer() {
      @Override
      protected Class<? extends Rule> ruleType() {
        return Rule.class;
      }

      @Override
      public void serialize(Rule rule, RuleSerializerContext context) throws IOException {

      }
    };

    Rule deserializedRule = ruleSerializer.deserialize(RuleSerializerContext.of(objectRegistry));
    Assertions.assertEquals(expectedEntities, deserializedRule.entities());
  }
}
