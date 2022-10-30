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

import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zentitylimiter.registry.ObjectRegistry;
import io.github.gonalez.zentitylimiter.rule.Rule;

import javax.annotation.Nullable;

/** Contains more information about an {@link Rule}. */
public interface RuleDescription {
  /** Provides thar provides {@link RuleDescription}s for a given rule. */
  interface Provider {
    @Nullable
    RuleDescription provide(@Nullable Rule rule);
  }

  /** The rule for which this description is intended for. */
  Rule getRule();

  /** @return a map of all filters that this rule description has. */
  ImmutableMap<EntityExtractorFilter<?>, ObjectRegistry> getFilters();

  /** Merges {@code this} rule description with {@code ruleDescription}. */
  void mergeWith(RuleDescription ruleDescription);

  /** @return {@code true} if this rule description supports cloning. */
  default boolean supportsClone() {
    return false;
  }

  /** Clones {@code this} instance, by default this method throws an {@link UnsupportedOperationException}. */
  default RuleDescription createClone() {
    throw new UnsupportedOperationException("not implemented");
  }
}
