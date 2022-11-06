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

import org.bukkit.entity.Entity;

/**
 * A {@link EntityCheckingTask} is responsible for {@link EntityChecker#check checking entities}, consecutively
 * every {@link #intervalMs interval}. Use the method {@link #addEntityForChecking(Entity)} to add a new entity
 * to be checked.
 */
public interface EntityCheckingTask {
  /** Callbacks to execute when entities in this task. */
  interface Callback {

    /** Called when there are no more pending entities to check. */
    default void onAllEntitiesChecked() {}
  }

  /** Every how often this task checks. */
  long intervalMs();

  /** Starts this entity checking task. */
  void start();

  /** Shutdowns this entity checking task. */
  void shutdown();

  /** Adds a new callback into this task. */
  void addCallback(Callback callback);

  /** Adds a new entity for being checked. */
  void addEntityForChecking(Entity entity);

  /** Adds a new entity checker. */
  void addEntityChecker(EntityChecker entityChecker);
}
