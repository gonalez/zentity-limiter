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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zentitylimiter.entity.event.EntityCheckEvent;
import io.github.gonalez.zentitylimiter.rule.Rule;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Basic implementation for {@link EntityChecker}. This class also calls some events, for extra functionality,
 * i.e {@link EntityCheckEvent}.
 */
public abstract class AbstractEntityChecker implements EntityChecker {
  private final RuleDescription.Provider ruleDescriptionProvider;
  private final EntityExtractor entityExtractor;

  protected PluginManager pluginManager;

  public AbstractEntityChecker(
      RuleDescription.Provider ruleDescriptionProvider,
      EntityExtractor entityExtractor) {
    this.ruleDescriptionProvider = checkNotNull(ruleDescriptionProvider);
    this.entityExtractor = checkNotNull(entityExtractor);
  }

  @Override
  public void init(Plugin plugin) {
    checkNotNull(plugin, "plugin cannot be null");
    this.pluginManager = plugin.getServer().getPluginManager();
  }

  /**
   * Analyzes the entities that exceeded the limit set by the {@code checked} entity rule.
   *
   * @see Rule#maxAmount()
   */
  protected abstract ResultType analyzeExceededEntities(
      Rule rule, Entity checked, ImmutableList<Entity> entities);

  /** Calls the given {@code event}, using the plugin manager. */
  private <T extends Event> T callEvent(T event) {
    pluginManager.callEvent(event);
    return event;
  }

  @Override
  public ResultType check(Entity entity, Rule rule) {
    RuleDescription ruleDescription = ruleDescriptionProvider.provide(rule);
    if (ruleDescription == null) {
      return ResultType.RULE_NOT_FOUND;
    }

    if (pluginManager != null) {
      EntityCheckEvent entityCheckEvent = this.callEvent(new EntityCheckEvent(entity, rule, ruleDescription));
      if (entityCheckEvent.isCancelled()) {
        return ResultType.EVENT_CANCELLED;
      }
      rule = entityCheckEvent.getRule();
      ruleDescription = entityCheckEvent.getRuleDescription();
    }

    ImmutableSet<Entity> extractEntities =
        entityExtractor.extractEntitiesInLocation(entity.getLocation(), rule.radius(), ruleDescription);
    if (extractEntities.size() > rule.maxAmount()) {
      ImmutableList<Entity> needsAnalyze =
          extractEntities.asList().subList(rule.maxAmount(), extractEntities.size());
      return analyzeExceededEntities(rule, entity, needsAnalyze);
    }

    return ResultType.TOO_FEW_ENTITIES;
  }
}
