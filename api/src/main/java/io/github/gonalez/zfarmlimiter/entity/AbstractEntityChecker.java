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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zfarmlimiter.rule.Rule;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public abstract class AbstractEntityChecker implements EntityChecker {
  /** Resolves a rule for an entity. */
  @FunctionalInterface
  public interface EntityRuleResolver {
    /** Retrieves the rule for the given entity or {@code null} if not found. */
    @Nullable
    Rule findRule(Entity entity);
  }

  private final EntityRuleResolver entityRuleResolver;
  private final EntityExtractor entityExtractor;

  public AbstractEntityChecker(
      EntityRuleResolver entityRuleResolver,
      EntityExtractor entityExtractor) {
    this.entityRuleResolver = entityRuleResolver;
    this.entityExtractor = entityExtractor;
  }

  /**
   * Analyzes the entities that exceeded the limit set by the {@code checked} entity rule.
   *
   * @see Rule#maxAmount()
   */
  protected abstract void analyzeExceededEntities(
      Rule rule, Entity checked, ImmutableList<Entity> entities) throws EntityCheckerException;

  @Override
  public void check(Entity entity) throws EntityCheckerException {
    Rule rule = entityRuleResolver.findRule(entity);
    if (rule == null) {
      throw EntityCheckerException.newBuilder()
          .withExceptionCode(EntityCheckerExceptionCode.NO_RULE_FOUND)
          .build();
    }
    Location entityLocation = entity.getLocation();
    ImmutableSet<Entity> extractEntities =
        entityExtractor.extractEntitiesInLocation(entityLocation, rule.radius());
    if (extractEntities.size() > rule.maxAmount()) {
      ImmutableList<Entity> needsAnalyze =
          extractEntities.asList().subList(rule.maxAmount(), extractEntities.size());
      analyzeExceededEntities(rule, entity, needsAnalyze);
    }
  }
}
