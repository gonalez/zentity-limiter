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
package io.github.gonalez.zentitylimiter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zentitylimiter.entity.*;
import io.github.gonalez.zentitylimiter.rule.FileWritingRuleSerializer;
import io.github.gonalez.zentitylimiter.rule.Rule;
import io.github.gonalez.zentitylimiter.rule.RuleCollection;
import io.github.gonalez.zentitylimiter.rule.RuleSerializerListeningRuleCollection;
import io.github.gonalez.zentitylimiter.rule.YamlConfigurationRuleSerializer;
import io.github.gonalez.zentitylimiter.util.converter.MoreObjectConverters;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** The main class of the plugin. */
public class EntityLimiterPlugin extends JavaPlugin {
  /** Actions to execute upon plugin disable. */
  private final List<Runnable> disableActions = new ArrayList<>();

  @Nullable
  private EntityLimiterPluginVariables pluginVariables;

  @Override
  public void onEnable() {
    FileConfiguration fileConfiguration = getConfig();

    fileConfiguration.options().copyDefaults(true);
    saveConfig();

    try {
      EntityLimiterPluginVariables.Builder variablesBuilder = EntityLimiterPluginVariables.newBuilder();

      FileWritingRuleSerializer ruleSerializer =
          new YamlConfigurationRuleSerializer(
              MoreObjectConverters.DEFAULT_REGISTRY,
              false,
              getDataFolder().toPath().resolve("rules"),
              ImmutableMap.of(
                  "myRule",
                  Rule.newBuilder().build()), // we already cover the defaults in Rule#newBuilder
              true);

      RuleCollection ruleCollection = new RuleSerializerListeningRuleCollection(ruleSerializer);
      variablesBuilder.setRuleCollection(ruleCollection);
      ruleSerializer.init();

      RuleDescription.Provider ruleDescriptionProvider =
          new CachingRuleDescriptionProvider(DefaultRuleDescription::new);
      variablesBuilder.setRuleDescriptionProvider(ruleDescriptionProvider);

      EntityChecker entityChecker = new EntityHandlingEntityChecker(
          ruleDescriptionProvider,
          new RecursivelyEntityExtractor(
              new TypeEqualsEntityExtractorFilterExtractor(entity -> entity.getType().getEntityClass())),
          ImmutableList.of(Entity::remove));
      variablesBuilder.setEntityChecker(entityChecker);
      entityChecker.init(this);

      this.pluginVariables = variablesBuilder.build();

      RuleCollection.RuleCollectionFinder ruleCollectionFinder =
          new AllowedEntitiesRuleCollectionFinder(ruleCollection);

      PluginManager pluginManager = getServer().getPluginManager();
      switch (EntityCheckingType.valueOf(
          fileConfiguration.getString("checking.type").toUpperCase(Locale.US))) {
        case EVENT:
          pluginManager.registerEvents(
              new EntityLimiterListener(ruleCollectionFinder, entityChecker), this);
          break;
        case INTERVAL:
          EntityCheckingTask entityCheckingTask =
              new RunnableEntityCheckingTask(
                  ruleCollectionFinder,
                  TimeUnit.SECONDS,
                  fileConfiguration.getInt("checking.interval", 60));
          disableActions.add(entityCheckingTask::shutdown);
          for (Rule rule : ruleCollection.getRules()) {
            entityCheckingTask.addCallback(new EntityCheckingTask.Callback() {
              @Override
              public void onAllEntitiesChecked() {
                Set<World> allowedWorlds = new HashSet<>();
                if (rule.allowedWorlds().isEmpty()) {
                  allowedWorlds.addAll(getServer().getWorlds());
                } else {
                  for (String worldName : rule.allowedWorlds()) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                      allowedWorlds.add(world);
                    }
                  }
                }
                for (World world : allowedWorlds) {
                  getServer().getScheduler().runTask(EntityLimiterPlugin.this,
                      () -> {
                        for (Entity entity : world.getEntities()) {
                          entityCheckingTask.addEntityForChecking(entity);
                        }
                      });
                }
              }
            });
            entityCheckingTask.addEntityChecker(entityChecker);
          }
          entityCheckingTask.start();
          break;
      }
    } catch (Exception e) {
      throw new RuntimeException("Cannot initialize plugin", e);
    }
  }

  @Nullable
  public EntityLimiterPluginVariables getPluginVariables() {
    return pluginVariables;
  }

  @Override
  public void onDisable() {
    for (Runnable disableActions : this.disableActions) {
      disableActions.run();
    }
  }
}
