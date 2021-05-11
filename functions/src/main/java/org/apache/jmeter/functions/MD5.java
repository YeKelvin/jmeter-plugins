package org.apache.jmeter.functions;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kelvin.Ye
 * @date 2020-04-21 17:14
 */
public class MD5 extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(MD5.class);

    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("MD5加密");
    }

    private static final String KEY = "__MD5";


    private CompoundVariable plaintext = null;
    private CompoundVariable md5Key = null;
    private CompoundVariable encode = null;
    private CompoundVariable variable = null;


    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return DESC;
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 1, 4);

        Object[] params = parameters.toArray();
        int count = params.length;

        plaintext = (CompoundVariable) params[0];

        if (count > 1) {
            md5Key = (CompoundVariable) params[1];
        }
        if (count > 2) {
            encode = (CompoundVariable) params[2];
        }
        if (count > 3) {
            variable = (CompoundVariable) params[3];
        }
    }

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) {
        String result = "";
        try {
            String data = this.plaintext.execute().trim();
            Charset charset = StandardCharsets.UTF_8;

            if (this.md5Key != null) {
                data += this.md5Key.execute().trim();
            }

            if (this.encode != null) {
                charset = Charset.forName(this.encode.execute().trim());
            }

            result = DigestUtils.md5Hex(data.getBytes(charset));

            if (this.variable != null) {
                String variable = this.variable.execute().trim();
                JMeterContextService.getContext().getVariables().put(variable, result);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }
}