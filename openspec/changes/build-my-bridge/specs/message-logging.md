## 新增需求

### 需求：消息日志记录

系统 SHALL 记录飞书与 Dify 之间处理的每条消息。

#### 场景：记录成功消息
- **WHEN** 消息处理成功
- **THEN** 系统创建日志记录，包含：feishu_bot_id、dify_app_id、user_id、user_name、message_type、user_message、bot_message、status=SUCCESS、latency_ms

#### 场景：记录失败消息
- **WHEN** 消息处理失败
- **THEN** 系统创建日志记录，包含：status=FAILED、error_message、latency_ms

### 需求：消息日志查询

系统 SHALL 提供 API 用于查询和检索消息日志。

#### 场景：带筛选条件的日志查询
- **WHEN** 管理员请求 GET /api/logs，携带查询参数（bot_id、dify_app_id、user_id、status、start_date、end_date、page、size）
- **THEN** 系统返回分页的匹配日志列表
- **THEN** 结果按 created_at 降序排序

#### 场景：获取日志统计
- **WHEN** 管理员请求 GET /api/logs/stats
- **THEN** 系统返回按状态统计的总数（SUCCESS、FAILED）
- **THEN** 系统返回平均响应耗时
- **THEN** 系统返回按机器人统计的消息数
- **THEN** 系统返回最近 24 小时每小时的消息数

### 需求：日志保留策略

系统 SHALL 管理日志的保留和清理。

#### 场景：自动清理旧日志
- **WHEN** 日志数量超过 100,000 条
- **THEN** 系统删除 30 天前的日志
- **THEN** 清理任务每天凌晨 2 点执行