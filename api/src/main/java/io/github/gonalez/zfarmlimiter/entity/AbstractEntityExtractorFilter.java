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
package io.github.gonalez.zfarmlimiter.entity;

import io.github.gonalez.zfarmlimiter.registry.ObjectRegistry;

/**
 * Adds an extra type {@code V}, useful if the implementation only needs a single value to do the necessary
 * comparisons. Hence, the allowed method becomes {@link #doAllowed} to be able to use {@code V} together
 * with {@code T}.
 *
 * <p>The code {@code V} argument passed to #doAllowed is determined from the object registry given when
 * calling #allowed, we look for the value using the name of this filter and valueType (V).
 */
public abstract class AbstractEntityExtractorFilter<V, T> implements EntityExtractorFilter<T> {
  public AbstractEntityExtractorFilter() {}

  protected abstract boolean doAllowed(V value, T type);

  protected abstract Class<V> valueType();

  @Override
  public boolean allowed(ObjectRegistry objectRegistry, T type) {
    V eval = objectRegistry.get(getName(), valueType());
    if (eval != null) {
      return doAllowed(eval, type);
    }
    return false;
  }
}
