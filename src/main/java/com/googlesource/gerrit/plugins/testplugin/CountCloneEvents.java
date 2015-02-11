package com.googlesource.gerrit.plugins.testplugin;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.PreUploadHook;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.UploadPack;

import java.util.Collection;

public class CountCloneEvents implements PreUploadHook {

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
  public void onSendPack(UploadPack up, Collection<? extends ObjectId> wants,
      Collection<? extends ObjectId> haves)
      throws ServiceMayNotContinueException {
    if(haves.isEmpty() || haves == null){
        // If the client doesn't already have anything it's a clone request
    }

  }

}
