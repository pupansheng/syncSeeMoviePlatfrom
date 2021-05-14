
package com.pps.movie.service;

import com.alibaba.fastjson.JSONObject;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.common.model.Result;
import com.pps.movie.entity.Relation;

import java.util.List;

/**
 * @author Pu PanSheng, 2021/5/12
 * @version OPRA v1.0
 */
public interface UserService {

    /**
     * 注册
     * @return
     */
    Result register(SysUser sysUser);

    Result addRelation(Relation relation);

    Result seachUser(JSONObject query);

    List<Relation> getRelations(SysUser sysUser);

    SysUser queryOneByUserName(String username);
}
