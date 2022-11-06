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
package io.github.gonalez.zentitylimiter.util.converter;

import com.google.common.collect.ImmutableList;
import com.google.gson.internal.Primitives;

import java.util.stream.Collectors;

/** Helper class to ease the use of {@link ObjectConverter}s. */
public final class MoreObjectConverters {
  /** A {@link ObjectConverter.Registry} which adds converters for the immutable collections and more. */
  public static final ObjectConverter.Registry DEFAULT_REGISTRY =
      ObjectConverter.Registry.newBuilder()
          // Guava
          .addConverter(new ImmutableListConverter<>())
          .addConverter(new ImmutableMapConverter<>())
          // YAML configuration converters
          .addConverter(new MemorySectionMapConverter())
          // Primitives
          .addConverter(
              ImmutableList.of(boolean.class, int.class)
                  .stream()
                  .map(e ->
                      new ObjectConverter<Object, Object>() {
                        final Class<?> wrappedType = Primitives.wrap(e);

                        @Override
                        public Class requiredType() {
                          return wrappedType;
                        }

                        @Override
                        public Class convertedType() {
                          return e;
                        }

                        @Override
                        public Object convert(Object key) {
                          return requiredType();
                        }
                      }).collect(Collectors.toList()))
          .build();

  /** @return the {@link ObjectConverter#convertedType()} for the given class. */
  public static Class<?> getConvertedType(Class<?> clazz) {
    ObjectConverter<?, ?> objectConverter = DEFAULT_REGISTRY.findConverter(clazz, clazz);
    if (objectConverter != null) {
      return objectConverter.convertedType();
    }
    return clazz;
  }

  private MoreObjectConverters() {}
}
