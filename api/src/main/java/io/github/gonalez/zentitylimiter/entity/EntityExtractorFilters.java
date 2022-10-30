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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zentitylimiter.entity.filter.EntityIsNamedExtractorFilter;
import io.github.gonalez.zentitylimiter.entity.filter.EntityIsTamedExtractorFilter;
import io.github.gonalez.zentitylimiter.entity.filter.EntityTypeExtractorFilter;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/** Helper class for {@link EntityExtractorFilter}s. */
public final class EntityExtractorFilters {

  /** Cache of filters by its class. */
  private static final LoadingCache<
          Class<? extends EntityExtractorFilter<?>>, EntityExtractorFilter<?>>
      FILTER_LOADING_CACHE =
          CacheBuilder.newBuilder()
              .build(new CacheLoader<Class<? extends EntityExtractorFilter<?>>, EntityExtractorFilter<?>>() {
                @Override
                public EntityExtractorFilter<?> load(
                    Class<? extends EntityExtractorFilter<?>> key) {
                  return loadEntityExtractorFilter(key);
                }
              });

  private static EntityExtractorFilter<?> loadEntityExtractorFilter(
      Class<? extends EntityExtractorFilter<?>> entityExtractorFilterClass) {
    try {
      return entityExtractorFilterClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException |
             NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked") // safe
  public static <T extends EntityExtractorFilter<?>> T getInstance(
      Class<T> entityExtractorFilterClass) {
    return (T) FILTER_LOADING_CACHE.getUnchecked(entityExtractorFilterClass);
  }

  @Nullable
  public static EntityExtractorFilter<?> getInstanceForName(String name) {
    final ImmutableMap<Class<? extends EntityExtractorFilter<?>>, EntityExtractorFilter<?>> all;
    try {
      all = FILTER_LOADING_CACHE.getAll(BUILT_IN);
    } catch (ExecutionException e) {
      throw new IllegalStateException(e);
    }
    for (EntityExtractorFilter<?> filter : all.values()) {
      if (filter.getName().equals(name)) {
        return filter;
      }
    }
    return null;
  }

  // Built in filters
  private static final ImmutableList<Class<? extends EntityExtractorFilter<?>>> BUILT_IN =
      ImmutableList.of(
          EntityTypeExtractorFilter.class,
          EntityIsTamedExtractorFilter.class,
          EntityIsNamedExtractorFilter.class);

  private EntityExtractorFilters() {}
}
