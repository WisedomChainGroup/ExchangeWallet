package com.sdk.server.pool;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NoncePool {

    private ConcurrentHashMap<String, TreeMap<Long, NonceState>> noncepool;

    public NoncePool() {
        try{
            //序列化获取
            String dbdata=null;
            if(dbdata!=null || !dbdata.equals("")){
                noncepool= (ConcurrentHashMap<String, TreeMap<Long, NonceState>>) JSON.parse(dbdata);
            }else{
                this.noncepool = new ConcurrentHashMap<>();
            }
        }catch (Exception e){
            this.noncepool = new ConcurrentHashMap<>();
        }
    }

    public ConcurrentHashMap<String, TreeMap<Long, NonceState>> getNoncepool() {
        return noncepool;
    }

    public void add(String address,NonceState nonceState){
        if(noncepool.containsKey(address)){
            TreeMap<Long, NonceState> tmaps=noncepool.get(address);
            long nownonce=nonceState.getNonce();
            if(!tmaps.containsKey(nownonce)){
                tmaps.put(nonceState.getNonce(),nonceState);
            }
        }else{
            TreeMap<Long, NonceState> treemap=new TreeMap<>();
            treemap.put(nonceState.getNonce(),nonceState);
            noncepool.put(address,treemap);
        }
        String json = JSON.toJSONString(noncepool,true);

    }

    public void remove(String address,long nonce){
        if(noncepool.containsKey(address)){
            TreeMap<Long, NonceState> tmaps=noncepool.get(address);
            if(tmaps.containsKey(nonce)){
                tmaps.remove(nonce);
            }
            if(tmaps.size()==0){
                noncepool.remove(address);
            }
        }
        String json = JSON.toJSONString(noncepool,true);
    }

    public long getMinNonce(String address){
        if(noncepool.containsKey(address)){
            TreeMap<Long, NonceState> tmaps=noncepool.get(address);
            return tmaps.firstKey();
        }
        return 0;
    }

    public long getMaxNonce(String address){
        if(noncepool.containsKey(address)){
            TreeMap<Long, NonceState> tmaps=noncepool.get(address);
            return tmaps.lastKey();
        }
        return 0;
    }

    public TreeMap<Long, NonceState> getTreemap(String address){
        if(noncepool.containsKey(address)){
            return noncepool.get(address);
        }
        return new TreeMap<>();
    }

//    public static void main(String args[]){
//        TreeMap<Integer,String> tree = new TreeMap<Integer,String>();
//        tree.put(1, "唐僧");
//        tree.put(2, "李白");
//        tree.put(5, "白居易");
//        tree.put(3, "孙悟空");
//        tree.put(2, "李黑");
//        tree.put(3, "李彤");
//        System.out.println(tree);
//        System.out.println("get(1):"+tree.get(1)+" get(2)"+tree.get(2));
//        System.out.println(tree.firstKey()+"--->"+tree.lastKey());
//    }
}
