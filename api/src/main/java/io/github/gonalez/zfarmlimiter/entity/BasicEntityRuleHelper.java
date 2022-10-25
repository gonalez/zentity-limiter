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

import io.github.gonalez.zfarmlimiter.rule.Rule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Basic implementation for {@link EntityRuleHelper}, which checks if the {@link Rule#allowedEntities() entity names},
 * are equal to the given entity {@link EntityType#name() entity type name}.
 */
public class BasicEntityRuleHelper implements EntityRuleHelper {
  // A new instance of this class
  public static final EntityRuleHelper INSTANCE = new BasicEntityRuleHelper();

  @Override
  public boolean isCompatible(Rule rule, Entity entity) {
    if (!rule.allowedWorlds().isEmpty()
        && !rule.allowedWorlds().contains(entity.getWorld().getName())) {
      return false;
    }
    return rule.allowedEntities().contains(entity.getType().name());
  }
}
