package com.code.common.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * load
 *
 * @author Pan Jiebin
 * @date 2021-03-02 15:13
 */
public class FileSystemClassLoader extends URLClassLoader {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemClassLoader.class);

    private String name;
    private String version;

    public FileSystemClassLoader(URL[] urls, ClassLoader parent, String name, String version) {
        super(urls, parent);
        this.name = name;
        this.version = version;
    }

    public FileSystemClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public FileSystemClassLoader(URL[] urls) {
        super(urls);
    }

    public FileSystemClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            clazz = this.loadClassFromSelf(name, resolve);
        } catch (ClassNotFoundException e) {
            //ignore
        }
        if (clazz == null) {
            clazz = loadClassFromParent(name, resolve);
        }
        return clazz;
    }

    private synchronized Class<?> loadClassFromSelf(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;
        if ((clazz = findLoadedClass(name)) != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        if ((clazz = findClass(name)) != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            if (logger.isTraceEnabled()) {
                logger.trace("[{}] loaded from self [name = {}, version = {}]", name, this.name, this.version);
            }
            return clazz;
        }
        throw new ClassNotFoundException("Could not find: {}" + name);
    }

    private Class<?> loadClassFromParent(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;
        if ((clazz = getParent().loadClass(name)) != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("[{}] loaded from parent.", name);
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        throw new ClassNotFoundException("Could not find: {}" + name);
    }
}
