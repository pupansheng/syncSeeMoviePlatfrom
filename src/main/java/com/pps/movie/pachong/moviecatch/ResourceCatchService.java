package com.pps.movie.pachong.moviecatch;

import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.SearchVideo;

import java.util.List;

/**
 * @author
 * @discription;
 * @time 2021/1/21 17:05
 */
public interface  ResourceCatchService {


    String getVideoUrl(String url);
    /**
     * 关键词搜索
     * @param word
     * @return
     */
    List<SearchVideo> getSearchResult(String word);
    /**
     * 点击搜搜结果的页面获取播放链接列表
     */
    List<PlayLink> videoResource(String link);





}
