package com.github.alantr7.torus.api.resource;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public class ResourceLocation {

    public final Container container;

    public final String relativePath;

    public final Container fallbackContainer;

    public final String fallbackRelativePath;

    public ResourceLocation(Container container, String relativePath) {
        this(container, relativePath, null, null);
    }

    public ResourceLocation(Container container, String relativePath, Container fallbackContainer, String fallbackRelativePath) {
        this.container = container;
        this.relativePath = relativePath;
        this.fallbackContainer = fallbackContainer;
        this.fallbackRelativePath = fallbackRelativePath;
    }

    public boolean exists() {
        return getResource() == null;
    }

    @Nullable
    public InputStream getResource() {
        InputStream primary = container.resourceGetFunction.apply(relativePath);
        if (primary != null)
            return primary;

        return fallbackContainer != null && fallbackRelativePath != null ? fallbackContainer.resourceGetFunction.apply(fallbackRelativePath) : null;
    }

}
