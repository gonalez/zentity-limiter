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

import io.github.gonalez.zentitylimiter.registry.ObjectRegistry;

/** {@link RuleSerializerContext} which delegates to another context. */
public class DelegatingRuleSerializerContext implements RuleSerializerContext {
  private final ObjectRegistry objectRegistry;

  public DelegatingRuleSerializerContext(ObjectRegistry objectRegistry) {
    this.objectRegistry = checkNotNull(objectRegistry);
  }

  @Override
  public <T> T get(String key, Class<T> type) {
    return objectRegistry.get(key, type);
  }

  @Override
  public <T> Iterable<T> getAll(String key, Class<T> type) {
    return objectRegistry.getAll(key, type);
  }

  @Override
  public void merge(Builder objectRegistryBuilder) {
    objectRegistry.merge(objectRegistryBuilder);
  }
}
