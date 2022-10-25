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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zfarmlimiter.entity.BasicEntityRuleHelper;
import io.github.gonalez.zfarmlimiter.entity.CachingRuleDescription;
import io.github.gonalez.zfarmlimiter.entity.EntityChecker;
import io.github.gonalez.zfarmlimiter.entity.EntityHandlingEntityChecker;
import io.github.gonalez.zfarmlimiter.entity.RecursivelyEntityExtractor;
import io.github.gonalez.zfarmlimiter.listener.ZFarmLimiterListener;
import io.github.gonalez.zfarmlimiter.rule.FileWritingRuleSerializer;
import io.github.gonalez.zfarmlimiter.rule.Rule;
import io.github.gonalez.zfarmlimiter.rule.RuleCollection;
import io.github.gonalez.zfarmlimiter.rule.RuleSerializerListeningRuleCollection;
import io.github.gonalez.zfarmlimiter.rule.YamlConfigurationRuleSerializer;
import io.github.gonalez.zfarmlimiter.util.converter.MoreObjectConverters;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;

/** The main class of the ZFarm Limiter plugin. */
public class ZFarmLimiterPlugin extends JavaPlugin {
  @Nullable
  private ZFarmLimiterPluginVariables pluginVariables;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    FileConfiguration fileConfiguration = getConfig();

    ImmutableSet.Builder<EntityType> excludedEntityTypesBuilder = ImmutableSet.builder();
    for (String excludedEntityName : fileConfiguration.getStringList("excludedEntityTypes")) {
      try {
        EntityType entityType = EntityType.valueOf(excludedEntityName);
        excludedEntityTypesBuilder.add(entityType);
      } catch (IllegalArgumentException exception) {
        getLogger().log(Level.WARNING,
            "excludedEntityTypes no entity type found with name %s, skipping", excludedEntityName);
      }
    }

    try {
      ZFarmLimiterPluginVariables.Builder variablesBuilder = ZFarmLimiterPluginVariables.newBuilder();

      FileWritingRuleSerializer ruleSerializer =
          new YamlConfigurationRuleSerializer(
              MoreObjectConverters.DEFAULT_REGISTRY,
              false,
              getDataFolder().toPath().resolve("rules"),
              ImmutableMap.of(
                  "myRule",
                  Rule.newBuilder()
                      .build()), // we already cover the defaults in Rule#newBuilder
              true);

      RuleCollection ruleCollection = new RuleSerializerListeningRuleCollection(ruleSerializer);
      variablesBuilder.setRuleCollection(ruleCollection);
      ruleSerializer.init();

      EntityChecker entityChecker = new EntityHandlingEntityChecker(
          CachingRuleDescription.INSTANCE,
          RecursivelyEntityExtractor.INSTANCE,
          ImmutableList.of(Entity::remove));
      variablesBuilder.setEntityChecker(entityChecker);
      entityChecker.init(this);

      this.pluginVariables = variablesBuilder.build();

      PluginManager pluginManager = getServer().getPluginManager();
      pluginManager.registerEvents(
          new ZFarmLimiterListener(
              excludedEntityTypesBuilder.build(),
              BasicEntityRuleHelper.INSTANCE,
              ruleCollection,
              entityChecker),
          this);
    } catch (IOException e) {
      throw new RuntimeException("Cannot initialize plugin", e);
    }
  }

  @Nullable
  public ZFarmLimiterPluginVariables getPluginVariables() {
    return pluginVariables;
  }
}
