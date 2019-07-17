package com.sdk.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.keystore.wallet.TxUtility;
import com.company.keystore.wallet.WalletUtility;
import com.sdk.server.ApiResult.APIResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class WalletController {
    @RequestMapping(value="/fromPassword",method = RequestMethod.GET )
    public JSON fromPassword(@RequestParam(value = "password", required = true) String password) {
        JSON keystore = WalletUtility.fromPassword(password);
        APIResult result = new APIResult();
        if(keystore == null){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(keystore);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/modifyPassword",method = RequestMethod.POST )
    public JSON modifyPassword(@RequestParam(value = "keystoreJson", required = true) String keystoreJson,@RequestParam(value = "password", required = true) String password,@RequestParam(value = "newPassword", required = true) String newPassword) {
        JSON keystore = WalletUtility.modifyPassword(keystoreJson,password,newPassword);
        APIResult result = new APIResult();
        if(keystore == null){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(keystore);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/verifyAddress",method = RequestMethod.GET )
    public JSON verifyAddress(@RequestParam(value = "address", required = true) String address) {
        int code = WalletUtility.verifyAddress(address);
        System.out.println(address);
        System.out.println(code);
        APIResult result = new APIResult();
        if (code == 0){
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
        }else if(code == -1){
            result.setStatusCode(6000);
            result.setMessage("Error");
        }else if(code == -2){
            result.setStatusCode(7000);
            result.setMessage("Error");
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }


    @RequestMapping(value="/pubkeyHashToAddress",method = RequestMethod.GET )
    public JSON pubkeyHashToAddress(@RequestParam(value = "pubkeyHash", required = true) String pubkeyHash) {
        String address = WalletUtility.pubkeyHashToAddress(pubkeyHash);
        APIResult result = new APIResult();
        if(address == null || address == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(address);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    /**
     * 调用此方法之前请先校验地址合法性！(调用verifyAddress方法)
     * @param address
     * @return
     */
    @RequestMapping(value="/addressToPubkeyHash",method = RequestMethod.GET )
    public JSON addressToPubkeyHash(@RequestParam(value = "address", required = true) String address) {
        String pubkeyHash = WalletUtility.addressToPubkeyHash(address);
        APIResult result = new APIResult();
        if(pubkeyHash == null || pubkeyHash == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(pubkeyHash);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/keystoreToAddress",method = RequestMethod.POST )
    public JSON keystoreToAddress(@RequestParam(value = "keystoreJson", required = true) String keystoreJson){
        String address = WalletUtility.keystoreToAddress(keystoreJson,null);
        APIResult result = new APIResult();
        if(address == null || address == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(address);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/keystoreToPubkey",method = RequestMethod.POST )
    public JSON keystoreToPubkey(@RequestParam(value = "keystoreJson", required = true) String keystoreJson, @RequestParam(value = "password", required = true) String password) {
        String pubkey = WalletUtility.keystoreToPubkey(keystoreJson,password);
        APIResult result = new APIResult();
        if(pubkey == null || pubkey == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(pubkey);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/keystoreToPubkeyHash",method = RequestMethod.POST )
    public JSONObject keystoreToPubkeyHash(@RequestParam(value = "keystoreJson", required = true) String keystoreJson, @RequestParam(value = "password", required = true) String password) {
        String pubkeyHash = WalletUtility.keystoreToPubkeyHash(keystoreJson,password);
        APIResult result = new APIResult();
        if(pubkeyHash == null || pubkeyHash == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(pubkeyHash);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/obtainPrikey",method = RequestMethod.POST )
    public JSON obtainPrikey(@RequestParam(value = "keystoreJson", required = true) String keystoreJson,@RequestParam(value = "password", required = true) String password) {
        String privateKey = WalletUtility.obtainPrikey(keystoreJson,password);
        APIResult result = new APIResult();
        if(privateKey == null || privateKey == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(privateKey);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;

    }

    @RequestMapping(value="/prikeyToPubkey",method = RequestMethod.POST )
    public JSON prikeyToPubkey(@RequestParam(value = "prikey", required = true) String prikey) {
        String privateKey = WalletUtility.prikeyToPubkey(prikey);
        APIResult result = new APIResult();
        if(privateKey == null || privateKey == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(privateKey);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }

    @RequestMapping(value="/pubkeyStrToPubkeyHashStr",method = RequestMethod.POST )
    public JSON pubkeyToPubkeyHash(@RequestParam(value = "pubkey", required = true) String pubkey) {
        String pubkeyHash = WalletUtility.pubkeyStrToPubkeyHashStr(pubkey);
        APIResult result = new APIResult();
        if(pubkeyHash == null || pubkeyHash == ""){
            result.setStatusCode(5000);
            result.setMessage("Error");
        }else{
            result.setStatusCode(2000);
            result.setMessage("SUCCESS");
            result.setData(pubkeyHash);
        }
        String jsonString = JSON.toJSONString(result);
        JSONObject json = JSON.parseObject(jsonString);
        return json;
    }
}
