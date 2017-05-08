package com.loadmore.mark.testloadmore;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 腾讯云json回调
 * Created by Mark.Han on 2017/4/26.
 */
public abstract class JsonResponse extends JsonHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        if (statusCode != 200) {
            onFailure(statusCode, "发生异常");
            return;
        }

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
        com.alibaba.fastjson.JSONObject result = jsonObject.getJSONObject("multiResult");

        if (result != null) {
            onSuccess(result);
        } else {
//            onFailure(code, msg);
        }


//        com.alibaba.fastjson.JSONObject code = jsonObject.getJSONObject("page");


//        if (code == 0) {
//            onSuccess((com.alibaba.fastjson.JSONObject) jsonObject.get("multiResult"));
//        } else {
//            String msg = jsonObject.getString("returnMsg");
//            onFailure(code, msg);
//        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        if (errorResponse != null) {
            onFailure(statusCode, errorResponse.toString());
        } else {
            onFailure(statusCode, "发生异常");
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        if (errorResponse != null) {
            onFailure(statusCode, errorResponse.toString());
        } else {
            onFailure(statusCode, "发生异常");
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        onFailure(statusCode, responseString);
    }

    /**
     * 成功以后的回调
     *
     * @param object 返回Data
     */
    public abstract void onSuccess(com.alibaba.fastjson.JSONObject object);

    /**
     * 失败的回调
     *
     * @param code 失败状态码
     * @param msg  失败信息
     */
    public abstract void onFailure(int code, String msg);

}
