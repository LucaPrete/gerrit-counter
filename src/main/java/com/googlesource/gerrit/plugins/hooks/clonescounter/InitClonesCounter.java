package com.googlesource.gerrit.plugins.hooks.clonescounter;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.AllProjectsConfig;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.util.ConsoleUI;

public class InitClonesCounter implements InitStep {
    private final String pluginName;
    private final ConsoleUI ui;
    private final AllProjectsConfig allProjectsConfig;
    
    private static final Logger log = LoggerFactory.getLogger(InitClonesCounter.class);
    
    public InitClonesCounter(@PluginName String pluginName, ConsoleUI ui,
        AllProjectsConfig allProjectsConfig) {
      this.pluginName = pluginName;
      this.ui = ui;
      this.allProjectsConfig = allProjectsConfig;
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public void postRun() throws Exception {
      ui.message("\n");
      ui.header(pluginName + " Integration");
      boolean enabled = ui.yesno(true, "By default enabled for all projects");
      Config cfg = allProjectsConfig.load();
      log.info(cfg.toString());
      if (enabled) {
        cfg.setBoolean("plugin", pluginName, "enabled", enabled);
      } else {
        cfg.unset("plugin", pluginName, "enabled");
      }
      allProjectsConfig.save(pluginName, "Initialize " + pluginName + " Integration");
    }
  }