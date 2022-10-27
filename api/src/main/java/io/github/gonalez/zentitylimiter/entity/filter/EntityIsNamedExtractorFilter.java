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
package io.github.gonalez.zentitylimiter.entity.filter;

import io.github.gonalez.zentitylimiter.entity.AbstractEntityExtractorFilter;
import org.bukkit.entity.Entity;

public class EntityIsNamedExtractorFilter extends AbstractEntityExtractorFilter<Boolean, Entity> {

  @Override
  public Class<Entity> filterType() {
    return Entity.class;
  }

  @Override
  public String getName() {
    return "named";
  }

  @Override
  protected boolean doAllowed(Boolean value, Entity type) {
    return !value || type.getCustomName() != null;

  }

  @Override
  protected Class<Boolean> valueType() {
    return boolean.class;
  }
}
