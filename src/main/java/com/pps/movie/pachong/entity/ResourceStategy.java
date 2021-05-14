package com.pps.movie.pachong.entity;


import com.pps.core.authority.security.component.exception.ServiceException;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author
 * @discription;
 * @time 2021/1/24 12:59
 */
public class ResourceStategy {

    private static ConcurrentHashMap<String, ResourceCatchService> resourcStragegy=new ConcurrentHashMap<>();

     public static List<String> getKeys(){

     return   resourcStragegy.entrySet().stream().map(p->p.getKey()).collect(Collectors.toList());

    }

    public static void register(String key,ResourceCatchService resourceCatchService){

         resourcStragegy.put(key,resourceCatchService);
    }
    public static ResourceCatchService get(String key){

        ResourceCatchService resourceCatchService = resourcStragegy.get(key);
        if(resourceCatchService==null){
            throw new ServiceException("系统暂时未支持该网站爬虫服务");
        }
        return resourceCatchService;
    }


}
