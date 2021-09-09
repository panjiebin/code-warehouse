package com.code.common.resource.loader.local;

import com.code.common.resource.ResourceLoader;
import com.code.common.resource.Resources;
import com.code.common.utils.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Pan Jiebin
 * @date 2021-03-02 15:52
 */
public class LocalFSResourceLoader implements ResourceLoader {

    @Override
    public URL[] findResources(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        File dir = new File(path);
        List<URL> urls = new ArrayList<>();
        try {
            if (dir.exists() && dir.isDirectory() && dir.canRead()) {
                List<File> allFiles = FileUtils.getAllFiles(dir);
                for (File file : allFiles) {
                    urls.add(file.toURI().toURL());
                }
            }
        } catch (MalformedURLException e) {
            //ignore
        }
        if (urls.isEmpty()) {
            return null;
        }
        URL[] arr = new URL[urls.size()];
        return urls.toArray(arr);
    }

    @Override
    public Set<Resources> findResourceInfo(String baseDir) {
        File[] nameFiles = new File(baseDir).listFiles();
        if (nameFiles == null || nameFiles.length == 0) {
            return Collections.emptySet();
        }
        Set<Resources> versions = new HashSet<>();
        for (File nameFile : nameFiles) {
            Resources version = new Resources(nameFile.getName());
            File[] versionFiles = nameFile.listFiles();
            if (versionFiles != null && versionFiles.length != 0) {
                for (File versionFile : versionFiles) {
                    version.addVersion(versionFile.getName());
                }
            }
            versions.add(version);
        }
        return versions;
    }
}
