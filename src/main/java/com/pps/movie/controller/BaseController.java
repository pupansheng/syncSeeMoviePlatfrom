package com.pps.movie.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author
 * @discription;
 * @time 2020/12/29 17:40
 */
public class BaseController {


    public String getLoginUerName(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= (String) authentication.getPrincipal();
        return username;
    }

    public String getSuffix(String name){
        return name.substring(name.lastIndexOf(".")+1);
    }

}
