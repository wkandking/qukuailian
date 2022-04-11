package com.example.qukuailian.util.Cert;

import java.io.*;

public class FileUtil {
    public static void writeFile(String filePath, byte[] data) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            raf.write(data);
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    public static byte[] readFile(String filePath) throws IOException {
        RandomAccessFile raf = null;
        byte[] data;
        try {
            raf = new RandomAccessFile(filePath, "r");
            data = new byte[(int) raf.length()];
            raf.read(data);
            return data;
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    /**
     * 字节转FileInputStream
     *
     * @param bytes
     * @return
     */
    public static File byteToFile(byte[] bytes, String fileName) throws IOException {
        File file = new File(fileName);
        OutputStream output = new FileOutputStream(file);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        bufferedOutput.write(bytes);
        bufferedOutput.close();
        return file;
    }

    /**
     * 通过文件绝对路径 删除单个文件
     * @param filePath
     */
    public static boolean delFile(String filePath) {
        File delFile = new File(filePath);
        if(delFile.isFile() && delFile.exists()) {
            delFile.delete();
            System.out.println("删除文件成功");
            return true;
        }else {
            System.out.println("没有该文件，删除失败");
            return false;
        }
    }

}
