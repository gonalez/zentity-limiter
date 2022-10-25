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

package io.github.gonalez.zfarmlimiter.entity.filter;

import io.github.gonalez.zfarmlimiter.entity.EntityExtractor;
import org.bukkit.entity.Entity;

public class EntityIsNamedExtractorFilter implements EntityExtractor.Filter<Entity> {
  private final boolean named;

  public EntityIsNamedExtractorFilter(boolean named) {
    this.named = named;
  }

  @Override
  public Class<Entity> filterType() {
    return Entity.class;
  }

  @Override
  public String getName() {
    return "named";
  }

  @Override
  public boolean allowed(Entity type) {
    return !named || type.getCustomName() != null;
  }
}
