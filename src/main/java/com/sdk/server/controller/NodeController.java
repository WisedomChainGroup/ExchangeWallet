package com.sdk.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdk.server.Utils.HttpRequestUtil;

public class NodeController {
    //    public static final String ip = "http://120.76.101.153:19585/";
    public static final String ip = "http://192.168.0.101:19585/";


    /**
     * 获取nonce
     * @param pubkeyhash
     * @return
     */
    public static JSONObject getNonce(String pubkeyhash){
        String url = ip+"sendNonce";
        String param = "pubkeyhash="+pubkeyhash;
        String result = HttpRequestUtil.sendPost(url,param);
        JSONObject jo = JSONObject.parseObject(result);
        return  jo;
    }

    /**
     * 通过事务哈希获取区块确认状态
     * @param txHash
     * @return
     */
    public static JSONObject getTransactionConfirmed(String txHash){
        String url = ip+"transactionConfirmed";
        String param = "txHash="+txHash;
        String result = HttpRequestUtil.sendGet(url,param);
        JSONObject jo = JSONObject.parseObject(result);
        return jo;
    }

    /**
     * 广播事务
     * @param traninfo
     * @return
     */
    public static JSONObject sendTransaction(String traninfo){
        String url = ip+"sendTransaction";
        String param = "traninfo="+traninfo;
        String result = HttpRequestUtil.sendPost(url,param);
        JSONObject jo = JSONObject.parseObject(result);
        return  jo;
    }

}
