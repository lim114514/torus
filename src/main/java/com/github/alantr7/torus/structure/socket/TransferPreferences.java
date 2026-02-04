package com.github.alantr7.torus.structure.socket;

import java.util.Collections;
import java.util.Set;

public class TransferPreferences {

    public static final TransferPreferences DEFAULT = new TransferPreferences(Collections.emptySet(), (byte) 0);

    public final Set<String> nodeList;

    public final byte mode;

    public static final byte MODE_BLACKLIST = 0;

    public static final byte MODE_WHITELIST = 1;

    public TransferPreferences(Set<String> nodeList, byte mode) {
        this.nodeList = nodeList;
        this.mode = mode;
    }

    public boolean isWhitelisted(String namespacedId) {
        if (mode == MODE_BLACKLIST)
            return !nodeList.contains(namespacedId);

        return nodeList.contains(namespacedId);
    }

}
