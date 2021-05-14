package com.pps.movie.pachong.analy;

import com.alibaba.fastjson.JSONObject;
import com.pps.http.PpsHttpUtilForSpring;
import com.pps.movie.pachong.entity.Html;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author
 * @discription;
 * @time 2021/1/25 10:40
 */
@Service
public class HtmlAnaly {

    @Autowired
    PpsHttpUtilForSpring ppsHttpUtilForSpring;


    public Html toOutHtml(String url, JSONObject hearder){

        Html html=new Html();
        ppsHttpUtilForSpring.createSyncClient()
                .setUrl(url)
                .setHttpHeadersConsumer(h->{
           hearder.keySet().forEach(p->{
               h.set(p,hearder.getString(p));
           });
           html.setRquestHeader(hearder.toJSONString());
        }).get((r,h)-> {


            try {
                html.setStatusCode(r.getStatusCode().value());
            } catch (IOException e) {
                e.printStackTrace();
            }
            html.setResponseHeader(r.getHeaders().toString());
            html.setHtml(h);


        });

        return  html;
    }


}
