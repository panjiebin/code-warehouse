package com.code.common.resource;

import java.io.File;
import java.net.URL;
import java.util.Set;

/**
 * @author Pan Jiebin
 * @date 2021-03-02 15:45
 */
public interface ResourceLoader {

    /**
     * find class URLs by certain code and version
     * @param baseDir base directory
     * @param name name
     * @param version version
     * @return URLs
     */
    default URL[] findResources(String baseDir, String name, String version) {
        return findResources(baseDir + File.separator + name + File.separator + version);
    }

    /**
     * find class URLs by certain dir path
     * @param path dir path
     * @return URLs
     */
    URL[] findResources(String path);

    /**
     * find all resource versions in certain directory
     * @param baseDir base directory
     * @return resource versions
     */
    Set<Resources> findResourceInfo(String baseDir);
}
