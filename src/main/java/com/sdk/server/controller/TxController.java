package com.sdk.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.keystore.wallet.TxUtility;
import com.company.keystore.wallet.WalletUtility;
import com.sdk.server.ApiResult.APIResult;
import com.sdk.server.pool.NoncePool;
import com.sdk.server.pool.NonceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@RestController
public class TxController {

    @Autowired
    NoncePool noncePool;

    @RequestMapping(value="/ClientToTransferAccount",method = RequestMethod.POST )
    public JSON ClientToTransferAccount(@RequestParam(value = "fromPubkey", required = true) String fromPubkey,@RequestParam(value = "toPubkeyHash", required = true) String toPubkeyHash,
                                        @RequestParam(value = "amount", required = true) BigDecimal amount,@RequestParam(value = "prikey", required = true) String prikey
                                        ) {
        long nownonce=0;
        String frompubhash=WalletUtility.pubkeyStrToPubkeyHashStr(fromPubkey);
        String address=WalletUtility.pubkeyHashToAddress(frompubhash);
        if(WalletUtility.verifyAddress(address)!=0){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Address Error");
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;
        }
        //rpc获取nonce
        JSONObject getnonoce=NodeController.getNonce(frompubhash);
        int Code= (int) getnonoce.get("code");
        if(Code==5000){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Error");
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;
        }
        long dbnonce= (long) getnonoce.get("data");
        dbnonce++;
        long minnonce=noncePool.getMinNonce(address);
        long maxnonce=noncePool.getMaxNonce(address);
        if(minnonce==0){
            nownonce=dbnonce;
        }else{
            if(dbnonce!=minnonce){
                nownonce=dbnonce;
            }else{
                nownonce=maxnonce+1;
            }
        }
        JSON data = TxUtility.ClientToTransferAccount(fromPubkey,toPubkeyHash,amount,prikey,nownonce);
        if (data == null){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Error");
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;
        }else {
            ((JSONObject) data).put("statusCode",2000);
            String texhash= (String) ((JSONObject) data).get("data");
            NonceState nonceState=new NonceState(texhash,nownonce,new Date().getTime());
            noncePool.add(address,nonceState);
            return data;
        }
    }
}
