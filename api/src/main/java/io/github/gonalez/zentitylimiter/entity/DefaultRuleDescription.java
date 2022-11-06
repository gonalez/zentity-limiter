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
import static io.github.gonalez.zentitylimiter.util.converter.MoreObjectConverters.getConvertedType;

import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zentitylimiter.entity.filter.EntityTypeExtractorFilter;
import io.github.gonalez.zentitylimiter.registry.ObjectRegistry;
import io.github.gonalez.zentitylimiter.rule.Rule;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** Default, basic implementation for {@link RuleDescription}. */
public class DefaultRuleDescription implements RuleDescription {
  private final HashSet<RuleDescription> ruleDescriptions = new HashSet<>();

  private final HashMap<EntityExtractorFilter<?>, ObjectRegistry> filters = new HashMap<>();

  private final Rule rule;

  public DefaultRuleDescription(Rule rule) {
    this.rule = checkNotNull(rule);
    for (String allowedEntity : rule.allowedEntities()) {
      registerFilter(EntityExtractorFilters.getInstance(EntityTypeExtractorFilter.class),
          EntityType.valueOf(allowedEntity));
    }
    for (Map.Entry<String, Object> optionEntry : rule.options().entrySet()) {
      EntityExtractorFilter<?> maybeCreateFilter =
          EntityExtractorFilters.getInstanceForName(optionEntry.getKey());
      if (maybeCreateFilter != null) {
        registerFilter(maybeCreateFilter, optionEntry.getValue());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void registerFilter(
      EntityExtractorFilter<?> filter, Object value) {
    ObjectRegistry objectRegistry =
        ObjectRegistry.of(filter.getName(), (Class<? super Object>) getConvertedType(value.getClass()), value);
    filters.put(filter, objectRegistry);
  }

  @Override
  public Rule getRule() {
    return rule;
  }

  @Override
  public ImmutableMap<EntityExtractorFilter<?>, ObjectRegistry> getFilters() {
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
    RuleDescription ruleDescription = new DefaultRuleDescription(rule);
    for (RuleDescription merge : ruleDescriptions) {
      ruleDescription.mergeWith(merge);
    }
    return ruleDescription;
  }
}
