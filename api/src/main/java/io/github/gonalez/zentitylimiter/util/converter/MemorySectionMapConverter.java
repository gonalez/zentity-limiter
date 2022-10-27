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

import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Map;

/** Support for converting yaml configuration (MemorySection) into map. */
public class MemorySectionMapConverter implements ObjectConverter<MemorySection, Map> {

  @Override
  public Class<MemorySection> requiredType() {
    return MemorySection.class;
  }

  @Override
  public Class<Map> convertedType() {
    return Map.class;
  }

  @Override
  public Map<?, ?> convert(MemorySection key) {
    Map map = new HashMap<>();
    for (String sectionKey : key.getKeys(true)) {
      map.put(sectionKey, key.get(sectionKey));
    }
    return map;
  }
}
