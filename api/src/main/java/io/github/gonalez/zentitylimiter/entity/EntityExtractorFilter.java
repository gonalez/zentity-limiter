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

/** Restricts which entities can be extracted. */
public abstract class EntityExtractorFilter<T> {
  protected EntityExtractorFilter() {}

  /** @return the type of thing that this filter applies on. */
  public abstract Class<T> filterType();

  /** @return the name of this filter. */
  public abstract String getName();

  /** @return {@code true} if the entity can be extracted. */
  public abstract boolean allowed(ObjectRegistry objectRegistry, T type);
}