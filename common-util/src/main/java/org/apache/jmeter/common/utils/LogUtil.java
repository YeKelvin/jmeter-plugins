package org.apache.jmeter.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: KelvinYe
 * Date: 2018-03-28
 * Time: 17:10
 */
public class LogUtil {
    public static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
