package com.code.common.resource.loader.hdfs;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Optional;

/**
 *  URLStreamHandlerFactory Decorator
 *
 * @author Pan Jie Bin
 * @date 2020-03-25 15:57
 */
public class URLStreamHandlerFactoryDecorator implements URLStreamHandlerFactory {

    private final Optional<URLStreamHandlerFactory> delegate;

    private final URLStreamHandlerFactory fsUrlStreamHandlerFactory = new FsUrlStreamHandlerFactory();

    private static final String PROTOCOL_HDFS = "hdfs";

    public URLStreamHandlerFactoryDecorator() {
        this(null);
    }

    public URLStreamHandlerFactoryDecorator(URLStreamHandlerFactory delegate) {
        this.delegate = Optional.ofNullable(delegate);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        //hdfs protocol
        if (PROTOCOL_HDFS.equals(protocol)) {
            return fsUrlStreamHandlerFactory.createURLStreamHandler(protocol);
        }
        return this.delegate.map(factory -> factory.createURLStreamHandler(protocol)).orElse(null);
    }

}
