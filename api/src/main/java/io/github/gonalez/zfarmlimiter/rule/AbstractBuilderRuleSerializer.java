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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.gonalez.zfarmlimiter.util.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractBuilderRuleSerializer implements RuleSerializer {
  // Defines which characters a rule builder method should start with
  private static final String SET_BUILDER_METHOD_START = "set";

  private static final ImmutableSet<Method> IGNORED =
      ImmutableSet.<Method>builder()
          .add(Object.class.getDeclaredMethods())
          .build();

  private final Pair</*builder=*/Method, /*rule=*/Method> builderRuleMethodPair;

  private final ImmutableMap<String, Method> builderMethods;

  public AbstractBuilderRuleSerializer() {
    Class<? extends Rule> ruleClass = ruleType();

    builderRuleMethodPair =
        Arrays.stream(ruleClass.getDeclaredMethods())
            .map(method ->
                recursivelyFindMethodForReturnType(new HashSet<>(), method, ruleClass))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException("No builder method found for rule: " + ruleClass));

    Method builderRuleMethod = builderRuleMethodPair.getKey();
    Method buildRuleMethod = builderRuleMethodPair.getValue();

    checkState(Modifier.isStatic(builderRuleMethod.getModifiers()),
        "%s must be static", builderRuleMethod);
    checkState(builderRuleMethod.getParameterTypes().length == 0,
        "%s must have no parameters", builderRuleMethod);
    checkState(buildRuleMethod.getParameterTypes().length == 0,
        "%s must have no parameters", buildRuleMethod);

    ImmutableMap<String, Method> newBuilderMethods = findMethodsIgnoring(buildRuleMethod.getDeclaringClass());
    ImmutableMap.Builder<String, Method> builder = ImmutableMap.builder();

    for (Map.Entry<String, Method> entry : findMethodsIgnoring(ruleClass).entrySet()) {
      String capitalizedMethodName = capitalize(entry.getKey());

      Method maybeFindBuilderMethod = newBuilderMethods.get(SET_BUILDER_METHOD_START + capitalizedMethodName);
      if (maybeFindBuilderMethod == null) {
        throw new IllegalStateException(SET_BUILDER_METHOD_START + capitalizedMethodName);
      }

      checkState(maybeFindBuilderMethod.getParameterTypes().length == 1,
          "%s must have only one parameter", maybeFindBuilderMethod);
      Class<?> builderParameterType = maybeFindBuilderMethod.getParameterTypes()[0];
      checkState(builderParameterType == entry.getValue().getReturnType(),
          "parameter %s must assignable with %s",
          builderParameterType, entry.getValue().getReturnType());
      builder.put(entry.getKey(), maybeFindBuilderMethod);
    }
    this.builderMethods = builder.build();
  }

  /** The type of rule that this serializer expects. */
  protected abstract Class<? extends Rule> ruleType();

  /** Capitalizes the given {@link String}. */
  private static String capitalize(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  @Nullable
  private static Pair<Method, Method> recursivelyFindMethodForReturnType(
      Set<Class<?>> analyzedClasses, Method method, Class<?> findClass) {
    Class<?> clazz = method.getReturnType();
     if (analyzedClasses.contains(clazz)) {
       return null;
     }
    analyzedClasses.add(clazz);
    for (Method method1 : clazz.getDeclaredMethods()) {
      if (findClass.isAssignableFrom(method1.getReturnType())) {
        return Pair.create(method, method1);
      }
      Pair<Method, Method> findMethodForReturnType =
          recursivelyFindMethodForReturnType(analyzedClasses, method1, findClass);
      if (findMethodForReturnType != null) {
        return findMethodForReturnType;
      }
    }
    return null;
  }

  private static ImmutableMap<String, Method> findMethodsIgnoring(Class<?> clazz) {
    return ImmutableMap.copyOf(
        Arrays.stream(clazz.getDeclaredMethods())
            .filter(
                method ->
                    !IGNORED.contains(method)
                        && !Modifier.isStatic(method.getModifiers()))
            .collect(Collectors.toMap(Method::getName, Function.identity())));
  }

  @Override
  public Rule deserialize(RuleSerializerContext context) throws IOException {
    try {
      Object newBuilder = builderRuleMethodPair.getKey().invoke(null);
      for (Map.Entry<String, Method> entry : builderMethods.entrySet()) {
        Object maybeFind = context.get(entry.getKey(), entry.getValue().getParameterTypes()[0]);
        if (maybeFind == null) {
          throw new IOException("Could not find a value for rule property: " + entry.getKey());
        }
        entry.getValue().invoke(newBuilder, maybeFind);
      }
      return (Rule) builderRuleMethodPair.getValue().invoke(newBuilder);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
