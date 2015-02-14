package com.googlesource.gerrit.plugins.clonecounter;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.PreUploadHook;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.UploadPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * CloneCounterHook is a PreUploadHook that is called before the server
 * returns data in response to a git clone|fetch|pull.
 * <p>
 *     This hook identifies git clones and records it in a database.
 * </p>
 */
public class CloneCounterHook implements PreUploadHook {

    private static final Logger log = LoggerFactory.getLogger(CloneCounterHook.class);

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
        Repository repo = uploadPack.getRepository();
        if (haves == null || haves.isEmpty()) {
            log.info("clone for repo {} / has {} ", repo, haves);
            incrementCount(repo);
        } else {
            log.info("fetch or pull for repo {} / has {}", repo, haves);
        }
    }

    private void incrementCount(Repository repo) {
        // TODO
        //db.incrementClonesCounter();
    }

    public interface Factory {
        CloneCounterHook create();
    }

}
