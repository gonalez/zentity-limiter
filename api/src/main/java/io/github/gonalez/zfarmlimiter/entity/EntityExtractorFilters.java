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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityIsNamedExtractorFilter;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityIsTamedExtractorFilter;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityTypeExtractorFilter;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Helper class for {@link EntityExtractorFilter}s. */
public final class EntityExtractorFilters {
  private static final ImmutableList<EntityExtractorFilter<?>> DEFAULT_FILTERS =
      ImmutableList.of(
          new EntityTypeExtractorFilter(),
          new EntityIsTamedExtractorFilter(),
          new EntityIsNamedExtractorFilter());

  private static final ImmutableMap<String, EntityExtractorFilter<?>> BY_NAME =
      ImmutableMap.copyOf(DEFAULT_FILTERS.stream().collect(
          Collectors.toMap(EntityExtractorFilter::getName, Function.identity())));

  @Nullable
  public static EntityExtractorFilter<?> findFilterForName(String filterName) {
    return BY_NAME.get(filterName.toLowerCase());
  }

  private EntityExtractorFilters() {}
}
