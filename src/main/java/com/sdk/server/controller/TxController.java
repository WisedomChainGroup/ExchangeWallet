package com.sdk.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.keystore.wallet.TxUtility;
import com.sdk.server.ApiResult.APIResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
@RestController
public class TxController {
    @RequestMapping(value="/ClientToTransferAccount",method = RequestMethod.POST )
    public JSON ClientToTransferAccount(@RequestParam(value = "fromPubkey", required = true) String fromPubkey,@RequestParam(value = "toPubkeyHash", required = true) String toPubkeyHash,
                                        @RequestParam(value = "amount", required = true) BigDecimal amount,@RequestParam(value = "prikey", required = true) String prikey,
                                        @RequestParam(value = "nonce", required = true) Long nonce) {
        JSON data = TxUtility.ClientToTransferAccount(fromPubkey,toPubkeyHash,amount,prikey,nonce);
        if (data == null){
            APIResult result = new APIResult();
            result.setStatusCode(5000);
            result.setMessage("Error");
            String jsonString = JSON.toJSONString(result);
            JSONObject json = JSON.parseObject(jsonString);
            return json;
        }else {
            ((JSONObject) data).put("statusCode",2000);
            return data;
        }
    }
}
