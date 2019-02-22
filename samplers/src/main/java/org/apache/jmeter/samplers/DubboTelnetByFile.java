package org.apache.jmeter.samplers;

import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-22
 * Time     11:47
 */
public class DubboTelnetByFile extends AbstractSampler implements TestBean {

    private static final Logger logger = LogUtil.getLogger(DubboTelnetByFile.class);

    public static final String ADDRESS = "address";
    public static final String INTERFACE_NAME = "interfaceName";
    public static final String PARAMS = "params";
    public static final String EXPECTION = "expection";
    public static final String USE_TEMPLATE = "useTemplate";
    public static final String INTERFACE_SYSTEM = "interfaceSystem";
    public static final String TEMPLATE_NAME = "templateName";
    public static final String TEMPLATE_CONTENT = "templateContent";

    private static String configFilePath = JMeterUtils.getJMeterHome() + File.separator + "config" + File.separator + "config.json";

    public DubboTelnetByFile() {
        super();
    }

    public String getAddress() {
        return this.getPropertyAsString(ADDRESS);
    }

    public void setAddress(String address) {
        this.setProperty(ADDRESS, address);
    }

    public String getInterfaceName() {
        return this.getPropertyAsString(INTERFACE_NAME);
    }

    public void setInterfaceName(String interfaceName) {
        this.setProperty(INTERFACE_NAME, interfaceName);
    }

    public String getParams() {
        return this.getPropertyAsString(PARAMS);
    }

    public void setParams(String params) {
        this.setProperty(PARAMS, params);
    }

    public String getExpection() {
        return this.getPropertyAsString(EXPECTION);
    }

    public void setExpection(String expection) {
        this.setProperty(EXPECTION, expection);
    }

    public String getUseTemplate() {
        return this.getPropertyAsString(USE_TEMPLATE);
    }

    public void setUseTemplate(String useTemplate) {
        this.setProperty(USE_TEMPLATE, useTemplate);
    }

    public String getInterfaceSystem() {
        return this.getPropertyAsString(INTERFACE_SYSTEM);
    }

    public void setTemplateSystem(String templateSystem) {
        this.setProperty(INTERFACE_SYSTEM, templateSystem);
    }

    public String getTemplateName() {
        return this.getPropertyAsString(TEMPLATE_NAME);
    }

    public void setTemplateName(String templateName) {
        this.setProperty(TEMPLATE_NAME, templateName);
    }

    public String getTemplateContent() {
        return this.getPropertyAsString(TEMPLATE_CONTENT);
    }

    public void setTemplateContent(String templateContent) {
        this.setProperty(TEMPLATE_CONTENT, templateContent);
    }


    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setEncodingAndType("UTF-8");
        try {
            logger.info("address" + getAddress());
            logger.info("interfaceName" + getInterfaceName());
            logger.info("params" + getParams());
            logger.info("expection" + getExpection());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        } finally {
            result.sampleEnd();
        }

        return result;
    }
}
