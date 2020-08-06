package com.sdk.server;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tdf.common.store.DBSettings;
import org.tdf.common.store.LevelDb;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public LevelDb levelDb() {
        LevelDb db = new LevelDb(Iq80DBFactory.factory, "local/leveldb", "tmp");
        db.init(DBSettings.DEFAULT);
        db.clear();
        return db;
    }


}
