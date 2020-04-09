package org.apache.jmeter.samplers;

import org.apache.jmeter.common.utils.json.JsonUtil;

/**
 * Description: 响应相关
 *
 * @author: KelvinYe
 * Date: 2018-05-11
 * Time: 17:13
 */
public class Response {
    private Object response;

    public Response(Object responseObj) {
        this.response = responseObj;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(response);
    }
}
