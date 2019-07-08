package pers.kelvin.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-14
 * Time     14:53
 */
public class FileUtil {
    /**
     * 获取当前系统换行符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 判断文件父目录是否存在，不存在则新建
     *
     * @param file File对象
     */
    public static void createParentDir(File file) {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 写文件
     *
     * @param outputFilePath 文件路径
     * @param content        写入内容
     */
    public static void outputFile(String outputFilePath, String content) {
        File file = new File(outputFilePath);
        outputFile(file, content);
    }

    /**
     * 写文件
     *
     * @param outputFile 文件对象
     * @param content    写入内容
     */
    public static void outputFile(File outputFile, String content) {
        try {
            FileUtil.createParentDir(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile, false), StandardCharsets.UTF_8));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 追加写文件
     *
     * @param outputFilePath 文件路径
     * @param newLine        追加内容
     */
    public static void appendFile(String outputFilePath, String newLine) {
        File file = new File(outputFilePath);
        appendFile(file, newLine);
    }

    /**
     * 追加写文件
     *
     * @param outputFile 文件对象
     * @param newLine    追加内容
     */
    public static void appendFile(File outputFile, String newLine) {
        try {
            FileUtil.createParentDir(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile, true), StandardCharsets.UTF_8));
            writer.write(newLine);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读文件
     *
     * @param outputFilePath 文件路径
     * @return 文件内容
     */
    public static String readFile(String outputFilePath) {
        StringBuffer content = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(outputFilePath)), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
