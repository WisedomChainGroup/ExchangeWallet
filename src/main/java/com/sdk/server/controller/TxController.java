package com.sdk.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.keystore.wallet.TxUtility;
import com.company.keystore.wallet.WalletUtility;
import com.sdk.server.ApiResult.APIResult;
import com.sdk.server.pool.NoncePool;
import com.sdk.server.pool.NonceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

@RestController
public class TxController {

    @Autowired
    NoncePool noncePool;

    @Autowired
    NodeController nodeController;

    private static final Logger logger = LoggerFactory.getLogger(TxController.class);


    @RequestMapping(value="/ClientToTransferAccount",method = RequestMethod.POST )
    public JSON ClientToTransferAccount(@RequestParam(value = "fromPubkey", required = true) String fromPubkey,@RequestParam(value = "toPubkeyHash", required = true) String toPubkeyHash,
                                        @RequestParam(value = "amount", required = true) BigDecimal amount,@RequestParam(value = "prikey", required = true) String prikey
                                        ){
        try{

            long nownonce=0;
        String frompubhash= WalletUtility.pubkeyStrToPubkeyHashStr(fromPubkey);
        String address=WalletUtility.pubkeyHashToAddress(frompubhash,1);
        if(WalletUtility.verifyAddress(address)!=0){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Address Error");
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;
        }
        long maxnonce=noncePool.getMaxNonce(address);
        if(maxnonce==0){
            //rpc获取nonce
            JSONObject getnonoce=nodeController.getNonce(frompubhash);
            int Code= getnonoce.getIntValue("code");
            if(Code==5000){
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("nonce Error");
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            }
            long dbnonce= getnonoce.getLongValue("data");
            nownonce=dbnonce;
        }else{
            nownonce=maxnonce;
        }
        JSON data = TxUtility.ClientToTransferAccount(fromPubkey,toPubkeyHash,amount,prikey,nownonce);
        APIResult apiResult  = JSON.toJavaObject(data,APIResult.class);
        if (apiResult.getStatusCode() == 5000){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Error:"+apiResult.getMessage());
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;
        }else {
            ((JSONObject) data).put("statusCode",2000);
            String texhash= (String) ((JSONObject) data).get("data");
            nownonce++;
            NonceState nonceState=new NonceState(texhash,nownonce,new Date().getTime());
            noncePool.add(address,nonceState);
            return data;
        }
        }catch (Exception e){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage(e.getMessage());
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            e.printStackTrace();
            return json;
        }
    }


    @RequestMapping(value="/CreateSignToDeployforRuleTransfer",method = RequestMethod.POST )
    public JSON ClientToTransferAccount(@RequestParam(value = "fromPubkey", required = true) String fromPubkey,@RequestParam(value = "txHash", required = true) String txHash,
                                        @RequestParam(value = "prikey", required = true) String prikey,@RequestParam(value = "payload_from", required = true) String payload_from,
                                        @RequestParam(value = "payload_to", required = true) String payload_to,@RequestParam(value = "value", required = true) BigDecimal value
    ){
        try{

            long nownonce=0;
            String frompubhash= WalletUtility.pubkeyStrToPubkeyHashStr(fromPubkey);
            String address=WalletUtility.pubkeyHashToAddress(frompubhash,1);
            if(WalletUtility.verifyAddress(address)!=0){
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("Address Error");
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            }
            long maxnonce=noncePool.getMaxNonce(address);
            if(maxnonce==0){
                //rpc获取nonce
                JSONObject getnonoce=nodeController.getNonce(frompubhash);
                int Code= getnonoce.getIntValue("code");
                if(Code==5000){
                    APIResult result = new APIResult();
                    result.setStatusCode(5000);
                    result.setMessage("nonce Error");
                    String jsonString = JSON.toJSONString(result);
                    JSONObject json = JSON.parseObject(jsonString);
                    return json;
                }
                long dbnonce= getnonoce.getLongValue("data");
                nownonce=dbnonce;
            }else{
                nownonce=maxnonce;
            }
            JSON data = TxUtility.CreateSignToDeployforRuleTransfer(fromPubkey,txHash,prikey,nownonce,payload_from,payload_to,value);
            APIResult apiResult  = JSON.toJavaObject(data,APIResult.class);
            if (apiResult.getStatusCode() == 5000){
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("Error:"+apiResult.getMessage());
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            }else {
                ((JSONObject) data).put("statusCode",2000);
                String texhash= (String) ((JSONObject) data).get("data");
                nownonce++;
                NonceState nonceState=new NonceState(texhash,nownonce,new Date().getTime());
                noncePool.add(address,nonceState);
                return data;
            }
        }catch (Exception e){
            logger.error("代笔转账发生错误：",e);
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage(e.getMessage());
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;

        }
    }



    @RequestMapping(value="/getNoncePool",method = RequestMethod.GET )
    public Object getNoncePool(@RequestParam(value = "address", required = true) String address){
        if(WalletUtility.verifyAddress(address)!=0){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Address Error");
            return result;
        }
        TreeMap<Long, NonceState> tree=noncePool.getTreemap(address);
        return APIResult.newFailResult(2000,"SUCCESS",tree);
    }

}
