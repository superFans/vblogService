package org.sang.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.sang.bean.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by fansongsong
 */
@Component
@Service
@Log4j2
public class Util {


    public static String exeCmd(String commandStr) {

        String result = null;
        try {
            String[] cmd = new String[]{"/bin/sh", "-c",commandStr};
            Process ps = Runtime.getRuntime().exec(cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                //执行结果加上回车
                log.error("cmd====> "+line);
                sb.append(line).append("\n");
            }
            result = sb.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

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
//        QueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(key, value);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//创建复合查询条件对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //如以下条件：查询key中有value的并且enabled必须==true的
//        BoolQueryBuilder must = boolQueryBuilder.should(
//                QueryBuilders.matchQuery(key, value))
//                .must(QueryBuilders.matchQuery("enabled", true));
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("type", "201002");

        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(key, value));
        boolQueryBuilder.must(QueryBuilders.matchQuery("enabled", true));
        sourceBuilder.query(boolQueryBuilder);
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





        public static void main(String[] args) {
            String result = exeCmd("ifconfig");
            System.out.println("获取的结果是"+"\n"+result);

        }

}
