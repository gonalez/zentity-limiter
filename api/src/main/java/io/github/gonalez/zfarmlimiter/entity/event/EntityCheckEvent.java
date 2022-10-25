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
package io.github.gonalez.zfarmlimiter.entity.event;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.gonalez.zfarmlimiter.entity.RuleDescription;
import io.github.gonalez.zfarmlimiter.rule.Rule;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityCheckEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();

  private final Entity entity;

  private Rule rule;
  private RuleDescription ruleDescription;

  private boolean cancelled = false;

  public EntityCheckEvent(
      Entity entity,
      Rule rule,
      RuleDescription ruleDescription) {
    this.entity = checkNotNull(entity);
    this.rule = checkNotNull(rule);
    this.ruleDescription = checkNotNull(ruleDescription);
  }

  public Entity getEntity() {
    return entity;
  }

  public Rule getRule() {
    return rule;
  }

  public void setRule(Rule rule) {
    this.rule = rule;
  }

  public RuleDescription getRuleDescription() {
    return ruleDescription;
  }

  public void setRuleDescription(RuleDescription ruleDescription) {
    this.ruleDescription = ruleDescription;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }
}
