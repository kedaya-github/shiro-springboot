package com.wxl.shiro.base.mapper;

import com.wxl.shiro.base.bo.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2021/10/14
 *@author Weixl
 */
public interface ResourceMapper {
    /**
    *  查询角色下资源信息
    * @param roleIdList 1
    * @return List<Resource>
    */
    List<Resource> queryByRoleList(@Param("roleIdList") List<String> roleIdList);

    /**
    *  查询过滤url
    * @param  1
    * @return List<Resource>
    */
    List<Resource> queryAll();


}