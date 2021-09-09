package com.code.common.resource.loader.hdfs;

import com.code.common.resource.ResourceLoader;
import com.code.common.resource.Resources;
import org.apache.hadoop.fs.FileStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Pan Jiebin
 * @date 2021-03-03 11:35
 */
public class HDFSResourceLoader implements ResourceLoader {

    private final HDFSClient client;

    public HDFSResourceLoader(HDFSClient client) {
        this.client = client;
    }

    @Override
    public URL[] findResources(String baseDir, String name, String version) {
        return findResources(baseDir + "/" + name + "/" + version);
    }

    @Override
    public URL[] findResources(String path) {
        try {
            return client.getURLs(path);
        } catch (FileNotFoundException e) {
            //ignore
        }
        return null;
    }

    @Override
    public Set<Resources> findResourceInfo(String baseDir) {
        Set<Resources> versions = null;
        try {
            FileStatus[] nameFiles = client.listStatus(baseDir);
            if (nameFiles == null || nameFiles.length == 0) {
                return Collections.emptySet();
            }
            versions = new HashSet<>();
            for (FileStatus nameFile : nameFiles) {
                String name = getFolderName(nameFile);
                Resources version = new Resources(name);
                FileStatus[] versionFiles = client.listStatus(baseDir + "/" + name);
                if (versionFiles != null && versionFiles.length != 0) {
                    for (FileStatus versionFile : versionFiles) {
                        version.addVersion(getFolderName(versionFile));
                    }
                }
                versions.add(version);
            }
        } catch (IOException e) {
            //ignore
        }
        return versions == null ? Collections.emptySet() : versions;
    }

    private String getFolderName(FileStatus fileStatus) {
        String codeFilePath = fileStatus.getPath().getName();
        return codeFilePath.substring(codeFilePath.lastIndexOf("/") + 1);
    }
}
