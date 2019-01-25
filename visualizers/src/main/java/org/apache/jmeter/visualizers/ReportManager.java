package org.apache.jmeter.visualizers;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-24
 * Time     16:51
 */
public class ReportManager {

    private String reportName;

    private ReportDataSet reportDataSet;

    public ReportManager(String reportName) {
        this.reportName = reportName;
        reportDataSet = new ReportDataSet();
    }

    public void addTestSuite(TestSuiteData testSuite) {
        reportDataSet.add(testSuite);
    }

    public void flush() {
        Map<String, Object> root = new HashMap<>(1);
        root.put("testSuiteList", "KelvinYe");
        FreemarkerUtil.fprint(".ftl", root, this.reportName);
    }

}
