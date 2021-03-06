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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.PreUploadHook;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.UploadPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.server.config.PluginConfig;

/**
 * CloneCounterHook is a PreUploadHook that is called before the server
 * returns data in response to a git clone|fetch|pull.
 * <p>
 *     This hook identifies git clones and records it in a Postgres database.
 * </p>
 */
public class GerritCounterHook implements PreUploadHook {
    
  private static final Logger log = LoggerFactory.getLogger(GerritCounterHook.class);
  private final ArrayList<String> activeRepos;
  private final ArrayList<ClientAction.Type> activeTrackers;
  private final DBConnection db;
  private final PluginConfig pluginConfig;
	
  public GerritCounterHook(PluginConfig config) {
    this.pluginConfig = config;
    HashMap<String, String> dbConfig = new HashMap<String, String>();
    dbConfig.put("dbUrl", pluginConfig.getString("dbUrl", "127.0.0.1"));
    dbConfig.put("dbPort", pluginConfig.getString("dbPort", "5432"));
    dbConfig.put("dbUser", pluginConfig.getString("dbUser", "admin"));
    dbConfig.put("dbPass", pluginConfig.getString("dbPass", "pass"));
    dbConfig.put("dbName", pluginConfig.getString("dbName", "default-db"));
    dbConfig.put("dbTable", pluginConfig.getString("dbTable", "default-table"));
    dbConfig.put("dbDateCol", pluginConfig.getString("dbDateCol", "date"));
    dbConfig.put("dbClonesCounterCol", pluginConfig
        .getString("dbClonesCounterCol", "dbClonesCounterCol"));
    dbConfig.put("dbUpdatesCounterCol", pluginConfig
        .getString("dbUpdatesCounterCol", "dbUpdatesCounterCol"));
    dbConfig.put("dbRepoCol", pluginConfig.getString("dbRepoCol", "repos"));
    dbConfig.put("timezone", pluginConfig.getString("timezone", ""));
    this.db = new DBConnection(dbConfig);
    this.activeRepos = new ArrayList<String>(Arrays
        .asList(pluginConfig.getString("activeRepos", "").split(",")));
    this.activeTrackers = stringToActionType(pluginConfig.getString("activeTrackers", ""));
  }
  
  private ArrayList<ClientAction.Type> stringToActionType(String activeTrackers) {
    ArrayList<ClientAction.Type> clientActionTypes = new ArrayList<ClientAction.Type>();
    for (String tracker : activeTrackers.split(",")) {
      clientActionTypes.add(ClientAction.getActionTypeFromString(tracker));
    }
    return clientActionTypes;
  }
  
  @Override
  public void onBeginNegotiateRound(UploadPack uploadPack,
      Collection<? extends ObjectId> collection, int i)
          throws ServiceMayNotContinueException {
    // nothing to do
  }

  @Override
  public void onEndNegotiateRound(UploadPack uploadPack,
      Collection<? extends ObjectId> collection,
      int i, int i1, boolean b)
          throws ServiceMayNotContinueException {
    // nothing to do
  }
  
  @Override
  public void onSendPack(UploadPack uploadPack,
      Collection<? extends ObjectId> collection,
      Collection<? extends ObjectId> haves)
          throws ServiceMayNotContinueException {
    ArrayList<String> repoTracked = getTrackedRepoList();
    ClientAction.Type requiredAction = cloneOrUpdate(haves);
    String requiredRepoName = getRepoName(uploadPack);
    log.debug("Client requires a {} on repository {}.", requiredAction,
        requiredRepoName);
    if (activeTrackers.contains(requiredAction)) {
      log.debug("Configuration requires to track the action {} just received +"
          + "for repository {}.", requiredAction, requiredRepoName);
      if (repoTracked != null && repoTracked.contains(requiredRepoName)) {
        db.incrementCounters(requiredAction, requiredRepoName);
      } else if (repoTracked == null) {
        db.incrementCounters(requiredAction, requiredRepoName);
      }
    }
  }
  
  /* Returns the request type checking what the client has.
   * If the client has nothing in the request, it's cloning.
   * Otherwise the client is fetching or pulling
  */
  private ClientAction.Type cloneOrUpdate(Collection<? extends ObjectId> haves) {
    if (haves == null || haves.isEmpty()) return ClientAction.Type.CLONE;
    else return ClientAction.Type.UPDATE;
  }
  
  /*
   * Returns the repository name from uploadPack
   */
  private String getRepoName(UploadPack uploadPack) {
    String[] repoPathSplitted = uploadPack.getRepository()
        .toString().split("/");
    String repoName = repoPathSplitted[repoPathSplitted.length-1]
        .split("\\.")[0].toLowerCase();
    return repoName;
  }
  
  /* Returns the list of repositories to be tracked if the user
   * specified specific repositories. It returns null if no
   * repositories have been set. 
   */
  private ArrayList<String> getTrackedRepoList() {
    if (activeRepos != null && activeRepos.size()>0
        && !activeRepos.get(0).equals("")) {
      return activeRepos;
    }
    return null;
  }
}