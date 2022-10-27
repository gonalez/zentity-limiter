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

/** {@inheritDoc}. */
public interface EntityChecker {
  /** The result of {@link #check(Entity, Rule)}. */
  enum ResultType {
    // Entity check was successful
    SUCCEED,
    // Some error or failure
    FAILED,
    // Event was cancelled
    EVENT_CANCELLED,
    // The number of entities found is less than the limit set by the rule
    TOO_FEW_ENTITIES,
  }

  /**
   * Checks the given entity for the given rule, looks for entities near the radius set by the rule and checks
   * if there are more than the limit set by the rule, and handles accordingly.
   */
  ResultType check(Entity entity, Rule rule) throws EntityCheckerException;

  /**
   * Initializes this checker with the given plugin, it may not be necessary to call this method,
   * but it is possible to have extra functionality if it is called.
   */
  default void init(Plugin plugin) {}

  /** @return {@code true} if this checker has been {@link #init(Plugin) initialized}. */
  default boolean isInitialized() {
    return false;
  }
}
