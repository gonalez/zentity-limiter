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

import io.github.gonalez.zentitylimiter.rule.Rule;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** A RuleDescription provider which cache results. */
public class CachingRuleDescriptionProvider implements RuleDescription.Provider {
  private final ConcurrentHashMap<Rule, RuleDescription> ruleDescriptionCache = new ConcurrentHashMap<>();

  private final Function<Rule, RuleDescription> computeRuleDescriptionFunction;

  public CachingRuleDescriptionProvider(
      Function<Rule, RuleDescription> computeRuleDescriptionFunction) {
    this.computeRuleDescriptionFunction = checkNotNull(computeRuleDescriptionFunction);
  }


  @Nullable
  @Override
  public RuleDescription provide(Rule rule) {
    if (rule == null) {
      return null;
    }
    ruleDescriptionCache.computeIfAbsent(rule, computeRuleDescriptionFunction);
    return ruleDescriptionCache.get(rule);
  }
}
