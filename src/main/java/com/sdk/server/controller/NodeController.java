package com.sdk.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdk.server.Utils.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NodeController {

    @Value("${nodeNet}")
    private String ip;


    /**
     * 获取nonce
     * @param pubkeyhash
     * @return
     */
    public JSONObject getNonce(String pubkeyhash){
        String url = "http://"+ip+"/sendNonce";
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
    public JSONObject getTransactionConfirmed(String txHash){
        String url = "http://"+ip+"/transactionConfirmed";
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
    public JSONObject sendTransaction(String traninfo){
        String url = "http://"+ip+"/sendTransaction";
        String param = "traninfo="+traninfo;
        String result = HttpRequestUtil.sendPost(url,param);
        JSONObject jo = JSONObject.parseObject(result);
        return  jo;
    }

}
