package com.sdk.server.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class PoolTask {

    @Autowired
    NoncePool noncePool;

    @Autowired
    NotPool notPool;

    @Scheduled(fixedDelay = 30 * 1000)
    public void task(){
        Map<String, TreeMap<Long, NonceState>> noncepool=noncePool.getNoncepool();
        for(Map.Entry<String, TreeMap<Long, NonceState>> entry:noncepool.entrySet()){
            TreeMap<Long, NonceState> treeMap=entry.getValue();
            long firstkey=treeMap.firstKey();
            NonceState nonceState=treeMap.get(firstkey);
            if(nonceState!=null){
                //判断txhash是否存在

                //超时
                long mul=(new Date().getTime() - nonceState.getDatetime()) / (60 * 1000);
                if(mul>3){
                    if(nonceState.getRetey()==0){
                        //重新发送


                    }else{
                        noncePool.remove(entry.getKey(),firstkey);
                        notPool.add(entry.getKey(),nonceState);
                    }
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
