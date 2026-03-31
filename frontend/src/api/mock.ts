import { BotAccount, AiApp, MessageLogItem } from '../types'

const mockBots: BotAccount[] = [
  {
    id: 'bot_001',
    botName: '客服机器人',
    botType: 'FEISHU',
    botConfig: { app_id: 'cli_xxx', webhook_url: 'https://xxx' },
    connectionStatus: 'CONNECTED',
    enabled: true,
    createdAt: '2024-03-20',
  },
  {
    id: 'bot_002',
    botName: '技术支持',
    botType: 'FEISHU',
    botConfig: { app_id: 'cli_yyy', webhook_url: 'https://yyy' },
    connectionStatus: 'CONNECTING',
    enabled: true,
    createdAt: '2024-03-21',
  },
  {
    id: 'bot_003',
    botName: '知识库助手',
    botType: 'FEISHU',
    botConfig: { app_id: 'cli_zzz', webhook_url: 'https://zzz' },
    connectionStatus: 'CONNECTED',
    enabled: true,
    createdAt: '2024-03-19',
  },
]

const mockApps: AiApp[] = [
  {
    id: 'app_001',
    appName: 'Dify-销售问答',
    appType: 'DIFY',
    appConfig: {
      api_url: 'https://dify.example.com',
      api_key: 'xxx',
      app_id: 'sales_qa',
      type: 'chatflow',
    },
    enabled: true,
    createdAt: '2024-03-15',
  },
  {
    id: 'app_002',
    appName: 'Dify-技术支持',
    appType: 'DIFY',
    appConfig: {
      api_url: 'https://dify.example.com',
      api_key: 'yyy',
      app_id: 'tech_support',
      type: 'workflow',
    },
    enabled: true,
    createdAt: '2024-03-16',
  },
  {
    id: 'app_003',
    appName: 'Dify-知识库查询',
    appType: 'DIFY',
    appConfig: {
      api_url: 'https://dify.example.com',
      api_key: 'zzz',
      app_id: 'kb_search',
      type: 'chatflow',
    },
    enabled: false,
    createdAt: '2024-03-17',
  },
]

const mockLogs: MessageLogItem[] = [
  {
    id: 'log_001',
    botAccountId: 'bot_001',
    aiAppId: 'app_001',
    messageContent: '请问你们的产品有哪些功能？',
    responseContent: '我们的产品主要有三个模块：机器人管理、应用配置、消息日志统计...',
    status: 'success',
    createdAt: '2024-03-30T10:15:00',
  },
  {
    id: 'log_002',
    botAccountId: 'bot_002',
    aiAppId: 'app_002',
    messageContent: '系统集成有什么要求吗？',
    responseContent: '需要 Spring Boot 后端和 React 前端...',
    status: 'success',
    createdAt: '2024-03-30T10:20:00',
  },
  {
    id: 'log_003',
    botAccountId: 'bot_003',
    aiAppId: 'app_003',
    messageContent: '如何开始使用？',
    responseContent: '',
    status: 'error',
    errorMessage: '应用已禁用',
    createdAt: '2024-03-30T10:25:00',
  },
  {
    id: 'log_004',
    botAccountId: 'bot_001',
    aiAppId: 'app_001',
    messageContent: '价格是多少？',
    responseContent: '我们提供开源免费版本...',
    status: 'success',
    createdAt: '2024-03-30T11:00:00',
  },
  {
    id: 'log_005',
    botAccountId: 'bot_002',
    aiAppId: 'app_002',
    messageContent: '支持哪些机器人平台？',
    responseContent: '目前支持飞书，后续会支持钉钉、企业微信...',
    status: 'success',
    createdAt: '2024-03-30T11:30:00',
  },
]

export const mockApi = {
  getBots: () => Promise.resolve({ data: { data: mockBots } }),
  getApps: () => Promise.resolve({ data: { data: mockApps } }),
  getLogs: (params: any) => {
    const pageNo = params.pageNo || 1
    const pageSize = params.pageSize || 10
    const start = (pageNo - 1) * pageSize
    const items = mockLogs.slice(start, start + pageSize)
    return Promise.resolve({
      data: { data: { items, total: mockLogs.length, pageNo, pageSize } },
    })
  },
  getStats: () =>
    Promise.resolve({
      data: { data: { botsCount: 3, appsCount: 3, todayMessages: 15, availability: 98.5 } },
    }),
}
