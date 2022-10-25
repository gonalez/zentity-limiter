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

import com.google.common.collect.ImmutableList;

import java.util.List;

@SuppressWarnings("unchecked")
public class ImmutableListConverter<T> implements ObjectConverter<List<T>, ImmutableList<T>> {

  @Override
  public Class<List<T>> requiredType() {
    return (Class<List<T>>) ((Class<?>) List.class);
  }

  @Override
  public Class<ImmutableList<T>> convertedType() {
    return (Class<ImmutableList<T>>) ((Class<?>) ImmutableList.class);
  }

  @Override
  public ImmutableList<T> convert(List<T> key) {
    return ImmutableList.copyOf(key);
  }

}
