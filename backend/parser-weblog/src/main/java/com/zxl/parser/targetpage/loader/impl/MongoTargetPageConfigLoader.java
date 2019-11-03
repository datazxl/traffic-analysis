package com.zxl.parser.targetpage.loader.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.zxl.parser.targetpage.TargetPage;
import com.zxl.parser.targetpage.loader.TargetPageConfigLoader;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MongoTargetPageConfigLoader implements TargetPageConfigLoader {
    private Map<Integer, List<TargetPage>> profileId2TargetPages = new HashMap<>();

    public MongoTargetPageConfigLoader(){
        //1.获取连接
        String mongoAddr = System.getProperty("web.metadata.mongodbAddr", "192.168.2.109");
        MongoClient client = new MongoClient(mongoAddr);
        MongoDatabase database = client.getDatabase("web-analysis");
        database.getCollection("TargetPage").find().forEach(new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                //2.获取targetPage，并放入map中
                TargetPage targetPage = new TargetPage(document.getString("Id"), document.getInteger("ProfileId"),
                        document.getString("Name"), document.getString("Description"), document.getString("MatchPattern"),
                        document.getString("MatchType"), document.getBoolean("MatchWithoutQueryString"), !document.getBoolean("IsDisabled"));
                List<TargetPage> list = profileId2TargetPages.getOrDefault(targetPage.getProfileId(), new ArrayList<TargetPage>());
                list.add(targetPage);
                profileId2TargetPages.put(targetPage.getProfileId(), list);
            }
        });
    }

    /**
     * 从mongo中web-analysis数据库的TargetPage Collection中加载所有的目标页面配置
     * @return
     */
    @Override
    public Map<Integer, List<TargetPage>> getTargetPagesConfigs() {
        return profileId2TargetPages;
    }
}