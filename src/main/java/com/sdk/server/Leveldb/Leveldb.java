package com.sdk.server.Leveldb;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

@Component
public class Leveldb {

    private DB db = null;
    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final String path = System.getProperty("user.dir")+File.separator+"leveldb";

    @PostConstruct
    public void Leveldb() {
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        options.createIfMissing(true);
        File file = new File(path);
        try {
            this.db = factory.open(file, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void addPoolDb(String noncepoolval, String notpool) throws IOException {
        try {

            byte[] keyByte = "noncepool".getBytes(CHARSET);
            byte[] keyByte2 = "notpool".getBytes(CHARSET);
            // 会写入磁盘中
            this.db.put(keyByte, noncepoolval.getBytes(CHARSET));
            this.db.put(keyByte2, notpool.getBytes(CHARSET));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public String readFromSnapshot(Integer type){
        String noncepool = "";
        String notpool = "";
        try {
            // 读取当前快照，重启服务仍能读取，说明快照持久化至磁盘，
            Snapshot snapshot = this.db.getSnapshot();
            // 读取操作
            ReadOptions readOptions = new ReadOptions();
            // 遍历中swap出来的数据，不应该保存在memtable中。
            readOptions.fillCache(false);
            // 默认snapshot为当前
            readOptions.snapshot(snapshot);

            DBIterator it = db.iterator(readOptions);
            while (it.hasNext()) {
                Map.Entry<byte[], byte[]> entry = (Map.Entry<byte[], byte[]>) it
                        .next();
                String key = new String(entry.getKey(), CHARSET);
                String value = new String(entry.getValue(), CHARSET);
                System.out.println("key: " + key + " value: " + value);
                if(key.equals("noncepool")){
                    noncepool = value;
                }else if(key.equals("notpool")){
                    notpool = value;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if(type == 1){
            return noncepool;
        }else{
            return notpool;
        }
    }

}
