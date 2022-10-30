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

import io.github.gonalez.zentitylimiter.rule.Rule;
import io.github.gonalez.zentitylimiter.rule.RuleCollection;
import org.bukkit.entity.EntityType;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zentitylimiter.rule.RuleCollection.RuleCollectionFinder;

import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * {@link RuleCollectionFinder} which gets the rule whose {@link EntityType#name() entity type name}
 * contains any of the rule {@link Rule#allowedEntities()}.
 */
public class AllowedEntitiesRuleCollectionFinder implements RuleCollectionFinder {
  private final ImmutableMap<String, Rule> entityTypeRules;

  public AllowedEntitiesRuleCollectionFinder(RuleCollection ruleCollection) {
    checkNotNull(ruleCollection);
    ImmutableMap.Builder<String, Rule> builder = ImmutableMap.builder();
    for (Rule rule : ruleCollection.getRules()) {
      for (String allowedEntity : rule.allowedEntities()) {
        builder.put(allowedEntity, rule);
      }
    }
    this.entityTypeRules = builder.build();
  }

  @Nullable
  @Override
  public Rule findRule(Entity entity) {
    return entityTypeRules.get(entity.getType().name());
  }
}
