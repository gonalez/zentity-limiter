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
import io.github.gonalez.zentitylimiter.rule.Rule;
import org.bukkit.entity.Entity;

/** Subclass of AbstractEntityChecker, which executes {@code handlers} for each analyzed entity. */
public class EntityHandlingEntityChecker extends AbstractEntityChecker {
  private final ImmutableList<EntityHandler> handlers;

  public EntityHandlingEntityChecker(
      RuleDescription.Provider ruleDescriptionProvider,
      EntityExtractor entityExtractor,
      ImmutableList<EntityHandler> handlers) {
    super(ruleDescriptionProvider, entityExtractor);
    this.handlers = checkNotNull(handlers);
  }

  @Override
  protected ResultType analyzeExceededEntities(Rule rule, Entity checked, ImmutableList<Entity> entities)  {
    for (Entity entity : entities) {
      for (EntityHandler entityHandler : handlers) {
        entityHandler.handle(entity);
      }
    }
    return ResultType.SUCCEED;
  }
}
