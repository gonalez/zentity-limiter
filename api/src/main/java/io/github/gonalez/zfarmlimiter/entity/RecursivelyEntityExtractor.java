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

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Basic implementation of {@link EntityExtractor}, which extracts the entities recursively, if there are
 * any entities in the given radius, we will search for new entities from this entity location.
 *
 * <p>Before extracting a new entity, the entity must go through the necessary {@code filters} that are
 * given when constructing this object, otherwise, the entity will not be selected to be extracted.
 */
public class RecursivelyEntityExtractor implements EntityExtractor {
  private final ImmutableSet<Filter> filters;

  public RecursivelyEntityExtractor(ImmutableSet<Filter> filters) {
    this.filters = checkNotNull(filters);
  }

  @Override
  public ImmutableSet<Entity> extractEntitiesInLocation(
      Location baseLocation, double radius) {
    World world = checkNotNull(
        baseLocation.getWorld(),
        "location world must not be bull");
    Set<Entity> entityBuilder = new HashSet<>();
    return extractEntitiesRecursively(world, baseLocation, radius, entityBuilder, filters);
  }

  private static ImmutableSet<Entity> extractEntitiesRecursively(
      World world, Location location,
      double radius, Set<Entity> entities,
      ImmutableSet<Filter> filters) {
    for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
      if (entities.contains(entity)) {
        continue;
      }
      // Checks if the entity passes all the necessary filters
      boolean entityIsAllowed = EntityExtractorFilters.allowed(filters, entity);
      if (!entityIsAllowed) {
        continue;
      }
      entities.add(entity);
      entities.addAll(extractEntitiesRecursively(world, location, radius, entities, filters));
    }
    return ImmutableSet.copyOf(entities);
  }
}
