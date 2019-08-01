package com.sdk.server.pool;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NoncePool {

    private ConcurrentHashMap<String, TreeMap<Long, NonceState>> noncepool;

    public NoncePool() {
        this.noncepool = new ConcurrentHashMap<>();
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
    }

    public void updatertey(String address,long nonce){
        if(noncepool.containsKey(address)){
            TreeMap<Long, NonceState> tmaps=noncepool.get(address);
            if(tmaps.containsKey(nonce)){
                NonceState nonceState=tmaps.get(nonce);
                if(nonceState.getDatetime()==0){
                    nonceState.setDatetime(new Date().getTime());
                    nonceState.setDatetime(1);
                    tmaps.put(nonce,nonceState);
                    noncepool.put(address,tmaps);
                }
            }
        }
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

    public static void main(String args[]){
        TreeMap<Integer,String> tree = new TreeMap<Integer,String>();
        tree.put(1, "唐僧");
        tree.put(2, "李白");
        tree.put(5, "白居易");
        tree.put(3, "孙悟空");
        tree.put(2, "李黑");
        tree.put(3, "李彤");
        System.out.println(tree);
        System.out.println("get(1):"+tree.get(1)+" get(2)"+tree.get(2));
        System.out.println(tree.firstKey()+"--->"+tree.lastKey());
    }
}
