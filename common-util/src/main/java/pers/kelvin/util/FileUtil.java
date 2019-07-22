package pers.kelvin.util;

import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.log.LogUtil;

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

    private static final Logger logger = LogUtil.getLogger(FileUtil.class);

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
            if (!file.getParentFile().mkdirs()) {
                throw new ServiceException("创建文件 " + file.getParentFile().getAbsolutePath() + "的父目录失败");
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            if (!file.delete()) {
                throw new ServiceException("删除文件 " + filePath + "失败");
            }
        } else {
            throw new FileNotFoundException("删除文件失败，文件 " + filePath + "不存在");
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
            logger.error(ExceptionUtil.getStackTrace(e));
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
            logger.error(ExceptionUtil.getStackTrace(e));
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
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(new File(outputFilePath)), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return content.toString();
    }

    public static String readEnvFile(String filePath) {
        File file = new File(filePath);
        return readEnvFile(file);
    }

    public static String readEnvFile(File file) {
        StringBuffer content = new StringBuffer();
        int charCode = -1;
        // 上一个字符
        char previous = '\u0000';
        // 是否在双引号内
        boolean isInsideQuotes = false;
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
        ) {
            while ((charCode = reader.read()) != -1) {
                // 当前字符
                char currentChar = (char) charCode;
                // 遇到引号时，标记当前正在双引号中
                if (currentChar == '\"') {
                    // 非转义符引号才标记
                    if (previous != '\\') {
                        isInsideQuotes = !isInsideQuotes;
                    }
                }
                // 在非双引号内且当前字符为 / 斜杠时
                if (!isInsideQuotes && currentChar == '/') {
                    // 遇到 //注释符号时丢弃该行的后续内容
                    if (previous == '/') {
                        reader.readLine();
                        previous = '\u0000';
                    } else {
                        previous = currentChar;
                    }
                } else {
                    if (!isInsideQuotes && previous == '/') {
                        content.append(previous);
                    }
                    content.append(currentChar);
                    previous = currentChar;
                }
            }
        } catch (IOException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return StringUtil.removeSpacesAndLineBreaks(content.toString());
    }
}
