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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RuleSerializerListeningRuleCollection implements RuleCollection {
  private final Queue<Rule> serializedRules = new ConcurrentLinkedQueue<>();

  public RuleSerializerListeningRuleCollection(
      RuleSerializer ruleSerializer) {
    checkNotNull(ruleSerializer);
    ruleSerializer.addListener(
        new RuleSerializerListener() {
          @Override
          public void onDeserialize(Rule rule, RuleSerializerContext context) {
            serializedRules.add(rule);
          }
        });
  }

  @Override
  public synchronized ImmutableSet<Rule> getRules() {
    return ImmutableSet.copyOf(serializedRules);
  }

  @Override
  public synchronized void invalidateCaches() {
    serializedRules.clear();
  }
}
