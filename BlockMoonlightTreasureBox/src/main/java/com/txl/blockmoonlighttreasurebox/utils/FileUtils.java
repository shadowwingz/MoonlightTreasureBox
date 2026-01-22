package com.txl.blockmoonlighttreasurebox.utils;

import java.io.File;

/**
 * 文件工具类
 * 提供文件和流操作的通用方法
 */
public class FileUtils {
    /**
     * 递归删除文件或目录
     * @param file 要删除的文件或目录
     * @return 是否删除成功
     */
    public static boolean deleteFile(File file){
        if(file != null && file.exists()){
            if(file.isDirectory()){
                File[] files = file.listFiles();
                if(files != null){
                    for (File value : files) {
                        return deleteFile(value);
                    }
                }
            }
            return file.delete();
        }
        return false;
    }

    /**
     * 安全关闭流
     * @param closeable 可关闭的对象
     */
    public static void closeStream(AutoCloseable closeable){
        if(closeable == null){
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
