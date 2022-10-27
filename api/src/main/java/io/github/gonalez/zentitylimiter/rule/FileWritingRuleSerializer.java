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
package io.github.gonalez.zentitylimiter.rule;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zentitylimiter.registry.ObjectRegistry;
import io.github.gonalez.zentitylimiter.util.converter.ObjectConverter;

import javax.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * A {@link AbstractBuilderRuleSerializer} which adds support for writing and adding values to the
 * context when serializing and deserializing rules.
 *
 * <p>{@code objectConverterRegistry} contains the necessary converters when a value on the context mismatches
 * with the necessary one, for example if we need ImmutableList, and we have List, then we can convert this
 * and continue with the deserialization safely.
 *
 * <p>{@code path} Is the path where the rules will be loaded/deserialized from, note that this will be done
 * recursively, so we will search for rules on all the paths within this directory too.
 */
public abstract class FileWritingRuleSerializer extends AbstractBuilderRuleSerializer {
  public static final String RULE_FILE_CONTEXT_VALUE_NAME = "file";

  protected final File path;

  protected ImmutableMap<Rule, RuleFileWritingInfo> rules;

  private final ImmutableMap<String, Rule> predefinedRules;

  private final boolean pathCreated;

  public FileWritingRuleSerializer(
      ObjectConverter.Registry objectConverterRegistry,
      boolean checkForMissingFields, Path path,
      ImmutableMap<String, Rule> predefinedRules) throws IOException {
    super(objectConverterRegistry, checkForMissingFields);
    checkNotNull(path);
    File pathFile = (this.path = path.toFile());
    if ((this.pathCreated =! pathFile.exists()) && !pathFile.mkdirs()) {
      throw new IOException("Could not create directory: " + path);
    } else if(!pathFile.isDirectory()) {
      throw new IOException(path + " is not a directory");
    }
    this.predefinedRules = checkNotNull(predefinedRules);
  }

  public void init() throws IOException {
    if (rules == null) {
      ImmutableMap.Builder<Rule,
          RuleFileWritingInfo> builder = ImmutableMap.builder();
      builder.putAll(fetchRulesRecursively(path));

      // Only set up the pre-defined rules if the path has been just created
      if (pathCreated) {
        for (Map.Entry<String, Rule> ruleEntry : predefinedRules.entrySet()) {
          Rule rule = ruleEntry.getValue();

          String fileName = ruleEntry.getKey() + "." + expectedFileExtension().replace(".", "");
          File file = path.toPath().resolve(fileName).toFile();

          RuleSerializerContext context = RuleSerializerContext.of(createBuilder(file).build());

          serialize(rule, context); // serialize values
          deserialize(context, null); // write

          builder.put(rule, new RuleFileWritingInfo(file, context));
        }
        this.rules = builder.build();
      }
    }
  }

  protected abstract String expectedFileExtension();

  protected ObjectRegistry.Builder createBuilder(File file) {
    return ObjectRegistry.newBuilder().add(RULE_FILE_CONTEXT_VALUE_NAME, File.class, file);
  }

  /**
   * Loads all the rules in the directory recursively,
   * throws an {@code IOException} if cannot, load a rule.
   */
  private ImmutableMap<Rule, RuleFileWritingInfo> fetchRulesRecursively(
      File dir) throws IOException {
    File[] files = dir.listFiles();
    ImmutableMap.Builder<Rule, RuleFileWritingInfo> ruleFileBuilder = ImmutableMap.builder();
    if (files == null) {
      return ruleFileBuilder.build();
    }
    for (File file : files) {
        RuleSerializerContext context = read(file);

        ObjectRegistry.Builder builder = createBuilder(file);
        context.merge(builder);

        ruleFileBuilder.put(deserialize(context, null),
            new RuleFileWritingInfo(file,
                RuleSerializerContext.of(builder.build())));
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

  /** Information about a rule for writing. */
  private static class RuleFileWritingInfo {
    final File file;
    final RuleSerializerContext context;

    public RuleFileWritingInfo(File file, RuleSerializerContext context) {
      this.file = file;
      this.context = context;
    }
  }
}
