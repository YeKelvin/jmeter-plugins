package org.apache.jmeter.config;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.engine.util.NoConfigMerge;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * User: KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class CSVDataSetInScript extends ConfigTestElement implements LoopIterationListener, NoConfigMerge {

    private static final Logger logger = LogUtil.getLogger(CSVDataSetInScript.class);

    public static final String VARIABLE_NAMES = "CSVDataSetInScript.variableNames";

    public static final String DATA = "CSVDataSetInScript.data";

    private String[] varNames = null;

    private int varNamesLength = 0;

    private Iterator lineIter = null;


    @Override
    public void iterationStart(LoopIterationEvent event) {
        Iterator iter = getLineIter();
        if (iter.hasNext()) {
            putVarsFromLine((String) iter.next());
        } else {
            logger.info("CSV Data循环结束，线程组停止循环");
            getThreadContext().getThreadGroup().stop();
        }

    }

    private void putVarsFromLine(String line) {
        String[] lineDatas = line.split(",");
        for (int i = 0; i < getVarNamesLength(); i++) {
            getThreadContext().getVariables().put(getVarNames()[i], lineDatas[i]);
        }
    }

    private Iterator getLineIter() {
        if (lineIter == null) {
            lineIter = readData().iterator();
        }
        return lineIter;
    }

    private String[] getVarNames() {
        if (varNames == null) {
            varNames = getVariableNames().split(",");
        }
        return varNames;
    }

    private int getVarNamesLength() {
        if (varNamesLength == 0) {
            varNamesLength = getVarNames().length;
        }
        return varNamesLength;
    }

    private List<String> readData() {
        return Arrays.asList(getData().split("\n"));
    }

    public String getVariableNames() {
        return getPropertyAsString(CSVDataSetInScript.VARIABLE_NAMES);
    }

    public String getData() {
        return getPropertyAsString(CSVDataSetInScript.DATA);
    }
}
