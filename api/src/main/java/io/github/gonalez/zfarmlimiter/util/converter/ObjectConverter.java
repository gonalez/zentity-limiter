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
package io.github.gonalez.zfarmlimiter.util.converter;

import io.github.gonalez.zfarmlimiter.util.Pair;

import java.util.HashMap;
import java.util.Map;

/** Converts a type to a different type. */
public interface ObjectConverter<K, V> {
  /** A registry of {@link ObjectConverter}s. */
  interface Registry {
    static Builder newBuilder() {
      return new Builder.DefaultObjectConverterBuilder();
    }

    <K, V> ObjectConverter<K,V> findConverter(Class<K> keyType, Class<V> valueType);

    /** Builder for {@link Registry}. */
    interface Builder {
      <K, V> Builder addConverter(ObjectConverter<K, V> objectConverter);

      Registry build();

      final class DefaultObjectConverterBuilder implements Builder {
        private final Map<Pair<Class<?>, Class<?>>,
            ObjectConverter<?, ?>> builder = new HashMap<>();

        @Override
        public <K, V> Builder addConverter(ObjectConverter<K, V> objectConverter) {
          builder.put(Pair.create(objectConverter.requiredType(),
              objectConverter.convertedType()), objectConverter);
          return this;
        }

        @Override
        public Registry build() {
          return new Registry() {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public <K, V> ObjectConverter<K, V> findConverter(Class<K> keyType, Class<V> valueType) {
              // TODO: a better way to cache this...
              Pair<Class<K>, Class<V>> pair = Pair.create(keyType, valueType);
              if (builder.containsKey(pair)) {
                return (ObjectConverter<K, V>) builder.get(pair);
              } else {
                for (Map.Entry<Pair<Class<?>, Class<?>>, ObjectConverter<?, ?>> entry : builder.entrySet()) {
                  if (entry.getKey().getKey().isAssignableFrom(keyType)) {
                    builder.put((Pair) pair, entry.getValue());
                    return findConverter(keyType, valueType);
                  }
                }
              }
              return null;
            }
          };
        }
      }
    }
  }

  /** The type being converted. */
  Class<K> requiredType();

  /** The converted type. */
  Class<V> convertedType();

  /** Converts {@code k} into {@code V}. */
  V convert(K key);
}
