package org.sang.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.sang.bean.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by fansongsong
 */
@Component
@Service
public class Util {

    public static User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    private static RestHighLevelClient client;
    public Util() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("47.106.128.210", 9200, "http")));
        this.client = client;
    }
    public void shutdown(){
        if(client!=null){
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据指定的内容，查询所有Doc。
     * @return
     */
    public static List<Map<String,Object>> searchArticle(String index,String key, String value){
        SearchHits searchHits = search(index,key,value);
        List<Map<String,Object>> list = new ArrayList<>();
        for(SearchHit hit:searchHits.getHits()){
            System.out.println( hit.getSourceAsString());
            Map<String,Object> stringObjectMap = hit.getSourceAsMap();
            stringObjectMap.put("name", stringObjectMap.get("nickname"));
            list.add(stringObjectMap);
        }
        return list;
    }

    public static SearchHits search(String index, String key, String value){
        QueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(key, value);
//        matchQueryBuilder.
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.termQuery("content", content));
        sourceBuilder.query(matchQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(100);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse;
        List<Map<String,Object>> list = new ArrayList<>();
        SearchHits searchHits = null;
        try {
            searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
            searchHits =  searchResponse.getHits();
            for(SearchHit hit:searchHits.getHits()){
                System.out.println( hit.getSourceAsString());
                list.add(hit.getSourceAsMap());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchHits;
    }

}
