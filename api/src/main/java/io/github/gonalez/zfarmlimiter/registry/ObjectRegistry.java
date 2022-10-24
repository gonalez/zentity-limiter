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
package io.github.gonalez.zfarmlimiter.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import io.github.gonalez.zfarmlimiter.util.Pair;

import java.util.Map;

/** An object type registry. */
public interface ObjectRegistry {
  static Builder newBuilder() {
    return new Builder.DefaultObjectRegistryBuilder();
  }

  <T> T get(String key, Class<T> type);
  <T> Iterable<T> getAll(String key, Class<T> type);

  /** Merges {@code this} values into the given ObjectRegistry builder. */
  void merge(ObjectRegistry.Builder objectRegistryBuilder);

  interface Builder {
    <T> Builder add(String name, Class<T> type, T value);

    ObjectRegistry build();

    final class DefaultObjectRegistryBuilder implements Builder {
      private final SetMultimap<Class<?>,
          Pair<String, Object>> values = LinkedHashMultimap.create();

      @Override
      public <T> Builder add(String name, Class<T> type, T value) {
        values.put(type, Pair.create(name, value));
        return this;
      }

      public ObjectRegistry build() {
        return new ObjectRegistry() {
          @Override
          public <T> T get(String key, Class<T> type) {
            return Iterables.getFirst(getAll(key, type), null);
          }

          @SuppressWarnings("unchecked")
          @Override
          public <T> Iterable<T> getAll(String key, Class<T> type) {
            ImmutableList.Builder<T> builder = ImmutableList.builder();
            for (Pair<String, Object> get : values.get(type)) {
              if (get.getKey().equals(key)) {
                builder.add((T) get.getValue());
              }
            }
            return builder.build();
          }

          @SuppressWarnings("unchecked")
          @Override
          public void merge(Builder objectRegistryBuilder) {
            for (Map.Entry<Class<?>, Pair<String, Object>> entry : values.entries()) {
              Pair<String, Object> value = entry.getValue();
              objectRegistryBuilder.add(value.getKey(), (Class) entry.getKey(), value.getValue());
            }
          }
        };
      }
    }
  }
}
