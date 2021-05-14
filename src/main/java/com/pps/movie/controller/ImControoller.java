package com.pps.movie.controller;


import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.core.common.model.Result;
import com.pps.movie.entity.Relation;
import com.pps.movie.im.entity.ImUser;
import com.pps.movie.im.listener.CustomListener;
import com.pps.movie.mapper.RelationDao;
import com.pps.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @discription;
 * @time 2021/1/5 11:49
 */
@RestController
@RequestMapping("/jwt/im")
public class ImControoller  {

    @Autowired
    SysUserDao sysUserDao;
    @Autowired
    RelationDao relationDao;
    @Autowired
    UserService userService;


    /**
     * 获得在线的好友
     * @return
     */
    @PostMapping("/get/online")
    public Result getOnline(){
     List<ImUser> array=new ArrayList<>();
     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     String username= (String) authentication.getPrincipal();
     SysUser sysUser = userService.queryOneByUserName(username);
     List<Relation> relations = userService.getRelations(sysUser);
     List<Integer> relationArray = relations.stream().map(Relation::getRelationId).collect(Collectors.toList());
     CustomListener.LOGIN_USER.forEach((k, v)->{
         if(!k.equals(username)&&relationArray.contains(v.getId())){
             ImUser imUser=new ImUser();
             imUser.setUserInfoPo(v.getUserInfoPo());
             imUser.setId(v.getId());
             imUser.setName(v.getName());
             array.add(imUser);
         }
     });
     return    Result.ok(array);
    }

    /**
     * 获得所有好友
     * @return
     */
    @PostMapping("/get/all")
    public Result getOnlineAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= (String) authentication.getPrincipal();
        SysUser sysUser = userService.queryOneByUserName(username);
        List<Relation> relations = userService.getRelations(sysUser);
        List<SysUser> sysUsers=relations.stream().map(relation -> {
            Integer relationId = relation.getRelationId();
            return sysUserDao.selectById(relationId);
        }).collect(Collectors.toList());
        return    Result.ok(sysUsers);
    }

}
