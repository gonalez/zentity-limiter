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
package io.github.gonalez.zfarmlimiter.listener;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zfarmlimiter.entity.EntityChecker;
import io.github.gonalez.zfarmlimiter.entity.EntityCheckerException;
import io.github.gonalez.zfarmlimiter.entity.EntityRuleHelper;
import io.github.gonalez.zfarmlimiter.rule.Rule;
import io.github.gonalez.zfarmlimiter.rule.RuleCollection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/** Subscribes to all necessary events for basic functionality of the plugin. */
public class ZFarmLimiterListener implements Listener {
  private final EntityRuleHelper entityRuleHelper;
  private final RuleCollection ruleCollection;

  private final LoadingCache<Entity, ImmutableSet<Rule>> ENTITY_COMPATIBLE_RULES =
      CacheBuilder.newBuilder()
          .weakKeys()
          .build(new CacheLoader<Entity, ImmutableSet<Rule>>() {
            @Override
            public ImmutableSet<Rule> load(Entity entity) throws Exception {
              ImmutableSet.Builder<Rule> ruleBuilder = ImmutableSet.builder();
              for (Rule rule : ruleCollection.getRules()) {
                if (entityRuleHelper.isCompatible(rule, entity)) {
                  ruleBuilder.add(rule);
                }
              }
              return ruleBuilder.build();
            }
          });

  private final ImmutableSet<EntityType> excludedEntityTypes;
  private final EntityChecker entityChecker;

  public ZFarmLimiterListener(
      ImmutableSet<EntityType> excludedEntityTypes,
      EntityRuleHelper entityRuleHelper,
      RuleCollection ruleCollection,
      EntityChecker entityChecker) {
    this.excludedEntityTypes = checkNotNull(excludedEntityTypes);
    this.entityRuleHelper = checkNotNull(entityRuleHelper);
    this.ruleCollection = checkNotNull(ruleCollection);
    this.entityChecker = checkNotNull(entityChecker);
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent creatureSpawnEvent) {
    final Entity entity = creatureSpawnEvent.getEntity();
    boolean isExcluded = excludedEntityTypes.contains(entity.getType());
    if (isExcluded) {
      return;
    }
    ImmutableSet<Rule> compatibleEntityRules = ENTITY_COMPATIBLE_RULES.getUnchecked(entity);
    for (Rule rule : compatibleEntityRules) {
      try {
        EntityChecker.ResultType resultType = entityChecker.check(entity, rule);
      } catch (EntityCheckerException ignored) {
      }
    }
  }
}
