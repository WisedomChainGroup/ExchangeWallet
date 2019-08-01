package com.sdk.server.pool;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotPool {

    private ConcurrentHashMap<String, NonceState> nopool;

    public NotPool() {
        this.nopool = new ConcurrentHashMap<>();
    }

    public void add(String address,NonceState nonceState){
        long nonce=nonceState.getNonce();
        String key=address+nonce;
        nopool.put(key,nonceState);
    }
}
