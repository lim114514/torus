package com.github.alantr7.torus.api.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class Resource {

    @Nullable
    public final File file;

    @NotNull
    public final InputStream stream;

    public Resource(@Nullable File file, @NotNull InputStream stream) {
        this.file = file;
        this.stream = stream;
    }

}
