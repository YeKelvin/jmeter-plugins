package org.apache.jmeter.config;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.engine.util.NoConfigMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraversalDataSet extends ConfigTestElement implements LoopIterationListener, NoConfigMerge {

    private static final Logger log = LoggerFactory.getLogger(TraversalDataSet.class);

    public static final String VARIABLE_NAMES = "TraversalDataSet.variableNames";

    public static final String DATA_SET = "TraversalDataSet.dataSet";

    private String[] varNames = null;

    private int varNamesLength = 0;

    private Iterator<String> lineIter = null;


    @Override
    public void iterationStart(LoopIterationEvent event) {
        Iterator<String> iter = getLineIter();
        if (iter.hasNext()) {
            putVarsFromLine(iter.next());
        } else {
            log.info("CSV Data循环结束，线程组停止循环");
            getThreadContext().getThreadGroup().stop();
        }

    }

    private void putVarsFromLine(String line) {
        String[] lineDatas = line.split(",");
        for (int i = 0; i < getVarNamesLength(); i++) {
            getThreadContext().getVariables().put(getVarNames()[i], lineDatas[i]);
        }
    }

    private Iterator<String> getLineIter() {
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
        return getPropertyAsString(TraversalDataSet.VARIABLE_NAMES);
    }

    public String getData() {
        return getPropertyAsString(TraversalDataSet.DATA_SET);
    }
}
