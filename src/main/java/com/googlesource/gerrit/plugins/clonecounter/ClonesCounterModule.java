package com.googlesource.gerrit.plugins.clonecounter;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.config.FactoryModule;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.transport.PreUploadHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ClonesCounterModule extends FactoryModule {

  private static final Logger log = LoggerFactory.getLogger(ClonesCounterModule.class);
  
  private final String pluginName;
  private final Config gerritConfig;
  private final PluginConfigFactory pluginCfgFactory;
  
  private DBConnection db;
  
  @Inject
  public ClonesCounterModule(@PluginName String pluginName,
		                     @GerritServerConfig Config config,
                             PluginConfigFactory pluginCfgFactory) {
	  this.pluginName = pluginName;
	  this.gerritConfig = config;
	  this.pluginCfgFactory = pluginCfgFactory;
	  this.log.info("Plugin started");
  }
  

  @Override
  protected void configure() {
	  // TODO Auto-generated method stub
      log.info("binding pre upload hook");
      DynamicSet.bind(binder(), PreUploadHook.class)
              .to(CloneCounterHook.class);
      factory(CloneCounterHook.Factory.class);
      log.info("binder: {}", binder());
  }
}