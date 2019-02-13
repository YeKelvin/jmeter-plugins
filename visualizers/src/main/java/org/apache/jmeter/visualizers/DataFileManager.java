package org.apache.jmeter.visualizers;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-13
 * Time     17:49
 */
public class DataFileManager {

    public static void outputFile(String outputFilePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(
                            new File(outputFilePath), false), StandardCharsets.UTF_8));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}