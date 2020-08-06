package com.sdk.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.keystore.wallet.TxUtility;
import com.company.keystore.wallet.WalletUtility;
import com.sdk.server.ApiResult.APIResult;
import com.sdk.server.Utils.Long2BytesUtil;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tdf.common.store.LevelDb;


import java.math.BigDecimal;
import java.util.*;

@RestController
public class TxController {

    @Autowired
    NodeController nodeController;

    @Autowired
    LevelDb levelDb;

    private static final Logger logger = LoggerFactory.getLogger(TxController.class);


    @RequestMapping(value = "/ClientToTransferAccount", method = RequestMethod.POST)
    public JSON ClientToTransferAccount(@RequestParam(value = "fromPubkey", required = true) String fromPubkey, @RequestParam(value = "toPubkeyHash", required = true) String toPubkeyHash,
                                        @RequestParam(value = "amount", required = true) BigDecimal amount, @RequestParam(value = "prikey", required = true) String prikey
    ) {
        try {

            long nownonce = 0;
            String frompubhash = WalletUtility.pubkeyStrToPubkeyHashStr(fromPubkey);
            String address = WalletUtility.pubkeyHashToAddress(frompubhash, 1);
            byte[] fromPubkey_bytes = Hex.decodeHex(fromPubkey.toCharArray());
            if (WalletUtility.verifyAddress(address) != 0) {
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("Address Error");
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            }
            long maxnonce = (levelDb.get(fromPubkey_bytes).isPresent() ? Long2BytesUtil.bytes2Long(levelDb.get(fromPubkey_bytes).get()) : 0);
            if (maxnonce == 0) {
                //rpc获取nonce
                JSONObject getnonoce = nodeController.getNonce(frompubhash);
                int Code = getnonoce.getIntValue("code");
                if (Code == 5000) {
                    APIResult result = new APIResult();
                    result.setStatusCode(5000);
                    result.setMessage("nonce Error");
                    String jsonString = JSON.toJSONString(result);
                    JSONObject json = JSON.parseObject(jsonString);
                    return json;
                }
                long dbnonce = getnonoce.getLongValue("data");
                nownonce = dbnonce;
            } else {
                nownonce = maxnonce;
            }
            JSON data = TxUtility.ClientToTransferAccount(fromPubkey, toPubkeyHash, amount, prikey, nownonce);
            APIResult apiResult = JSON.toJavaObject(data, APIResult.class);
            if (apiResult.getStatusCode() == 5000) {
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("Error:" + apiResult.getMessage());
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            } else {
                ((JSONObject) data).put("statusCode", 2000);
                nownonce++;
                levelDb.put(fromPubkey_bytes, Long2BytesUtil.long2Bytes(nownonce));
                return data;
            }
        } catch (Exception e) {
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage(e.getMessage());
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            e.printStackTrace();
            return json;
        }
    }


    @RequestMapping(value = "/CreateSignToDeployforRuleTransfer", method = RequestMethod.POST)
    public JSON ClientToTransferAccount(@RequestParam(value = "fromPubkey", required = true) String fromPubkey, @RequestParam(value = "txHash", required = true) String txHash,
                                        @RequestParam(value = "prikey", required = true) String prikey, @RequestParam(value = "payload_from", required = true) String payload_from,
                                        @RequestParam(value = "payload_to", required = true) String payload_to, @RequestParam(value = "value", required = true) BigDecimal value
    ) {
        try {

            long nownonce = 0;
            String frompubhash = WalletUtility.pubkeyStrToPubkeyHashStr(fromPubkey);
            String address = WalletUtility.pubkeyHashToAddress(frompubhash, 1);
            byte[] fromPubkey_bytes = Hex.decodeHex(fromPubkey.toCharArray());
            if (WalletUtility.verifyAddress(address) != 0) {
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("Address Error");
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            }
            long maxnonce = (levelDb.get(fromPubkey_bytes).isPresent() ? Long2BytesUtil.bytes2Long(levelDb.get(fromPubkey_bytes).get()) : 0);
            ;
            if (maxnonce == 0) {
                //rpc获取nonce
                JSONObject getnonoce = nodeController.getNonce(frompubhash);
                int Code = getnonoce.getIntValue("code");
                if (Code == 5000) {
                    APIResult result = new APIResult();
                    result.setStatusCode(5000);
                    result.setMessage("nonce Error");
                    String jsonString = JSON.toJSONString(result);
                    JSONObject json = JSON.parseObject(jsonString);
                    return json;
                }
                long dbnonce = getnonoce.getLongValue("data");
                nownonce = dbnonce;
            } else {
                nownonce = maxnonce;
            }
            JSON data = TxUtility.CreateSignToDeployforRuleTransfer(fromPubkey, txHash, prikey, nownonce, payload_from, payload_to, value);
            APIResult apiResult = JSON.toJavaObject(data, APIResult.class);
            if (apiResult.getStatusCode() == 5000) {
                APIResult result = new APIResult();
                result.setStatusCode(5000);
                result.setMessage("Error:" + apiResult.getMessage());
                String jsonString = JSON.toJSONString(result);
                JSONObject json = JSON.parseObject(jsonString);
                return json;
            } else {
                ((JSONObject) data).put("statusCode", 2000);
                nownonce++;
               levelDb.put(fromPubkey_bytes,Long2BytesUtil.long2Bytes(nownonce));
                return data;
            }
        } catch (Exception e) {
            logger.error("代笔转账发生错误：", e);
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage(e.getMessage());
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;

        }
    }


    @RequestMapping(value = "/getNoncePool", method = RequestMethod.GET)
    public Object getNoncePool(@RequestParam(value = "address", required = true) String address) throws DecoderException {
        if (WalletUtility.verifyAddress(address) != 0) {
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Address Error");
            return result;
        }
        byte[] address_bytes = Hex.decodeHex(address.toCharArray());

        return APIResult.newFailResult(2000, "SUCCESS", (levelDb.get(address_bytes).isPresent() ? Long2BytesUtil.bytes2Long(levelDb.get(address_bytes).get()) : 0));
    }

}
