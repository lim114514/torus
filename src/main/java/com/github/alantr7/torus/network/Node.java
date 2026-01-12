package com.github.alantr7.torus.network;

import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Socket;

public class Node {

    public final StructureInstance structure;

    public final Socket socket;

    public Node(StructureInstance structure, Socket socket) {
        this.structure = structure;
        this.socket = socket;
    }

}
