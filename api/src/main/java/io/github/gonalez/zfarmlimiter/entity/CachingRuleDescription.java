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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.gonalez.zfarmlimiter.rule.Rule;

import javax.annotation.Nullable;

/** Caching version of {@link RuleDescription.Provider}. */
public class CachingRuleDescription implements RuleDescription.Provider {
  public static final RuleDescription.Provider INSTANCE = new CachingRuleDescription();

  private static final LoadingCache<Rule, RuleDescription> RULE_DESCRIPTION_CACHE =
      CacheBuilder.newBuilder()
          .weakKeys()
          .build(new CacheLoader<Rule, RuleDescription>() {
            @Override
            public RuleDescription load(Rule key) throws Exception {
              return provideRuleDescription(key);
            }
          });

  private static RuleDescription provideRuleDescription(Rule rule) {
    return new DefaultRuleDescription(rule);
  }

  @Nullable
  @Override
  public RuleDescription provide(Rule rule) {
    return RULE_DESCRIPTION_CACHE.getUnchecked(rule);
  }
}
