package com.pps.movie.pachong.test;

import com.pps.http.PpsHttpUtil;
import com.pps.http.myrequest.phantom.PhantomClientHttpRequest;
import com.pps.http.myrequest.phantom.PhantomClientHttpResponse;
import com.pps.http.util.RegexUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @discription;
 * @time 2021/1/21 10:06
 */
public class SearchPage  {



    public static void main(String[] args) {

         //PpsHttpUtil.setPhamtomJsPath("C:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        String word="灵笼";
        Map map=new HashMap<>();
        map.put("wd",word);

        PpsHttpUtil.createSyncClient().setEquimentMode(true).setUrl("https://www.jisuysw.com/search/").setParam(null,map).postXWFrom((r,s)->{

            List<String> divs = RegexUtil.findManyContentByStartAndEnd("<div class=\"col-md-3 col-sm-4 col-xs-3 news-box-txt-l clearfix\">", "</div>", s);
            divs.forEach(div->{

                System.out.println(div);

            });



        });





    }
}
