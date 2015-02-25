package com.googlesource.gerrit.plugins.clonecounter;

public class ClientAction {
  
  public static enum Type {
    CLONE,
    UPDATE
  }
  
  public static ClientAction.Type getActionTypeFromString(String activeTracker) {
    for (Type t : Type.values()) {
      if (t.toString().equalsIgnoreCase(activeTracker)) {
        return t;
      }
    }
    return null;
  }
}
