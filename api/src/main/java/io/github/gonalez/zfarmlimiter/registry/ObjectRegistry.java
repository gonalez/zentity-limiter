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
import com.google.common.collect.Iterables;
import io.github.gonalez.zfarmlimiter.util.Pair;

/** An object type registry. */
public interface ObjectRegistry {
  static Builder newBuilder() {
    return new Builder.DefaultObjectRegistryBuilder();
  }

  <T> T get(String key, Class<T> type);
  <T> Iterable<T> getAll(String key, Class<T> type);

  interface Builder {
    <T> Builder add(String name, Class<T> type, T value);

    ObjectRegistry build();

    final class DefaultObjectRegistryBuilder implements Builder {
      private final ImmutableMultimap.Builder<Class<?>,
          Pair<String, Object>> values = ImmutableMultimap.builder();

      @Override
      public <T> Builder add(String name, Class<T> type, T value) {
        values.put(type, Pair.create(name, value));
        return this;
      }

      public ObjectRegistry build() {
        ImmutableMultimap<Class<?>, Pair<String, Object>> build = values.build();
        return new ObjectRegistry() {
          @Override
          public <T> T get(String key, Class<T> type) {
            return Iterables.getFirst(getAll(key, type), null);
          }

          @SuppressWarnings("unchecked")
          @Override
          public <T> Iterable<T> getAll(String key, Class<T> type) {
            ImmutableList.Builder<T> builder = ImmutableList.builder();
            for (Pair<String, Object> get : build.get(type)) {
              if (get.getKey().equals(key)) {
                builder.add((T) get.getValue());
              }
            }
            return builder.build();
          }
        };
      }
    }
  }
}
