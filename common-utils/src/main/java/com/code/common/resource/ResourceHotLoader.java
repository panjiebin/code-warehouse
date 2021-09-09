package com.code.common.resource;

import java.net.URL;

/**
 * @author Pan Jiebin
 * @date 2021-03-03 16:45
 */
public class ResourceHotLoader {

    private final String name;
    private final String baseDir;
    private final ResourceLoader loader;

    public ResourceHotLoader(String name, String baseDir, ResourceLoader loader) {
        this.name = name;
        this.baseDir = baseDir;
        this.loader = loader;
    }

    public URL[] findResources(String name, String version) {
        return loader.findResources(baseDir, name, version);
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getName() {
        return name;
    }
}
