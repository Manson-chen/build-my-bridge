package com.buildmybridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buildmybridge.entity.MessageLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 消息日志 Mapper 接口
 */
@Mapper
public interface MessageLogMapper extends BaseMapper<MessageLog> {

    /**
     * 查询指定时间之前的日志数量
     */
    @Select("SELECT COUNT(*) FROM message_log WHERE created_at < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    long countOldLogs(int days);

    /**
     * 删除指定时间之前的日志
     */
    @Select("DELETE FROM message_log WHERE created_at < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int deleteOldLogs(int days);
}
