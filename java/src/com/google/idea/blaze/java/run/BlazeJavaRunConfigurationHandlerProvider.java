/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.java.run;

import com.google.common.collect.ImmutableSet;
import com.google.idea.blaze.base.ideinfo.TargetIdeInfo;
import com.google.idea.blaze.base.ideinfo.TargetKey;
import com.google.idea.blaze.base.model.BlazeProjectData;
import com.google.idea.blaze.base.model.primitives.Kind;
import com.google.idea.blaze.base.model.primitives.Label;
import com.google.idea.blaze.base.model.primitives.RuleType;
import com.google.idea.blaze.base.model.primitives.TargetExpression;
import com.google.idea.blaze.base.run.BlazeCommandRunConfiguration;
import com.google.idea.blaze.base.run.confighandler.BlazeCommandRunConfigurationHandler;
import com.google.idea.blaze.base.run.confighandler.BlazeCommandRunConfigurationHandlerProvider;
import com.google.idea.blaze.base.sync.data.BlazeProjectDataManager;
import com.google.idea.blaze.java.sync.source.JavaLikeLanguage;
import javax.annotation.Nullable;

/** Java-specific handler provider for {@link BlazeCommandRunConfiguration}s. */
public class BlazeJavaRunConfigurationHandlerProvider
    implements BlazeCommandRunConfigurationHandlerProvider {

  private static final ImmutableSet<Kind> RELEVANT_RULE_KINDS =
      JavaLikeLanguage.getAllDebuggableKinds();

  /**
   * A fallback for {@link supports} that can be used when synced target data isn't available. Prefer to call
   * {@link supports} instead.
   */
  static boolean supportsKind(@Nullable Kind kind) {
    return RELEVANT_RULE_KINDS.contains(kind);
  }

  /**
   * Returns whether the Java handler should drive this configuration. True if either (a) the
   * target's synced {@link TargetIdeInfo} carries a {@code JavaIdeInfo} and is a test or binary, or (b) the rule kind
   * is explicitly registered as Java-like.
   */
  public static boolean supports(BlazeCommandRunConfiguration configuration) {
    TargetIdeInfo target = resolveTarget(configuration);

    if (target != null && target.getJavaIdeInfo() != null) {
      RuleType ruleType = target.getKind().getRuleType();

      if (ruleType == RuleType.TEST || ruleType == RuleType.BINARY) {
        return true;
      }
    }

    return supportsKind(configuration.getTargetKind());
  }

  @Nullable
  private static TargetIdeInfo resolveTarget(BlazeCommandRunConfiguration configuration) {
    TargetExpression target = configuration.getSingleTarget();

    if (!(target instanceof Label)) {
      return null;
    }

    BlazeProjectData projectData =
        BlazeProjectDataManager.getInstance(configuration.getProject()).getBlazeProjectData();

    if (projectData == null) {
      return null;
    }

    return projectData.getTargetMap().get(TargetKey.forPlainTarget((Label) target));
  }

  @Override
  public boolean canHandleKind(TargetState state, @Nullable Kind kind) {
    return supportsKind(kind);
  }

  @Override
  public boolean canHandleConfig(BlazeCommandRunConfiguration configuration) {
    return supports(configuration);
  }

  @Override
  public BlazeCommandRunConfigurationHandler createHandler(BlazeCommandRunConfiguration config) {
    return new BlazeJavaRunConfigurationHandler(config);
  }

  @Override
  public String getId() {
    return "BlazeJavaRunConfigurationHandlerProvider";
  }
}
