package com.googlesource.gerrit.plugins.clonecounter;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.AllProjectsConfig;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.util.ConsoleUI;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is mostly sample code. We are not using it, and it is not enabled.
 * <p>
 *     The purpose of this code is to add a step to the gerrit initialization
 *     (i.e. gerrit install or upgrade). We can do this manually for now.
 * </p>
 */
public class InitClonesCounter implements InitStep {
    private static final Logger log = LoggerFactory.getLogger(InitClonesCounter.class);

    private final String pluginName;
    private final ConsoleUI ui;
    private final AllProjectsConfig allProjectsConfig;

    public InitClonesCounter(@PluginName String pluginName,
                             ConsoleUI ui,
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
        //FIXME
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