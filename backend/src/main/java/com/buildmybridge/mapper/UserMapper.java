package com.buildmybridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buildmybridge.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据飞书 OpenID 查询用户
     */
    User findByFeishuOpenId(String feishuOpenId);
}
