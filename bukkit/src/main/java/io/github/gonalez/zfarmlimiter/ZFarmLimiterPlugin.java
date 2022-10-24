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

import io.github.gonalez.zfarmlimiter.rule.FileWritingRuleSerializer;
import io.github.gonalez.zfarmlimiter.rule.RuleCollection;
import io.github.gonalez.zfarmlimiter.rule.RuleSerializerListeningRuleCollection;
import io.github.gonalez.zfarmlimiter.rule.YamlConfigurationRuleSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/** The main class of the ZFarm Limiter plugin. */
public class ZFarmLimiterPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();

    try {
      FileWritingRuleSerializer ruleSerializer =
          new YamlConfigurationRuleSerializer(
              false,
              getDataFolder().toPath().resolve("rules"),
              true);

      RuleCollection ruleCollection =
          new RuleSerializerListeningRuleCollection(ruleSerializer);

      ruleSerializer.init();
    } catch (IOException e) {
      throw new RuntimeException("Cannot initialize plugin", e);
    }
  }
}
