package com.pps.movie.pachong.moviecatch.impl.discard;

import com.alibaba.fastjson.JSON;
import com.pps.http.PpsHttpUtilForSpring;
import com.pps.http.util.RegexUtil;
import com.pps.movie.entity.UploadRecordPo;
import com.pps.movie.im.listener.CustomListener;
import com.pps.movie.mapper.UploadRecordMapper;
import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.ResourceStategy;
import com.pps.movie.pachong.entity.SearchVideo;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;
import com.pps.websocket.server.chat.ChatProtocl;
import com.pps.websocket.server.util.SendMsgUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author @discription; 已废弃
 * @time 2021/1/21 17:06
 */

@Slf4j
public class XinchengResouce implements ResourceCatchService, InitializingBean {

    private String searchUrl = "http://www.hnxyyy.cn/vodsearch/{}-------------/";
    private static String playUrl = "http://www.hnxyyy.cn";

    @Autowired
    private PpsHttpUtilForSpring ppsHttpUtilForSpring;
    @Autowired
    UploadRecordMapper uploadRecordMapper;

    ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(1,1,10, TimeUnit.MINUTES,new ArrayBlockingQueue<>(100));

    @Override
    public String getVideoUrl(String url) {

        String[] split = url.split("#");
        String parse = split[0];
        String urlBase = split[1];
        Map map1 = JSON.parseObject(parse, Map.class);

        Set sets = map1.keySet();
        for (Object host : sets) {

            Map v = (Map) map1.get(host);
            String baseU = (String) v.get("parse");
            if (baseU == null || baseU.equals("")) {
                continue;
            }
            String newUrl = baseU + urlBase;
            String url1 = getUrl(newUrl);
            if (url1 != null && !"".equals(url1) && test(url1)) {
                return url1;
            }
        }

        throw new RuntimeException("该资源未找到播放资源！");
    }

    public boolean test(String url) {
        try {
            ppsHttpUtilForSpring.createSyncClient().setUrl(url).setHttpHeadersConsumer(h -> {
                h.set("Referer", playUrl);
                h.set("user-agent",
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/88.0.4324.96");
            }).get((r, h) -> {

            });
            return true;
        } catch (Exception e) {
            System.out.println(url + ":" + "影片资源不可用！");
        }
        return false;
    }

    public String getUrl(String url) {

        AtomicReference<String> re = new AtomicReference<>();
        try {

            ppsHttpUtilForSpring.createPhantojsClient(false)
                    .setUrl(url)
                    .setHttpHeadersConsumer(h -> {
                        h.set("Referer", playUrl);
                        h.set("Cookie", "UM_distinctid=1787846854c52e-0a74ecc5fd877e-5771031-144000-1787846854d339; history=%5B%7B%22name%22%3A%22%E7%9B%97%E6%A2%A6%E7%89%B9%E6%94%BB%E9%98%9F%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2019-08-02%2F201908021564713257.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fdaomengtegongdui-1-1%2F%22%2C%22part%22%3A%22HD%E9%AB%98%E6%B8%85%22%7D%2C%7B%22name%22%3A%22%E5%9B%9E%E5%A4%8D%E6%9C%AF%E5%A3%AB%E7%9A%84%E9%87%8D%E6%9D%A5%E4%BA%BA%E7%94%9F%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2021-01-14%2F202101141610594535.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fhuifushushidezhonglairensheng-1-1%2F%22%2C%22part%22%3A%22%E7%AC%AC01%E9%9B%86%22%7D%2C%7B%22name%22%3A%22%E4%BD%A0%E5%A5%BD%EF%BC%8C%E6%9D%8E%E7%84%95%E8%8B%B1%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2021-02-14%2F202102141613271201.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fnihaolihuanying-1-1%2F%22%2C%22part%22%3A%22HC%E9%AB%98%E6%B8%85%22%7D%2C%7B%22name%22%3A%22%E7%9B%97%E6%A2%A6%E7%A9%BA%E9%97%B4%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2019-05-23%2F201905231558593845.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fdaomengkongjian-1-1%2F%22%2C%22part%22%3A%22HD%E8%8B%B1%E8%AF%AD%22%7D%5D; CNZZDATA1279689094=1647349218-1616920571-null%7C1617084380; Hm_lvt_59c54c91990f5e3be7496d6a6d9f43e4=1616925591,1617084584");
                        h.set("user-agent",
                                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/88.0.4324.96");
                    }).get((r, html) -> {
                String oneContentByStartAndEnd = RegexUtil
                        .findOneContentByStartAndEnd("var url = '", "'", html);
                re.set(oneContentByStartAndEnd);
            });
        } catch (Exception e) {
            System.out.println(url + "：网址不可用！");
        }
        return re.get();

    }

    @Override
    public List<SearchVideo> getSearchResult(String word) {
        String encode = null;
        try {
            encode = URLEncoder.encode(word, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String searchUrlNew = searchUrl.replace("{}", encode);
        List<SearchVideo> searchVideos = new ArrayList<>();
        ppsHttpUtilForSpring.createPhantojsClient(false)
                .setDebug(true)
                .setUrl(searchUrlNew)
                .get((r, h) -> {
                    log.info(h);
                    log.info("------------------------------------------------------------------------------");
                    String uls = RegexUtil.findOneContentByStartAndEnd(
                            "<ul class=\"myui-vodlist__media clearfix\" id=\"searchList\">",
                            "</ul>", h);
                    List<String> lis = RegexUtil
                            .findManyContentByStartAndEnd("<li class=\"clearfix\">", "</li>", uls);
                    lis.stream().forEach(p -> {
                        SearchVideo searchVideo = new SearchVideo();
                        String name = RegexUtil
                                .findOneContentByStartAndEnd("<a class=\"searchkey\"", "/a>", p);
                        name = RegexUtil.findOneContentByStartAndEnd(">", "<", name);
                        String image = RegexUtil.findOneContentByStartAndEnd("data-original=\"",
                                "\"", p);
                        String link = RegexUtil
                                .findOneContentByStartAndEnd("<div class=\"detail\">", "</div>", p);
                        link = RegexUtil.findOneContentByStartAndEnd("<p class=\"margin-0\">",
                                "</p>", link);
                        link = RegexUtil.findOneContentByStartAndEnd("href=\"", "\"", link);
                        link = playUrl + link;
                        String deatail = RegexUtil
                                .findOneContentByStartAndEnd("<p class=\"hidden-xs\">", "</p>", p);
                        searchVideo.setDetail(deatail);
                        searchVideo.setImage(image);
                        searchVideo.setLink(link);
                        searchVideo.setName(name);
                        searchVideos.add(searchVideo);
                    });
                });

        return searchVideos;
    }

    @Override
    public List<PlayLink> videoResource(String link) {

        Instant now = Instant.now();

        List<PlayLink> playLinks = new ArrayList<>();
        ppsHttpUtilForSpring
                .createPhantojsClient(false)
                .setUrl(link)
                .setHttpHeadersConsumer((h) -> {
                    h.set("Referer", playUrl);
                }).setStrict(true).get((r, html) -> {
            String res = RegexUtil.findOneContentByStartAndEnd(
                    "<ul class=\"myui-content__list playlist clearfix\" id=\"playlist\">", "</ul>",
                    html);
            List<String> list = RegexUtil.findManyContentByStartAndEnd("<li", "</li>", res);
            for (int i = 0; i < list.size(); i++) {
                String t = RegexUtil.findOneContentByStartAndEnd("href=\"", "\"", list.get(i));
                PlayLink playLink = new PlayLink();
                t = playUrl + t;
                playLink.setUrl(t);
                String name = RegexUtil.findOneContentByStartAndEnd("href=\"", "/a", list.get(i));
                name = RegexUtil.findOneContentByStartAndEnd(">", "<", name);
                playLink.setName(name);
                playLinks.add(playLink);
            }
        });

        playLinks.forEach(p -> {

            String url = p.getUrl();
            ppsHttpUtilForSpring.createPhantojsClient(false)
                    .setUrl(url)
                    .setHttpHeadersConsumer(h -> {
                        h.set("Referer", playUrl);
                    }).get((r, html) -> {

                String data = RegexUtil.findOneContentByStartAndEnd("var player_data=", "<", html);
                Map map = JSON.parseObject(data, Map.class);
                String https = (String) map.get("url");
                String scriptUrl = RegexUtil.findOneContentByStartAndEnd(
                        "<div class=\"embed-responsive clearfix\">", "</div>", html);
                scriptUrl = RegexUtil.findOneContentByStartAndEnd("src=\"", "\"", scriptUrl);
                scriptUrl = playUrl + scriptUrl;
                String script = getScript(scriptUrl);
                String jsonParse = RegexUtil.findOneContentByStartAndEnd(
                        "MacPlayerConfig.player_list=", ",MacPlayerConfig.downer_list", script);
                p.setUrl(jsonParse + "#" + https);
            });
//https://www.mycqzx.com:65/20190802/3gKrlVkP/index.m3u8
        });

        Instant now1 = Instant.now();

        Duration between = Duration.between(now, now1);
        //如果花费时间超过了10秒钟 那么启动异步线程 将结果存入
        if(between.toMillis()>10000){

            log.info("开始异步电影资源下载------------：");
            threadPoolExecutor.execute(()->{

                playLinks.forEach(playLink -> {
                    try {
                        String videoUrl = getVideoUrl(playLink.getUrl());
                        UploadRecordPo uploadRecordPo=new UploadRecordPo();
                        uploadRecordPo.setFileName(playLink.getName()+"(异步添加)");
                        uploadRecordPo.setOptTime(new Date());
                        uploadRecordPo.setUserId(999);
                        uploadRecordPo.setId(UUID.randomUUID().toString());
                        uploadRecordPo.setUrl(videoUrl);
                        uploadRecordMapper.insert(uploadRecordPo);

                         CustomListener.LOGIN_USER.forEach((k, user)->{
                             if(user!=null){
                                 ChannelHandlerContext context = user.getContext();
                                 ChatProtocl chatProtocl=new ChatProtocl();
                                 chatProtocl.setData("["+playLink.getName()+"]"+"已经下载完成！请记得查看！");
                                 chatProtocl.setMsgType(3000);
                                 SendMsgUtil.sendMsgToClientForText(context,chatProtocl);
                             }

                        });


                    }catch (Exception e){

                        CustomListener.LOGIN_USER.forEach((k,user)->{
                            if(user!=null){
                                ChannelHandlerContext context = user.getContext();
                                ChatProtocl chatProtocl=new ChatProtocl();
                                chatProtocl.setData("["+playLink.getName()+"]"+"已经下载失败！");
                                chatProtocl.setMsgType(3000);
                                SendMsgUtil.sendMsgToClientForText(context,chatProtocl);
                            }
                        });

                    }
                });





            });


        }

        return playLinks;
    }

    public String getScript(String url) {

        AtomicReference<String> hr = new AtomicReference<>();
        ppsHttpUtilForSpring.createPhantojsClient(false).setUrl(url).setHttpHeadersConsumer(h -> {
            h.set("Referer", playUrl);
            h.set("Cookie", "UM_distinctid=1787846854c52e-0a74ecc5fd877e-5771031-144000-1787846854d339; history=%5B%7B%22name%22%3A%22%E7%9B%97%E6%A2%A6%E7%89%B9%E6%94%BB%E9%98%9F%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2019-08-02%2F201908021564713257.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fdaomengtegongdui-1-1%2F%22%2C%22part%22%3A%22HD%E9%AB%98%E6%B8%85%22%7D%2C%7B%22name%22%3A%22%E5%9B%9E%E5%A4%8D%E6%9C%AF%E5%A3%AB%E7%9A%84%E9%87%8D%E6%9D%A5%E4%BA%BA%E7%94%9F%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2021-01-14%2F202101141610594535.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fhuifushushidezhonglairensheng-1-1%2F%22%2C%22part%22%3A%22%E7%AC%AC01%E9%9B%86%22%7D%2C%7B%22name%22%3A%22%E4%BD%A0%E5%A5%BD%EF%BC%8C%E6%9D%8E%E7%84%95%E8%8B%B1%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2021-02-14%2F202102141613271201.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fnihaolihuanying-1-1%2F%22%2C%22part%22%3A%22HC%E9%AB%98%E6%B8%85%22%7D%2C%7B%22name%22%3A%22%E7%9B%97%E6%A2%A6%E7%A9%BA%E9%97%B4%22%2C%22pic%22%3A%22https%3A%2F%2Fimg.sokoyo-rj.com%2Ftuku%2Fupload%2Fvod%2F2019-05-23%2F201905231558593845.jpg%22%2C%22link%22%3A%22%2Fvodplay%2Fdaomengkongjian-1-1%2F%22%2C%22part%22%3A%22HD%E8%8B%B1%E8%AF%AD%22%7D%5D; CNZZDATA1279689094=1647349218-1616920571-null%7C1617084380; Hm_lvt_59c54c91990f5e3be7496d6a6d9f43e4=1616925591,1617084584");
            h.set("user-agent",
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/88.0.4324.96");
        }).get((r, html) -> {
            hr.set(html);
        });
        return hr.get();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ResourceStategy.register("星辰影院", this);
    }

    public static void main(String args[]) {

        XinchengResouce xinchengResouce = new XinchengResouce();
        List<SearchVideo> da = xinchengResouce.getSearchResult("盗梦");
        da.forEach(s -> {
            System.out.println("----------------------");
            System.out.println(s);
            List<PlayLink> playLinks = xinchengResouce.videoResource(s.getLink());
            System.out.println(playLinks);
            System.out.println("----------------------");
        });

        System.out.println("");

    }
}
