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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zfarmlimiter.registry.ObjectRegistry;

import javax.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public abstract class FileWritingRuleSerializer extends AbstractBuilderRuleSerializer {
  static final String RULE_FILE_CONTEXT_VALUE_NAME = "file";

  protected final File path;

  protected ImmutableMap<Rule, RuleFileWritingInfo> rules;

  public FileWritingRuleSerializer(boolean checkForMissingFields, Path path) throws IOException {
    super(checkForMissingFields);
    checkNotNull(path);
    File pathFile = (this.path = path.toFile());
    if (!pathFile.exists() && !pathFile.mkdirs()) {
      throw new IOException("Could not create directory: " + path);
    } else if(!pathFile.isDirectory()) {
      throw new IOException(path + " is not a directory");
    }
  }

  public void init() {
    if (rules == null) {
      this.rules = fetchRulesRecursively(path);
    }
  }

  private ImmutableMap<Rule, RuleFileWritingInfo> fetchRulesRecursively(File dir) {
    File[] files = dir.listFiles();
    ImmutableMap.Builder<Rule, RuleFileWritingInfo> ruleFileBuilder = ImmutableMap.builder();
    if (files == null) {
      return ruleFileBuilder.build();
    }
    for (File file : files) {
      try {
        RuleSerializerContext context = read(file);
        Rule rule = deserialize(context, null);

        ObjectRegistry.Builder builder =
            ObjectRegistry.newBuilder()
                .add(RULE_FILE_CONTEXT_VALUE_NAME, File.class, file);
        context.merge(builder);

        ruleFileBuilder.put(rule,
            new RuleFileWritingInfo(file,
                RuleSerializerContext.of(builder.build())));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      ruleFileBuilder.putAll(fetchRulesRecursively(file));
    }
    return ruleFileBuilder.build();
  }

  protected abstract RuleSerializerContext read(File file);


  protected File getFile(Rule rule, RuleSerializerContext ctx) {
    return ctx.get(RULE_FILE_CONTEXT_VALUE_NAME, File.class);
  }

  @Override
  public void serialize(Rule rule, RuleSerializerContext context) throws IOException {
    File file = getFile(rule, context);
    if (!file.exists() && !file.createNewFile()) {
      throw new IOException("Failed to create file: " + file);
    }
    super.serialize(rule, read(file));
  }

  @Override
  public Rule deserialize(RuleSerializerContext context, @Nullable Visitor visitor) throws IOException {
    Rule findRule = super.deserialize(context, visitor);
    read(getFile(findRule, context));
    return findRule;
  }

  private static class RuleFileWritingInfo {
    final File file;
    final RuleSerializerContext context;

    public RuleFileWritingInfo(File file, RuleSerializerContext context) {
      this.file = file;
      this.context = context;
    }
  }
}
