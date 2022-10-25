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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapEntityExtractorFilterFactory implements EntityExtractorFilterFactory {
  private final ImmutableSet<Class<? extends EntityExtractor.Filter<?>>> filters;

  private final ImmutableMultimap<Class<?>, Constructor<?>> parameterTypesConstructor;

  private final Set<Class<?>> analyzedClasses = new HashSet<>();
  private final HashMap<String, Constructor<?>> filterNamesConstructor = new HashMap<>();

  public MapEntityExtractorFilterFactory(
      ImmutableSet<Class<? extends EntityExtractor.Filter<?>>> filters) {
    this.filters = filters;
    ImmutableMultimap.Builder<Class<?>, Constructor<?>> builder = ImmutableMultimap.builder();
    for (Class<? extends EntityExtractor.Filter<?>> filterClass : filters) {
      for (Constructor<?> constructor : filterClass.getDeclaredConstructors()) {
        if (constructor.getParameterTypes().length != 1) {
          continue;
        }
        builder.put(constructor.getParameterTypes()[0], constructor);
      }
    }
    this.parameterTypesConstructor = builder.build();
  }

  @Nullable
  @Override
  public EntityExtractor.Filter<?> createFilter(String name, Object value) {
    Class<?> valueClass = value.getClass();
    ImmutableCollection<Constructor<?>> constructors =
        parameterTypesConstructor.get(valueClass);
    if (constructors.isEmpty()) {
      return null;
    }
    maybeSetupFilterNamesConstructor(valueClass, value);
    Constructor<?> maybeFindConstructor = filterNamesConstructor.get(name);
    if (maybeFindConstructor == null) {
      return null;
    }
    try {
      return (EntityExtractor.Filter<?>) maybeFindConstructor.newInstance(value);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private void maybeSetupFilterNamesConstructor(Class<?> clazz, Object value) {
    if (analyzedClasses.contains(clazz)) {
      return;
    }
    analyzedClasses.add(clazz);
    for (Constructor<?> constructor : parameterTypesConstructor.get(clazz)) {
      try {
        EntityExtractor.Filter<?> createFilter =
            (EntityExtractor.Filter<?>) constructor.newInstance(value);
        filterNamesConstructor.put(createFilter.getName(), constructor);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
