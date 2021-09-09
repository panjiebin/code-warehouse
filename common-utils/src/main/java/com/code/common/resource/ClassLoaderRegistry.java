package com.code.common.resource;

import com.code.common.classloader.FileSystemClassLoader;
import com.code.common.resource.loader.local.LocalFSResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * @author Pan Jiebin
 * @date 2021-03-02 15:31
 */
public class ClassLoaderRegistry {

    private final static Logger logger = LoggerFactory.getLogger(ClassLoaderRegistry.class);

    private final List<ResourceHotLoader> hotLoaders = new ArrayList<>();
    private ResourceLoader defaultLoader = new LocalFSResourceLoader();
    private final Map<String, ClassLoaderKey> keys = new HashMap<>();
    private final Map<ClassLoaderKey, ClassLoader> classLoaders = new HashMap<>();

    private ClassLoaderRegistry() {
    }

    private static class SingletonHolder {
        private final static ClassLoaderRegistry INSTANCE = new ClassLoaderRegistry();
    }

    public synchronized void register(String baseDir) {
        register(baseDir, defaultLoader);
    }

    public synchronized void register(String baseDir, ResourceLoader loader) {
        register(baseDir, Thread.currentThread().getContextClassLoader(), loader);
    }

    public synchronized void register(String baseDir, ClassLoader parent, ResourceLoader loader) {
        if (logger.isDebugEnabled()) {
            logger.debug("Start registering directory [{}] class loader", baseDir);
        }
        ClassLoader pcl = parent == null ? Thread.currentThread().getContextClassLoader() : parent;
        Set<Resources> resources = loader.findResourceInfo(baseDir);
        if (resources.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("base directory [{}] could not find resources", baseDir);
            }
            return;
        }
        for (Resources resource : resources) {
            Set<String> versions = resource.getVersions();
            if (!versions.isEmpty()) {
                versions.forEach(version -> {
                    String name = resource.getResourceName();
                    URL[] urls = loader.findResources(baseDir, name, version);
                    FileSystemClassLoader classLoader = createFSClassLoader(pcl, name, version, urls);
                    this.register(name, version, classLoader);
                });
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Registered base directory [{}] completed", baseDir);
        }
    }

    public synchronized void register(String name, String version, ClassLoader classLoader) {
        ClassLoaderKey key = getKey(name, version);
        ClassLoader loader = classLoaders.get(key);
        if (loader == null) {
            this.classLoaders.put(key, classLoader);
            this.keys.put(buildKeyStr(name, version), key);
            if (logger.isDebugEnabled()) {
                logger.debug("registered [name = {}, version = {}]", name, version);
            }
        }
    }

    public synchronized ClassLoader getClassLoader(String name, String version) {
        ClassLoaderKey key = getKey(name, version);
        ClassLoader classLoader = this.classLoaders.get(key);
        if (classLoader == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("class loader [name = {}, version = {}] could not find!", name, version);
                logger.debug("Try getting from hot loaders");
                URL[] urls = tryGetFromHotLoaders(name, version);
                if (urls != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("create class loader for resource from hot loader");
                    }
                    classLoader = createFSClassLoader(Thread.currentThread().getContextClassLoader(), name, version, urls);
                    register(name, version, classLoader);
                }
            }
        }
        return classLoader;
    }

    public synchronized void removeClassLoader(String name, String version) {
        this.classLoaders.remove(this.getKey(name, version));
    }

    private URL[] tryGetFromHotLoaders(String name, String version) {
        if (!hotLoaders.isEmpty()) {
            for (ResourceHotLoader hotLoader : hotLoaders) {
                URL[] urls = hotLoader.findResources(name, version);
                if (logger.isDebugEnabled()) {
                    logger.debug("loaded resource from hot loader [{}], resource path [{}]", hotLoader.getName(), hotLoader.getBaseDir());
                }
                if (urls != null && urls.length != 0) {
                    return urls;
                }
            }
        }
        return null;
    }

    public synchronized void clear() {
        this.keys.clear();
        this.classLoaders.clear();
        this.hotLoaders.clear();
    }

    public synchronized void addResourceHotLoader(ResourceHotLoader loader) {
        this.hotLoaders.add(loader);
    }

    public synchronized void addResourceHotLoaders(List<ResourceHotLoader> loaders) {
        this.hotLoaders.addAll(loaders);
    }

    public synchronized List<ResourceHotLoader> getResourceHotLoaders() {
        return new ArrayList<>(hotLoaders);
    }

    public synchronized int size() {
        return classLoaders.size();
    }

    private FileSystemClassLoader createFSClassLoader(ClassLoader parent, String name, String version, URL[] urls) {
        return new FileSystemClassLoader(urls, parent, name, version);
    }

    private ClassLoaderKey getKey(String name, String version) {
        String keyStr = buildKeyStr(name, version);
        return keys.containsKey(keyStr) ? keys.get(keyStr) : buildKey(name, version);
    }

    private String buildKeyStr(String name, String version) {
        return name + "_" + version;
    }

    private ClassLoaderKey buildKey(String name, String version) {
        return new ClassLoaderKey(name, version);
    }

    private static class ClassLoaderKey {
        private final String name;
        private final String version;

        public ClassLoaderKey(String name, String version) {
            this.name = name;
            this.version = version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ClassLoaderKey that = (ClassLoaderKey) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(version, that.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, version);
        }
    }

    public synchronized void setDefaultLoader(ResourceLoader defaultLoader) {
        this.defaultLoader = defaultLoader;
    }

    public static ClassLoaderRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
