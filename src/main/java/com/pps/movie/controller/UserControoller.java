package com.pps.movie.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.core.common.model.Result;
import com.pps.movie.entity.Relation;
import com.pps.movie.entity.UserInfoPo;
import com.pps.movie.mapper.UserInfoMapper;
import com.pps.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author
 * @discription;
 * @time 2021/1/5 11:49
 */
@RestController
@RequestMapping("/jwt/user/")
public class UserControoller {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserService userService;

    @PostMapping("update")
    public Result update(@RequestBody UserInfoPo userInfoPo){
        Integer userId = userInfoPo.getUserId();
        QueryWrapper<UserInfoPo> query=new QueryWrapper<>();
        query.lambda().eq(UserInfoPo::getUserId,userId);
        List<UserInfoPo> userInfoPos = userInfoMapper.selectList(query);
        if(userInfoPos.size()>0){
            userInfoPo.setId(userInfoPos.get(0).getId());
            userInfoMapper.updateById(userInfoPo);
        }else {
            userInfoMapper.insert(userInfoPo);
        }
        return    Result.ok(userInfoPo);
    }


    @PostMapping("/register")
    public Result register(@RequestBody SysUser sysUser){

        return userService.register(sysUser);

    }


    /**
     * 添加好友
     * @param relation
     * @return
     */
    @PostMapping("/add/friend")
    public Result addRelation(@RequestBody Relation relation){

        return  userService.addRelation(relation);

    }


    /**
     * 寻找好友
     * @param query
     * @return
     */
    @PostMapping("/search")
    public Result searchUser(@RequestBody JSONObject query){

        return  userService.seachUser(query);

    }



}
