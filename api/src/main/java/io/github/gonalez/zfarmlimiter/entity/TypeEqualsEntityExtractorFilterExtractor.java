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
import io.github.gonalez.zfarmlimiter.registry.ObjectRegistry;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Simple {@link EntityExtractorFilterExtractor} which checks if a specific class is assignable for each of the
 * rule description filters, and adds it to the extraction, the class is determined by the {@code classFunction}.
 */
public class TypeEqualsEntityExtractorFilterExtractor implements EntityExtractorFilterExtractor {
  private final HashMap<Class<?>, Set<Class<?>>> allowedTypes = new HashMap<>();
  private final HashMap<Class<?>, Set<Class<?>>> nonAllowedTypes = new HashMap<>();

  private final Function<Entity, Class<?>> classFunction;

  public TypeEqualsEntityExtractorFilterExtractor(
      Function<Entity, Class<?>> classFunction) {
    this.classFunction = checkNotNull(classFunction);
  }

  @Override
  public ImmutableMap<EntityExtractorFilter<?>, ObjectRegistry> extractFilters(
      RuleDescription ruleDescription, Entity entity) {
    Class<?> findClass = classFunction.apply(entity);

    Set<Class<?>> allowedClasses =
        allowedTypes.computeIfAbsent(findClass, c -> new HashSet<>());
    Set<Class<?>> nonAllowedClasses =
        nonAllowedTypes.computeIfAbsent(findClass, c -> new HashSet<>());

    ImmutableMap.Builder<EntityExtractorFilter<?>, ObjectRegistry> builder = ImmutableMap.builder();
    for (Map.Entry<EntityExtractorFilter<?>,
        ObjectRegistry> entry : ruleDescription.getFilters().entrySet()) {
      EntityExtractorFilter<?> filter = entry.getKey();
      Class<?> filterType = filter.filterType();

      if (nonAllowedClasses.contains(filterType)) {
        continue;
      }

      if (allowedClasses.contains(filter.filterType())) {
        builder.put(entry);
      } else {
        if (filterType.isAssignableFrom(findClass)) {
          allowedClasses.add(findClass);
          builder.put(entry);
        } else {
          nonAllowedClasses.add(filterType);
        }
      }
    }
    return builder.build();
  }
}
