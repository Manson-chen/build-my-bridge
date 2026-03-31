package com.buildmybridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buildmybridge.entity.BotAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 机器人账户 Mapper 接口
 */
@Mapper
public interface BotAccountMapper extends BaseMapper<BotAccount> {

    /**
     * 根据连接状态查询机器人
     */
    @Select("SELECT * FROM bot_account WHERE connection_status = #{status}")
    List<BotAccount> findByConnectionStatus(String status);

    /**
     * 获取所有启用的机器人
     */
    @Select("SELECT * FROM bot_account WHERE enabled = true ORDER BY created_at DESC")
    List<BotAccount> findAllEnabled();
}
