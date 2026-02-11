package com.github.alantr7.torus.api.resource;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class Resource {

    @Nullable
    public final File file;

    @Nullable
    public final InputStream stream;

    public Resource(@Nullable File file, @Nullable InputStream stream) {
        this.file = file;
        this.stream = stream;
    }

    public boolean exists() {
        if (file != null)
            return file.exists();

        return stream != null;
    }

}
