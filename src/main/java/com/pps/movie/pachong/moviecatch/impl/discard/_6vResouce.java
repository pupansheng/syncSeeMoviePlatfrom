package com.pps.movie.pachong.moviecatch.impl.discard;


import com.pps.http.PpsHttpUtilForSpring;
import com.pps.http.util.RegexUtil;
import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.ResourceStategy;
import com.pps.movie.pachong.entity.SearchVideo;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * @author
 * @discription; 6v 电影网爬取资源 已废弃
 * @time 2021/1/21 17:06
 */

public class _6vResouce implements ResourceCatchService, InitializingBean {

    //根据电影资源获取可以直接播放的http资源连接

    //提取主机
    private static Pattern hostPattern = Pattern.compile("^(https://|http://).*(?=/.*/)");
    //提取电影链接
    private static Pattern urlPattern = Pattern.compile("(?<=var main = \").*(?=\";)");

    //解析搜索结果

    //搜索页面
    private String searchUrl = "https://www.ai66.cc/e/search/index.php";
    //搜索结果重定向的页面前缀
    private String searchRedict = "https://www.ai66.cc/e/search/";

    //解析搜索页面结果的正则
    private static Pattern linkPattrn = compile("(?<=<a href=\")[\\s\\S]*?(?=\" class=\"zoom\" rel=\"bookmark\")");
    private static Pattern namePattrn = compile("(?<=class=\"zoom\" rel=\"bookmark\" title=\"<font color='red'>)[\\s\\S]*?(?=</font>)");
    private static Pattern imagePattrn = compile("(?<=<img src=\")[\\s\\S]*?(?=\" alt=\"<font color='red')");
    private static Pattern detailPattrn = compile("(?<=<p>)[\\s\\S]*?(?=</p>)");


    //点击搜索连接获取资源连接

    //获取播放列表

    //获取播放连接
    private static Pattern linkResoucePattrn = compile("(?<=<a title=)[\\s\\S]*?(?=\" target= \"_blank\" class=\"lBtn\" >高清</a>)");
    //获取电影资源连接
    private static Pattern videoResourcePattrn = compile("(?<=<iframe width=\"100%\" height=\"100%\" allowtransparency=\"true\" allowfullscreen=\"true\" frameborder=\"0\" scrolling=\"no\" src=\")[\\s\\S]*?(?=\"></iframe></div>)");


    @Autowired
    private PpsHttpUtilForSpring ppsHttpUtilForSpring;


    @Override
    public String getVideoUrl(String url) {

        String[] result = new String[1];
        ppsHttpUtilForSpring.createSyncClient()
                .setUrl(url)
                .get((r2, html) -> {
                    String host = "";
                    Matcher matcher = hostPattern.matcher(url);
                    if (matcher.find()) {
                        host = matcher.group();
                    }
                    String suffix = "";
                    matcher = urlPattern.matcher(html);
                    if (matcher.find()) {
                        suffix = matcher.group();
                    }
                    String videoUrl = host + suffix;
                    result[0] = videoUrl;
                });

        return result[0];
    }

    @Override
    public List<SearchVideo> getSearchResult(String word) {

        Map param = new HashMap<>();
        List<String> result = new ArrayList<>();
        param.put("keyboard", word);
        param.put("dopost", "search");
        param.put("mid", "1");
        param.put("tempid", "1");
        param.put("tbname", "article");
        param.put("show", "title");
        ppsHttpUtilForSpring.createSyncClient()
                .setUrl(searchUrl)
                .setAuto3xxStrategy(false)
                .setStrict(false)
                .setParam(null, param)
                .setAddW(false)
                .setAutoTransforString(false)
                .setAuto3xxStrategy(false)
                .postXWFrom((r, h) -> {
                    HttpStatus statusCode = null;
                    try {
                        statusCode = r.getStatusCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (statusCode.is3xxRedirection()) {
                        URI location = r.getHeaders().getLocation();
                        String path = location.toString();
                        String newUrl = searchRedict + path;
                        ppsHttpUtilForSpring.createSyncClient().setUrl(newUrl).get((r2, html) -> {
                            Pattern compile = Pattern.compile("<li class=\"post box row fixed-hight\">[\\s\\S]*?(</li>)");
                            Matcher matcher = compile.matcher(html);
                            while (matcher.find()) {
                                String group = matcher.group();
                                result.add(group);
                            }
                        });
                    }
                });

        List<SearchVideo> searchPages = result.stream().map(p -> {
            SearchVideo searchVideo = new SearchVideo();
            String image = RegexUtil.matchOne(imagePattrn, p);
            String name = RegexUtil.matchOne(namePattrn, p);
            String link = RegexUtil.matchOne(linkPattrn, p);
            String detail = RegexUtil.matchOne(detailPattrn, p);
            searchVideo.setDetail(detail);
            searchVideo.setImage(image);
            searchVideo.setLink(link);
            searchVideo.setName(name);
            return searchVideo;
        }).collect(Collectors.toList());


        return searchPages;
    }

    @Override
    public List<PlayLink> videoResource(String link) {
        List<PlayLink> playLinks = new ArrayList<>();
        ppsHttpUtilForSpring.createSyncClient()
                .setUrl(link).setStrict(true)
                .get((r, html) -> {
                    String oneContentByStartAndEnd = RegexUtil.findOneContentByStartAndEnd("<div class=\"widget box row\"><h3>播放地址（无插件 极速播放）</h3>", "</div>", html);
                    List<String> list = RegexUtil.findManyContentByStartAndEnd("href= \"", "\" target= \"_blank\" class=\"lBtn\"", oneContentByStartAndEnd);
                    for (String resource : list) {
                        ppsHttpUtilForSpring.createSyncClient().setUrl(resource).get((vi, htmlV) -> {
                            String s = RegexUtil.matchOne(videoResourcePattrn, htmlV);
                            if (!"".equals(s)) {
                                PlayLink playLink = new PlayLink();
                                String name = RegexUtil.findOneContentByStartAndEnd("pathid1=", "&", resource);
                                try {
                                    int i = Integer.parseInt(name) + 1;
                                    name = String.valueOf(i);
                                } catch (Exception e) {

                                }
                                playLink.setName("第" + name + "部");
                                playLink.setUrl(s);
                                playLinks.add(playLink);
                            }
                        });
                    }
                });

        return playLinks;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ResourceStategy.register("6v影视", this);
    }
}
