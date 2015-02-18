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

package com.googlesource.gerrit.plugins.clonecounter;

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
public class CloneCounterHook implements PreUploadHook {
    
  private static final Logger log = LoggerFactory.getLogger(CloneCounterHook.class);
  private final ArrayList<String> activeRepos;
  private final DBConnection db;
  private final PluginConfig pluginConfig;
	
  public CloneCounterHook(PluginConfig config) {
    this.pluginConfig = config;
    this.activeRepos = new ArrayList<String>(Arrays
        .asList(pluginConfig.getString("activeRepos", "").split(",")));
    HashMap<String, String> dbConfig = new HashMap<String, String>();
    dbConfig.put("dbUrl", pluginConfig.getString("dbUrl", "127.0.0.1"));
    dbConfig.put("dbPort", pluginConfig.getString("dbPort", "5432"));
    dbConfig.put("dbUser", pluginConfig.getString("dbUser", "admin"));
    dbConfig.put("dbPass", pluginConfig.getString("dbPass", "pass"));
    dbConfig.put("dbName", pluginConfig.getString("dbName", "default-db"));
    dbConfig.put("dbTable", pluginConfig.getString("dbTable", "default-table"));
    dbConfig.put("dbDateCol", pluginConfig.getString("dbDateCol", "date"));
    dbConfig.put("dbCounterCol", pluginConfig.getString("dbCounterCol", "clones"));
    dbConfig.put("dbRepoCol", pluginConfig.getString("dbRepoCol", "repos"));
    this.db = new DBConnection(dbConfig);
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
      Collection<? extends ObjectId> collection, Collection<? extends ObjectId> haves)
          throws ServiceMayNotContinueException {
    String[] repoPathSplitted = uploadPack.getRepository()
        .toString().split("/");
    String repoName = repoPathSplitted[repoPathSplitted.length-1]
        .split("\\.")[0];
    if (haves == null || haves.isEmpty()) {
      log.debug("Repository {} cloned.", repoName);
            if (activeRepos != null && activeRepos.size()>0
                && !activeRepos.get(0).equals("")) {
              if (activeRepos.contains(repoName)) {
                log.debug("{} is a repository to be tracked. Incrementing counter in DB.", repoName);
                incrementCount(repoName);
              }
            } else {
              log.debug("{} is a repository to be tracked. Incrementing counter in DB.", repoName);
              incrementCount(repoName);
            }
    }
  }

  private void incrementCount(String repo) {
    log.debug("Incrementing for repo {}", repo);
    db.incrementClonesCounter(repo);
  }
}