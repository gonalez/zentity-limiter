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
package io.github.gonalez.zfarmlimiter.rule;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class FileRuleCollection implements RuleCollection {
  private final Path path;
  private final ImmutableList<Rule> predefineRules;

  private ImmutableList<Rule> rules = null;

  public FileRuleCollection(Path path, ImmutableList<Rule> predefineRules) throws IOException {
    this.path = checkNotNull(path);
    File toFile = path.toFile();
    if (!toFile.exists()) {
      if (!toFile.mkdirs()) {
        throw new IOException("failed to create directory: " + path);
      }
    } else if (!toFile.isDirectory()) {
      throw new IOException(path + " is not a directory");
    }
    this.predefineRules = predefineRules;
  }

  protected abstract Rule loadRule(File file) throws IOException;
  protected void onLoadRule(File file, Rule rule) throws IOException {}

  @Override
  public synchronized ImmutableList<Rule> getRules() {
    if (rules == null) {
      ImmutableList.Builder<Rule> ruleBuilder = ImmutableList.builder();
      try (Stream<Path> paths = Files.walk(path)) {
        paths.forEach(
            path -> {
              File toFile = path.toFile();
              try {
                Rule rule = loadRule(toFile);
                if (rule != null) {
                  onLoadRule(toFile, rule);
                  ruleBuilder.add(rule);
                }
              } catch (IOException e) {
                // ignore for now...
              }
            });
      } catch (IOException e) {
        throw new IOError(e);
      }
      rules = ruleBuilder.build();
    }
    return rules;
  }

  @Override
  public synchronized void invalidateCache() {
    rules = null;
  }
}
