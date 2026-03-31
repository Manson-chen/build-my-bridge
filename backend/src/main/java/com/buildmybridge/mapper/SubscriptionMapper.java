package com.buildmybridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buildmybridge.entity.Subscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 绑定关系 Mapper 接口
 */
@Mapper
public interface SubscriptionMapper extends BaseMapper<Subscription> {

    /**
     * 根据机器人ID查询绑定关系
     */
    @Select("SELECT * FROM subscription WHERE bot_account_id = #{botAccountId} AND enabled = true LIMIT 1")
    Subscription findByBotAccountId(Long botAccountId);

    /**
     * 根据应用ID查询受影响的机器人
     */
    @Select("""
        SELECT DISTINCT ba.* FROM bot_account ba
        INNER JOIN subscription s ON ba.id = s.bot_account_id
        WHERE s.ai_app_id = #{aiAppId} AND s.enabled = true
        """)
    List<Subscription> findByAiAppId(Long aiAppId);

    /**
     * 获取所有启用的绑定关系
     */
    @Select("SELECT * FROM subscription WHERE enabled = true ORDER BY created_at DESC")
    List<Subscription> findAllEnabled();
}
