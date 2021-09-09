package com.code.common.classloader;

import java.io.*;

/**
 * @author Pan Jiebin
 * @date 2021-02-25 10:41
 */
public class CustomClassLoader extends ClassLoader {

    private final String rootDir;

    public CustomClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = getClassData(name);
        if (bytes == null) {
            throw new ClassNotFoundException();
        }
        return this.defineClass(name, bytes, 0, bytes.length);
    }

    private byte[] getClassData(String className) {
        byte[] bytes = null;
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            String path = this.classNameToPath(className);
            is = new FileInputStream(path);
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bytes =  bos.toByteArray();
        } catch (IOException e) {

        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    private String classNameToPath(String className) {
        return rootDir + File.separator + className.replace(".", File.separator) + ".class";
    }
}
