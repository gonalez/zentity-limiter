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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/** Implementation of {@link EntityCheckingTask} which uses runnable. */
public class RunnableEntityCheckingTask implements EntityCheckingTask, Runnable {
  private final BlockingDeque<Entity> entities;
  private final Thread thread;

  private volatile boolean started = false;

  private final List<Callback> callbacks = new ArrayList<>();
  private final List<EntityHandler> entityHandlers = new ArrayList<>();

  private final TimeUnit unit;
  private final long interval;

  public RunnableEntityCheckingTask(TimeUnit unit, long interval) {
    this.unit = unit;
    this.interval = interval;

    this.entities = new LinkedBlockingDeque<>();
    this.thread = new Thread(this, "entity-checking-task");
  }

  @Override
  public long intervalMs() {
    return interval;
  }

  @Override
  public synchronized void start() {
    if (isStarted()) {
      return;
    }

    thread.start();
    started = true;
  }

  @Override
  public synchronized void shutdown() {
    if (!isStarted()) {
      return;
    }

    if (!thread.isInterrupted()) {
      thread.interrupt();
    }

    started = false;
  }

  public synchronized boolean isStarted() {
    return started;
  }

  @Override
  public void addCallback(Callback callback) {
    callbacks.add(callback);
  }

  @Override
  public void addEntityForChecking(Entity entity) {
    entities.add(entity);
  }

  @Override
  public void addHandler(EntityHandler entityHandler) {
    entityHandlers.add(entityHandler);
  }

  @Override
  public void run() {
    try {
      while (isStarted()) {
        // If the queue is empty, execute the onAllEntitiesChecked callbacks and sleep the thread to wait a little
        // for next execution.
        if (entities.isEmpty()) {
          // Run onAllEntitiesChecked callbacks, useful to add new entities to this task.
          callbacks.forEach(Callback::onAllEntitiesChecked);

          // Sleep the thread before checking again, we use the unit and interval given when constructing
          // the instance for determining for how long we have to wait until next check. Note that the thread
          Thread.sleep(unit.toMillis(interval));
        }
        Entity entity = entities.takeFirst();
        for (EntityHandler entityHandler : entityHandlers) {
          entityHandler.handle(entity);
        }
      }
    } catch (InterruptedException interruptedException) {
      // just ignore
    }
  }
}
