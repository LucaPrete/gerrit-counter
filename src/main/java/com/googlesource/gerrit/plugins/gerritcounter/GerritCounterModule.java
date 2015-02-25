// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.gerritcounter;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.transport.PreUploadHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.config.FactoryModule;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;

/**
 * The main plugin module that is responsible for binding the hook when the
 * plugin is started.
 */
public class GerritCounterModule extends FactoryModule {

  private static final Logger log = LoggerFactory.getLogger(GerritCounterModule.class);
  private final String pluginName;

  private final PluginConfigFactory pluginCfgFactory;

  @Inject
  public GerritCounterModule(@PluginName String pluginName,
      @GerritServerConfig Config config,
      PluginConfigFactory pluginCfgFactory) {
    this.pluginName = pluginName;
    this.pluginCfgFactory = pluginCfgFactory;
    log.debug("Module started");
  }

  @Override
  protected void configure() {
    PluginConfig pluginConfig = pluginCfgFactory.getFromGerritConfig(pluginName);
    // Adding CloneCounterHook to the PreUploadHook set
    // Note: The chain of hooks is called in ReceiveFactory by Gerrit
    DynamicSet.bind(binder(), PreUploadHook.class)
      .toInstance(new GerritCounterHook(pluginConfig));
  }
}