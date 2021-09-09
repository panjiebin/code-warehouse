package com.code.common.resource.loader.hdfs;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author Pan Jie Bin
 * @date 2019-12-26 9:49
 */
public class HDFSClient{

    private final FileSystem fileSystem;
    private final String defaultFS;

    static {
        try {
            registerURLStreamHandlerFactory();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("set FsUrlStreamHandlerFactory failure!", e);
        }
    }

    public HDFSClient(String defaultFS, String hadoopLoginName) {
        this.defaultFS = defaultFS;
        Configuration configuration = new Configuration();
        try {
            fileSystem = FileSystem.get(new URI(defaultFS), configuration, hadoopLoginName);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerURLStreamHandlerFactory() throws NoSuchFieldException, IllegalAccessException {
        Field factoryField = URL.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        URLStreamHandlerFactory urlStreamHandlerFactory = (URLStreamHandlerFactory) factoryField.get(null);
        if (urlStreamHandlerFactory == null) {
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactoryDecorator());
        } else {
            Field lockField = URL.class.getDeclaredField("streamHandlerLock");
            lockField.setAccessible(true);
            synchronized (lockField.get(null)) {
                factoryField.set(null, null);
                URL.setURLStreamHandlerFactory(new URLStreamHandlerFactoryDecorator(urlStreamHandlerFactory));
            }
        }
    }

    public void deleteOnExit(String filePath) {
        try {
            fileSystem.deleteOnExit(getPath(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFromLocalFile(String localPath, String targetPath) {
        try {
            fileSystem.copyFromLocalFile(getPath(localPath), getPath(targetPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deepCopyFromLocalFile(String localFolder, String dstFolder) {
        this.traverseLocalFolder(localFolder, dstFolder);
    }

    public void copyFile(String src, String dst) {
        this.copyFile(getPath(src), getPath(dst));
    }

    public void copyDir(String src, String dst) {
        try {
            this.traverseHDFSFolder(getPath(src), dst);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void traverseHDFSFolder(Path folderPath, String dst) throws IOException {
        FileStatus[] fileStatuses = fileSystem.listStatus(folderPath);
        if (fileStatuses != null) {
            for (FileStatus fileStatus : fileStatuses) {
                if (fileStatus.isDirectory()) {
                    traverseHDFSFolder(fileStatus.getPath(), dst + "/" + fileStatus.getPath().getName());
                } else {
                    copyFile(fileStatus.getPath(), getPath(dst + "/" + fileStatus.getPath().getName()));
                }
            }
        }
    }

    public URL[] getURLs(String filePath) throws FileNotFoundException {
        try {
            FileStatus[] fileStatuses = fileSystem.listStatus(getPath(filePath));
            List<URL> urls = new ArrayList<>();
            for (FileStatus fileStatus : fileStatuses) {
                urls.add(fileStatus.getPath().toUri().toURL());
            }
            return urls.toArray(new URL[urls.size()]);
        } catch (IOException e) {
            throw new FileNotFoundException(filePath);
        }
    }

    public void write(byte[] bytes, String filePath) {
        String path = defaultFS + filePath;
        FSDataOutputStream fsDataOutputStream = null;
        try {
            fsDataOutputStream = fileSystem.create(getPath(path));
            fsDataOutputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fsDataOutputStream != null) {
                    fsDataOutputStream.flush();
                    fsDataOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeJar(Map<String, byte[]> map, String filePath, String jarName) {
        JarOutputStream out = null;
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
            String path = defaultFS  + filePath + "/"  + jarName + ".jar";
            FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(path));
            out = new JarOutputStream(fsDataOutputStream, manifest);
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                String classPath = entry.getKey().replaceAll("\\.", "/") + ".class";
                out.putNextEntry(new JarEntry(classPath));
                out.write(entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public FileStatus[] listStatus(String filePath) throws IOException {
        return fileSystem.listStatus(getPath(filePath));
    }

    public boolean exists(String filePath) throws IOException {
        return fileSystem.exists(getPath(filePath));
    }

    public InputStream open(String filePath) throws IOException {
        return fileSystem.open(getPath(filePath));
    }

    public OutputStream create(String path) throws IOException {
        return fileSystem.create(getPath(path));
    }

    public boolean isExists(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        return fileSystem.exists(getPath(path));
    }

    public void download(String src, String dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = fileSystem.open(getPath(src));
            out = new FileOutputStream(dst);
            IOUtils.copyBytes(in, out, 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deepDownload(String srcFolder, String dstFolder) throws IOException {
        deepDownload(srcFolder, dstFolder, null);
    }

    public void deepDownload(String srcFolder, String dstFolder, FileFilter filter) throws IOException {
        if (srcFolder.contains("\\")) {
            srcFolder = srcFolder.replace("\\", "/");
        }
        traverseHDFSFolder(srcFolder, srcFolder, dstFolder, filter);
    }

    private void traverseLocalFolder(String src, String dst) {
        File file = new File(src);
        File[] files = file.listFiles();
        if (files != null) {
            for (File aFile : files) {
                if (aFile.isDirectory()) {
                    traverseLocalFolder(aFile.getAbsolutePath(), dst + "/" + aFile.getName());
                } else {
                    this.copyFromLocalFile(aFile.getAbsolutePath(), dst + "/" + aFile.getName());
                }
            }
        }
    }

    private void traverseHDFSFolder(String basePath, String src, String dst, FileFilter fileFilter) throws IOException {
        FileStatus[] fileStatuses = this.listStatus(src);
        if (fileStatuses != null) {
            for (FileStatus fileStatus : fileStatuses) {
                String filePath = src + "/" + fileStatus.getPath().getName();
                if (fileStatus.isDirectory()) {
                    traverseHDFSFolder(basePath, filePath, dst, fileFilter);
                } else {
                    if (fileFilter == null || fileFilter.filter(fileStatus.getPath().getName())) {
                        mkdirs(basePath, src, dst);
                        String distDir = dst + File.separator + src.substring(basePath.length() + 1);
                        String distPath = distDir + File.separator + fileStatus.getPath().getName();
                        this.download(filePath, distPath);
                    }
                }
            }
        }
    }

    private void copyFile(Path src, Path dst) {
        FSDataInputStream in = null;
        FSDataOutputStream out = null;
        try {
            in = fileSystem.open(src);
            out = fileSystem.create(dst);
            IOUtils.copyBytes(in, out, 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mkdirs(String basePath, String src, String dst) {
        String[] subDirs = src.substring(basePath.length() + 1).split("/");
        if (subDirs.length > 0) {
            String parentDir = dst;
            for (String dir : subDirs) {
                String subDir = parentDir + File.separator + dir;
                mkdir(subDir);
                parentDir = subDir;
            }
        }
    }

    private void mkdir(String distDir) {
        File file = new File(distDir);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private Path getPath(String path) {
        return new Path(path);
    }

    interface FileFilter {
        boolean filter(String fileName);
    }
}
