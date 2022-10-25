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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zfarmlimiter.rule.Rule;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** Default, basic implementation for {@link RuleDescription}. */
public class DefaultRuleDescription implements RuleDescription {
  private final HashSet<RuleDescription> ruleDescriptions = new HashSet<>();

  private final HashMap<EntityExtractor.Filter<?>, Boolean> filters = new HashMap<>();

  private final EntityExtractorFilterFactory filterFactory;
  private final Rule rule;

  public DefaultRuleDescription(
      EntityExtractorFilterFactory filterFactory, Rule rule) {
    this.filterFactory = checkNotNull(filterFactory);
    this.rule = checkNotNull(rule);
    for (String allowedEntities : rule.allowedEntities()) {
      filters.put(EntityExtractorFilters.isEntityType(EntityType.valueOf(allowedEntities)), false);
    }
    for (Map.Entry<String, Object> optionEntry : rule.options().entrySet()) {
      EntityExtractor.Filter<?> maybeCreateFilter =
          filterFactory.createFilter(optionEntry.getKey(), optionEntry.getValue());
      if (maybeCreateFilter != null) {
        filters.put(maybeCreateFilter, true);
      }
    }
  }

  @Override
  public Rule getRule() {
    return rule;
  }

  @Override
  public ImmutableMap<EntityExtractor.Filter<?>, Boolean> getFilters() {
    return ImmutableMap.copyOf(filters);
  }

  @Override
  public void mergeWith(RuleDescription ruleDescription) {
    if (this.ruleDescriptions.add(ruleDescription)) {
      filters.putAll(ruleDescription.getFilters());
    }
  }

  @Override
  public boolean supportsClone() {
    return true;
  }

  @Override
  public RuleDescription createClone() {
    RuleDescription ruleDescription = new DefaultRuleDescription(filterFactory, rule);
    for (RuleDescription merge : ruleDescriptions) {
      ruleDescription.mergeWith(merge);
    }
    return ruleDescription;
  }
}
