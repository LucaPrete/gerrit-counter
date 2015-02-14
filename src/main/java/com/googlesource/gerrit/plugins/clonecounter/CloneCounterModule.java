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

/**
 * The main plugin module that is responsible for binding the hood when the
 * plugin is started.
 */
public class CloneCounterModule extends FactoryModule {
    private static final Logger log = LoggerFactory.getLogger(CloneCounterModule.class);

    private final String pluginName;
    private final Config gerritConfig;
    private final PluginConfigFactory pluginCfgFactory;

    private DBConnection db;

    @Inject
    public CloneCounterModule(@PluginName String pluginName,
                              @GerritServerConfig Config config,
                              PluginConfigFactory pluginCfgFactory) {
        this.pluginName = pluginName;
        this.gerritConfig = config;
        this.pluginCfgFactory = pluginCfgFactory;

        log.debug("Started");
    }

    @Override
    protected void configure() {
        // Adding CloneCounterHook to the PreUploadHook set
        // Note: The chain of hooks is called in ReceiveFactory by Gerrit
        DynamicSet.bind(binder(), PreUploadHook.class)
                .to(CloneCounterHook.class);
    }
}