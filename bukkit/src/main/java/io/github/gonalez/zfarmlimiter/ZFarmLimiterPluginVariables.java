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
package io.github.gonalez.zfarmlimiter;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.gonalez.zfarmlimiter.entity.EntityChecker;
import io.github.gonalez.zfarmlimiter.rule.RuleCollection;

/** Shared values for the {@link ZFarmLimiterPlugin} plugin. */
public interface ZFarmLimiterPluginVariables {
  static Builder newBuilder() {
    return new Builder.DefaultZFarmLimiterPluginVariablesBuilder();
  }

  EntityChecker getEntityChecker();

  RuleCollection getRuleCollection();

  interface Builder {
    Builder setEntityChecker(EntityChecker entityChecker);
    Builder setRuleCollection(RuleCollection ruleCollection);

    ZFarmLimiterPluginVariables build();

    final class DefaultZFarmLimiterPluginVariablesBuilder implements Builder {
      private EntityChecker entityChecker;
      private RuleCollection ruleCollection;

      @Override
      public Builder setEntityChecker(EntityChecker entityChecker) {
        this.entityChecker = entityChecker;
        return this;
      }

      @Override
      public Builder setRuleCollection(RuleCollection ruleCollection) {
        this.ruleCollection = ruleCollection;
        return this;
      }

      @Override
      public ZFarmLimiterPluginVariables build() {
        checkNotNull(entityChecker);
        checkNotNull(ruleCollection);
        return new ZFarmLimiterPluginVariables() {
          @Override
          public EntityChecker getEntityChecker() {
            return entityChecker;
          }

          @Override
          public RuleCollection getRuleCollection() {
            return ruleCollection;
          }
        };
      }
    }
  }
}
