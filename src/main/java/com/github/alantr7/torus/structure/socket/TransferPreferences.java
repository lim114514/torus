package com.github.alantr7.torus.structure.socket;

public class TransferPreferences {

    public final String[] nodeWhitelist;

    public final String[] nodeBlacklist;

    public TransferPreferences(String[] nodeWhitelist, String[] nodeBlacklist) {
        this.nodeWhitelist = nodeWhitelist;
        this.nodeBlacklist = nodeBlacklist;
    }

}
