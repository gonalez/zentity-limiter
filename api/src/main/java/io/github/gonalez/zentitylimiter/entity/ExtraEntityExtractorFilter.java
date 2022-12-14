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

import io.github.gonalez.zentitylimiter.registry.ObjectRegistry;

/**
 * {@link EntityExtractorFilter} that has an extra thing to check for allowed (@code V), useful if the
 * subclass only needs a single value from the {@code objectRegistry} to do the necessary 'comparisons'.
 */
public abstract class ExtraEntityExtractorFilter<V, T> extends EntityExtractorFilter<T> {
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
