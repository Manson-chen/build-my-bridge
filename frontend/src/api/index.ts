import client from './client'
import { User, BotAccount, AiApp, Subscription, MessageLogItem, ApiResponse, ListResponse } from '../types'

export const authApi = {
  getLoginUrl: () => client.get<ApiResponse<{ url: string }>>('/auth/login-url'),
  callback: (code: string) => client.post<ApiResponse<{ token: string; user: User }>>('/auth/callback', { code }),
  getUser: () => client.get<ApiResponse<User>>('/auth/user'),
}

export const botApi = {
  list: () => client.get<ApiResponse<BotAccount[]>>('/bots'),
  create: (data: any) => client.post<ApiResponse<BotAccount>>('/bots', data),
  update: (id: string, data: any) => client.put<ApiResponse<BotAccount>>(`/bots/${id}`, data),
  delete: (id: string) => client.delete(`/bots/${id}`),
}

export const appApi = {
  list: () => client.get<ApiResponse<AiApp[]>>('/apps'),
  create: (data: any) => client.post<ApiResponse<AiApp>>('/apps', data),
  update: (id: string, data: any) => client.put<ApiResponse<AiApp>>(`/apps/${id}`, data),
  delete: (id: string) => client.delete(`/apps/${id}`),
  getAffectedBots: (appId: string) => client.get<ApiResponse<BotAccount[]>>(`/apps/${appId}/affected-bots`),
}

export const logApi = {
  list: (params: any) => client.get<ApiResponse<ListResponse<MessageLogItem>>>('/logs', { params }),
  getStats: () => client.get<ApiResponse<any>>('/logs/stats'),
}
