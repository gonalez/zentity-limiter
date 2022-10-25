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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zfarmlimiter.rule.Rule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;

/** Default, basic implementation for {@link RuleDescription}. */
public class DefaultRuleDescription implements RuleDescription {
  private final EntityExtractorFilterFactory filterFactory;
  private final Rule rule;

  private final ImmutableMap<EntityExtractor.Filter<?>, Boolean> filters;

  public DefaultRuleDescription(
      EntityExtractorFilterFactory filterFactory, Rule rule) {
    this.filterFactory = checkNotNull(filterFactory);
    this.rule = checkNotNull(rule);
    filters = loadFilters();
  }

  private ImmutableMap<EntityExtractor.Filter<?>, Boolean> loadFilters() {
    ImmutableMap.Builder<EntityExtractor.Filter<?>, Boolean> filters = ImmutableMap.builder();
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
    return filters.build();
  }

  @Override
  public ImmutableMap<EntityExtractor.Filter<?>, Boolean> getFilters() {
    return filters;
  }
}
