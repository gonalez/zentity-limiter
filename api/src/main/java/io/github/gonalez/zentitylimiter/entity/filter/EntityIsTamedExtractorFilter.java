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

import io.github.gonalez.zentitylimiter.entity.ExtraEntityExtractorFilter;
import org.bukkit.entity.Tameable;

/** Filter that checks if the entity is tamed or not. */
public class EntityIsTamedExtractorFilter extends ExtraEntityExtractorFilter<Boolean, Tameable> {

  @Override
  public Class<Tameable> filterType() {
    return Tameable.class;
  }

  @Override
  public String getName() {
    return "tamed";
  }
  
  @Override
  protected boolean doAllowed(Boolean tamed, Tameable type) {
    return tamed == type.isTamed();
  }

  @Override
  protected Class<Boolean> valueType() {
    return boolean.class;
  }
}
