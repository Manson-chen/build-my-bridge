export interface User {
  id: string
  username: string
  feishuOpenId: string
}

export interface BotAccount {
  id: string
  botName: string
  botType: string
  botConfig: Record<string, any>
  connectionStatus: 'INIT' | 'CONNECTING' | 'CONNECTED' | 'FAILED' | 'DISCONNECTED'
  enabled: boolean
  createdAt: string
}

export interface AiApp {
  id: string
  appName: string
  appType: string
  appConfig: Record<string, any>
  enabled: boolean
  createdAt: string
}

export interface Subscription {
  id: string
  botAccountId: string
  aiAppId: string
  sessionTimeoutMinutes: number
  showErrorToUser: boolean
  enabled: boolean
}

export interface MessageLogItem {
  id: string
  botAccountId: string
  aiAppId: string
  messageContent: string
  responseContent: string
  status: 'success' | 'error'
  errorMessage?: string
  createdAt: string
}

export interface ApiResponse<T = any> {
  code: number
  data?: T
  message?: string
}

export interface ListResponse<T> {
  total: number
  pageNo: number
  pageSize: number
  items: T[]
}
