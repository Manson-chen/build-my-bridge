package com.buildmybridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buildmybridge.entity.AiApp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI 应用 Mapper 接口
 */
@Mapper
public interface AiAppMapper extends BaseMapper<AiApp> {

    /**
     * 根据应用类型查询应用
     */
    @Select("SELECT * FROM ai_app WHERE app_type = #{appType} ORDER BY created_at DESC")
    List<AiApp> findByAppType(String appType);

    /**
     * 获取所有启用的应用
     */
    @Select("SELECT * FROM ai_app WHERE enabled = true ORDER BY created_at DESC")
    List<AiApp> findAllEnabled();
}
