package com.googlesource.gerrit.plugins.hooks.clonescounter;

import java.util.Collection;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.PreUploadHook;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.UploadPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class ClonesCounterModule extends AbstractModule implements PreUploadHook {

  private static final Logger log = LoggerFactory.getLogger(ClonesCounterModule.class);
  
  private final String pluginName;
  private final Config gerritConfig;
  private final PluginConfigFactory pluginCfgFactory;
  
  private DBConnection db;
  
  @Inject
  public ClonesCounterModule(@PluginName String pluginName,
		  @GerritServerConfig Config config, PluginConfigFactory pluginCfgFactory) {
	  this.pluginName = pluginName;
	  this.gerritConfig = config;
	  this.pluginCfgFactory = pluginCfgFactory;
	  this.log.info("Plugin started");
  }
  
  @Override
  public void onBeginNegotiateRound(UploadPack arg0,
  		  Collection<? extends ObjectId> arg1, int arg2)
		  throws ServiceMayNotContinueException {
	  // TODO Auto-generated method stub
  }

  @Override
  public void onEndNegotiateRound(UploadPack arg0,
		  Collection<? extends ObjectId> arg1, int arg2, int arg3, boolean arg4)
		  throws ServiceMayNotContinueException {
	  // TODO Auto-generated method stub
  }

  @Override
  public void onSendPack(UploadPack arg0, Collection<? extends ObjectId> arg1,
		  Collection<? extends ObjectId> haves)
		  throws ServiceMayNotContinueException {
	  if(haves.isEmpty() || haves == null){
			log.info("New git clone request found.");
			db.incrementClonesCounter();
	  }
  }

  @Override
  protected void configure() {
	  // TODO Auto-generated method stub
		
  }
}