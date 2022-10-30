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
package io.github.gonalez.zentitylimiter;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.gonalez.zentitylimiter.entity.EntityChecker;
import io.github.gonalez.zentitylimiter.rule.RuleCollection;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/** Subscribes to all necessary events for {@link io.github.gonalez.zentitylimiter.EntityCheckingType#EVENT} functionality. */
class EntityLimiterListener implements Listener {
  private final RuleCollection.RuleCollectionFinder ruleCollectionFinder;
  private final EntityChecker entityChecker;

  public EntityLimiterListener(
      RuleCollection.RuleCollectionFinder ruleCollectionFinder,
      EntityChecker entityChecker) {
    this.ruleCollectionFinder = checkNotNull(ruleCollectionFinder);
    this.entityChecker = checkNotNull(entityChecker);
  }

  private EntityChecker.ResultType checkEntity(Entity entity) {
    return entityChecker.check(entity, ruleCollectionFinder.findRule(entity));
  }

  @EventHandler
  public void onItemSpawn(CreatureSpawnEvent creatureSpawnEvent) {
    if (creatureSpawnEvent.isCancelled())
      return;
    checkEntity(creatureSpawnEvent.getEntity());
  }
}
