/*
 * Copyright (c) ACCA Corp.
 * All Rights Reserved.
 */
package com.pps.movie.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.component.exception.ServiceException;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.core.common.model.Result;
import com.pps.core.common.util.ValidateUtil;
import com.pps.movie.entity.Relation;
import com.pps.movie.enums.FlagType;
import com.pps.movie.enums.RelationType;
import com.pps.movie.mapper.RelationDao;
import com.pps.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Pu PanSheng, 2021/5/12
 * @version OPRA v1.0
 */
@Service
@Transactional
public class UserServiceImpl  implements UserService  {

    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private RelationDao relationDao;


    @Override
    public Result register(SysUser sysUser) {
        if(ValidateUtil.isEmpty(sysUser.getName())||ValidateUtil.isEmpty(sysUser.getPassword())){
            throw new ServiceException("未填写完整");
        }
         sysUserDao.insert(sysUser);
        return Result.ok("登陆成功");
    }

    @Override
    public Result addRelation(Relation relation) {

        relation.setFlag(FlagType.valid.getValue());
        relation.setType(RelationType.friend.getValue());
        relationDao.insert(relation);

        return Result.ok("成功");
    }



    @Override
    public Result seachUser(JSONObject query) {

        String username = query.getString("username");

        QueryWrapper<SysUser> queryWrapper=new QueryWrapper();
        queryWrapper.like("name",username);
        List<SysUser> sysUsers = sysUserDao.selectList(queryWrapper);
        sysUsers.stream().forEach(sysUser -> {
            sysUser.setPassword(null);
        });

        return Result.ok(sysUsers);
    }

    @Override
    public List<Relation> getRelations(SysUser sysUser) {

        QueryWrapper<Relation> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(Relation::getUserId,sysUser.getId());
        List<Relation> relations = relationDao.selectList(queryWrapper);
        return relations;

    }

    @Override
    public SysUser queryOneByUserName(String username) {

        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getName,username);
        SysUser sysUser = sysUserDao.selectOne(queryWrapper);
        if(sysUser==null){
            throw new ServiceException(username+"用户不存在！");
        }
        return sysUser;
    }


}
