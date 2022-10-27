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
package io.github.gonalez.zentitylimiter.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public final class Pair<K, V> {
  public static <K,V> Pair<K, V> create(K key, V value) {
    return new Pair<>(key, value);
  }

  private final K key;
  private final V value;

  private Pair(K key, V value) {
    this.key = checkNotNull(key);
    this.value = checkNotNull(value);
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equal(key, pair.key) && Objects.equal(value, pair.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, value);
  }
}
