package com.sdk.server.pool;

import com.alibaba.fastjson.JSONObject;
import com.company.keystore.wallet.WalletUtility;
import com.sdk.server.controller.NodeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class PoolTask {

    public NoncePool noncePool;

    @Autowired
    public PoolTask(NoncePool noncePool){
        this.noncePool=noncePool;
    }

    @Scheduled(fixedDelay = 30 * 1000)
    public void task() throws IOException {
        Map<String, TreeMap<Long, NonceState>> noncepool=noncePool.getNoncepool();
        for(Map.Entry<String, TreeMap<Long, NonceState>> entry:noncepool.entrySet()){
            TreeMap<Long, NonceState> treeMap=entry.getValue();
            long firstkey=treeMap.firstKey();
            //rpc获取nonce
            JSONObject getnonoce=NodeController.getNonce(WalletUtility.addressToPubkeyHash(entry.getKey()));
            int Codes= getnonoce.getIntValue("code");
            if(Codes==2000){
                long nownonce= getnonoce.getLongValue("data");
                if(nownonce>=firstkey){
                    noncePool.remove(entry.getKey(),firstkey);
                    continue;
                }
                NonceState nonceState=treeMap.get(firstkey);
                if(nonceState!=null){
                    //判断txhash是否存在
                    JSONObject result=NodeController.getTransactionConfirmed(nonceState.getTranHash());
                    int Code=result.getIntValue("code");
                    if(Code==2000){
                        noncePool.remove(entry.getKey(),firstkey);
                    }
                }else{
                    noncePool.remove(entry.getKey(),firstkey);
                }
            }
        }
    }

//    public static void main(String agrs[]) throws ParseException {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date date1 = simpleDateFormat.parse("2019-08-01 14:22:30");
//        Date date2 = simpleDateFormat.parse("2019-08-01 14:26:29");
//        long mul=(date2.getTime() - date1.getTime()) / (60 * 1000);
//        System.out.println(mul);
//    }
}
