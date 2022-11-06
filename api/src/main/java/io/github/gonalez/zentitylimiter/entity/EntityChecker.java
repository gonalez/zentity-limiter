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
package io.github.gonalez.zentitylimiter.entity;

import io.github.gonalez.zentitylimiter.rule.Rule;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/** {@inheritDoc}. */
public interface EntityChecker {
  /** The result of {@link #check(Entity, Rule)}. */
  enum ResultType {
    // Entity check was successful
    SUCCEED,
    // Some error or failure
    FAILED,
    // No rule was found
    RULE_NOT_FOUND,
    // Event was cancelled
    EVENT_CANCELLED,
    // The number of entities found is less than the limit set by the rule
    TOO_FEW_ENTITIES,
  }

  /** Checks the given entity for the given rule. */
  ResultType check(Entity entity, @Nullable Rule rule);

  /** Initializes this checker for the given plugin (optional). */
  default void init(Plugin plugin) {}

  /** @return {@code true} if this checker has been {@link #init initialized}. */
  default boolean isInitialized() {
    return false;
  }
}
