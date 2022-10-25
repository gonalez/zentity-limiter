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
package io.github.gonalez.zfarmlimiter.rule;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class Rule {
  public static Builder newBuilder() {
    return new AutoValue_Rule.Builder()
        .setAllowedEntities(ImmutableList.of("VILLAGER"))
        .setAllowedWorlds(ImmutableList.of())
        .setRadius(3)
        .setMaxAmount(5)
        .setOptions(ImmutableMap.of("tamed", true));
  }

  public abstract ImmutableList<String> allowedEntities();
  public abstract ImmutableList<String> allowedWorlds();

  public abstract double radius();
  public abstract int maxAmount();

  public abstract ImmutableMap<String, Object> options();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setAllowedEntities(ImmutableList<String> allowedEntities);
    public abstract Builder setAllowedWorlds(ImmutableList<String> allowedWorlds);

    public abstract Builder setRadius(double radius);
    public abstract Builder setMaxAmount(int maxAmount);

    public abstract Builder setOptions(ImmutableMap<String, Object> options);

    public abstract Rule build();
  }
}
