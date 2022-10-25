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

import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityIsNamedExtractorFilter;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityIsTamedExtractorFilter;
import io.github.gonalez.zfarmlimiter.entity.filter.EntityTypeExtractorFilter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;

/** Helper class for {@link EntityExtractor.Filter}s. */
public final class EntityExtractorFilters {
  public static final ImmutableSet<Class<? extends EntityExtractor.Filter<?>>> FILTERS =
      ImmutableSet.of(
          EntityTypeExtractorFilter.class,
          EntityIsTamedExtractorFilter.class,
          EntityIsNamedExtractorFilter.class);

  public static boolean allowed(RuleDescription ruleDescription, Entity entity) {
    for (Map.Entry<EntityExtractor.Filter<?>, Boolean> entry :
        ruleDescription.getFilters().entrySet()) {
      EntityExtractor.Filter filter = entry.getKey();
      if (filter.filterType().isAssignableFrom(entity.getClass())) {
        if (!filter.allowed(entity) && entry.getValue()) {
          return false;
        }
      }
    }
    return true;
  }

  /** A new filter that compares if the entity type is compatible with another. */
  public static EntityExtractor.Filter<Entity> isEntityType(EntityType entityType) {
    return new EntityTypeExtractorFilter(entityType);
  }

  private EntityExtractorFilters() {}
}
