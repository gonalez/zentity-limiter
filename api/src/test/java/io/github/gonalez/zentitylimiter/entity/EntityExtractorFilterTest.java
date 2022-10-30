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

import static io.github.gonalez.zentitylimiter.entity.EntityExtractorTest.mockEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zentitylimiter.entity.filter.EntityIsNamedExtractorFilter;
import io.github.gonalez.zentitylimiter.entity.filter.EntityIsTamedExtractorFilter;
import io.github.gonalez.zentitylimiter.entity.filter.EntityTypeExtractorFilter;
import io.github.gonalez.zentitylimiter.registry.ObjectRegistry;
import io.github.gonalez.zentitylimiter.rule.Rule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EntityExtractorFilterTest {
  @Mock private static World world;

  private static EntityExtractor entityExtractor;

  private static Entity entity;

  @BeforeAll
  public static void setUp() {
    entity = mockEntity(EntityType.COW);
    entityExtractor = new RecursivelyEntityExtractor(
        new ClassEntityExtractorFilterExtractor(entity -> entity.getType().getEntityClass()));
  }

  /** Tests {@link EntityIsNamedExtractorFilter}. */
  @Test
  public void testEntityIsNamed() {
    EntityIsNamedExtractorFilter entityIsNamedExtractorFilter = new EntityIsNamedExtractorFilter();
    when(entity.getCustomName()).thenReturn(entityIsNamedExtractorFilter.getName());

    ImmutableSet<Entity> named = extractEntitiesWithFilter(entity, entityIsNamedExtractorFilter, true);
    assertEquals(1, named.size());
    ImmutableSet<Entity> notNamed = extractEntitiesWithFilter(entity, entityIsNamedExtractorFilter, false);
    assertEquals(0, notNamed.size());
  }

  /** Tests {@link EntityTypeExtractorFilter}. */
  @Test
  public void testEntityTypeEquals() {
    EntityTypeExtractorFilter entityTypeExtractorFilter = new EntityTypeExtractorFilter();

    ImmutableSet<Entity> hasEntity = extractEntitiesWithFilter(entity, entityTypeExtractorFilter, EntityType.COW);
    assertEquals(1, hasEntity.size());
    ImmutableSet<Entity> hadEntity = extractEntitiesWithFilter(entity, entityTypeExtractorFilter, EntityType.ZOMBIE);
    assertEquals(0, hadEntity.size());
  }

  /** Tests {@link EntityTypeExtractorFilter}. */
  @Test
  public void testEntityIsTamed() {
    Wolf tameable = mock(Wolf.class);
    when(tameable.getType()).thenReturn(EntityType.WOLF);

    when(tameable.isTamed()).thenReturn(true);

    EntityIsTamedExtractorFilter entityIsTamedExtractorFilter = new EntityIsTamedExtractorFilter();

    ImmutableSet<Entity> tamedTrue = extractEntitiesWithFilter(tameable, entityIsTamedExtractorFilter, true);
    assertEquals(1, tamedTrue.size());
    ImmutableSet<Entity> tamedFalse = extractEntitiesWithFilter(tameable, entityIsTamedExtractorFilter, false);
    assertEquals(0, tamedFalse.size());
  }

  private <V, T> ImmutableSet<Entity> extractEntitiesWithFilter(
      Entity entity, ExtraEntityExtractorFilter<V, T> entityExtractorFilter, V v) {
    return extractEntitiesWithFilter(entity, entityExtractorFilter,
        ObjectRegistry.of(entityExtractorFilter.getName(), entityExtractorFilter.valueType(), v));
  }

  private ImmutableSet<Entity> extractEntitiesWithFilter(
      Entity entity, EntityExtractorFilter<?> entityTypeExtractorFilter) {
    return extractEntitiesWithFilter(entity, entityTypeExtractorFilter,
        ObjectRegistry.newBuilder().build());
  }

  private ImmutableSet<Entity> extractEntitiesWithFilter(
      Entity entity, EntityExtractorFilter<?> entityTypeExtractorFilter, ObjectRegistry objectRegistry) {
      when(world.getNearbyEntities(any(), anyDouble(), anyDouble(), anyDouble()))
        .thenReturn(ImmutableSet.of(entity));
      when(entity.getLocation()).thenReturn(new Location(world, 5, 5, 5));

      RuleDescription ruleDescription = mock(RuleDescription.class);

      when(ruleDescription.getFilters()).thenReturn(
          ImmutableMap.of(entityTypeExtractorFilter, objectRegistry));
      lenient().when(ruleDescription.getRule()).thenReturn(Rule.newBuilder().build());

      return entityExtractor.extractEntitiesInLocation(entity.getLocation(), 5, ruleDescription);
  }
}
