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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link EntityExtractor}. */
@ExtendWith({MockitoExtension.class})
public class EntityExtractorTest {

  @Mock private World world;

  @Test
  public void testRecursivelyEntityExtractorWithFilters() {
    int numEntities = 5;
    Location location = new Location(world, 0, 0, 0);

    ImmutableSet.Builder<Entity> entityBuilder = ImmutableSet.builder();
    for (int i = 0; i < numEntities; i++) {
      entityBuilder.add(mockEntity(EntityType.ZOMBIE));
      entityBuilder.add(mockEntity(EntityType.BAT));
    }

    when(world.getNearbyEntities(any(), anyDouble(), anyDouble(), anyDouble()))
        .thenReturn(entityBuilder.build());

    EntityExtractor entityExtractor = new RecursivelyEntityExtractor(
        ImmutableSet.of(
            EntityExtractorFilters.isEntityType(EntityType.ZOMBIE)));

    assertEquals(numEntities, entityExtractor.extractEntitiesInLocation(location, 5).size());
  }

  private static Entity mockEntity(EntityType entityType) {
    Entity entity = mock(Entity.class);
    when(entity.getType()).thenReturn(entityType);
    return entity;
  }
}
