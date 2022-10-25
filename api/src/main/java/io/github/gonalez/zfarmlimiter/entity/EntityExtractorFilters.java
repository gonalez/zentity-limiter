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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityTypeExtractorFilter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/** Helper class for {@link EntityExtractor.Filter}s. */
public final class EntityExtractorFilters {
  /** @return {@code true} if all of the given filters are allowed for the given entity. */
  public static boolean allowed(
      ImmutableList<EntityExtractor.Filter> filters, Entity entity) {
    for (EntityExtractor.Filter filter : filters) {
      if (!filter.allowed(entity))
        return false;
    }
    return true;
  }

  /** A new filter that compares if the entity type is compatible with another. */
  public static EntityExtractor.Filter isEntityType(EntityType entityType) {
    return new EntityTypeExtractorFilter(entityType);
  }

  /** A new filter that compares if any of the given filters is allowed for an entity. */
  public static EntityExtractor.Filter anyOf(ImmutableList<EntityExtractor.Filter> filters) {
    return new AnyOfEntityExtractorFilter(filters);
  }

  private static final class AnyOfEntityExtractorFilter implements EntityExtractor.Filter {
    private final ImmutableList<EntityExtractor.Filter> filters;

    public AnyOfEntityExtractorFilter(ImmutableList<EntityExtractor.Filter> filters) {
      this.filters = checkNotNull(filters);
    }

    @Override
    public boolean allowed(Entity entity) {
      for (EntityExtractor.Filter filter : filters) {
        if (filter.allowed(entity)) {
          return true;
        }
      }
      return false;
    }
  }

  private EntityExtractorFilters() {}
}
