# CLAUDE.md

本文件为在此仓库中使用 Claude Code（claude.ai/code）提供指导。

## 项目概览

本项目使用 **OpenSpec** - 一个规范驱动的工作流系统，用于管理软件开发变更。工作流围绕在实现前创建提案、设计、规范和任务而组织。

## OpenSpec 工作流命令

项目使用 `openspec` CLI 管理变更。关键命令：

```bash
# 列出所有活跃的变更
openspec list --json

# 创建新变更
openspec new change "<name>"

# 检查变更状态（显示工件及其完成情况）
openspec status --change "<name>" --json

# 获取创建工件的说明
openspec instructions <artifact-id> --change "<name>" --json

# 获取应用说明（实现上下文）
openspec instructions apply --change "<name>" --json
```

## OPSX 斜杠命令

提供四个工作流命令：

| 命令 | 用途 |
|------|------|
| `/opsx:explore` | 思考/发现模式 - 探索想法，不实现功能 |
| `/opsx:propose` | 创建新变更及所有工件（提案、设计、任务） |
| `/opsx:apply` | 实现变更中的任务 |
| `/opsx:archive` | 归档完成的变更 |

### 典型工作流步骤

1. **探索** (`/opsx:explore`) - 思考问题、调查代码库、澄清需求
2. **提案** (`/opsx:propose <name>`) - 创建包含 proposal.md、design.md、tasks.md 的变更
3. **应用** (`/opsx:apply`) - 逐个实现任务，标记为完成
4. **归档** (`/opsx:archive`) - 移动完成的变更到归档

## 项目结构

```
openspec/
├── config.yaml        # 模式配置（规范驱动）
├── changes/           # 活跃变更存储位置
│   └── <name>/        # 每个变更有独立目录
│       ├── .openspec.yaml
│       ├── proposal.md
│       ├── design.md
│       ├── tasks.md
│       └── specs/     # 增量规范（主规范的变更）
└── specs/             # 主规范（永久规范）
```

## 模式：spec-driven

项目使用 `spec-driven` 模式（在 `openspec/config.yaml` 中定义）。此模式要求：

- **proposal.md** - 是什么和为什么（问题、目标、范围、非目标）
- **design.md** - 如何实现（架构、组件、数据模型、API）
- **tasks.md** - 实现步骤（检查清单格式）
- **specs/** - 增量规范，用于修改主规范

## 探索模式规则

使用 `/opsx:explore` 时：
- 这只是**思考** - 不实现功能
- 可以读取文件、搜索代码、调查代码库
- 可以创建 OpenSpec 工件（记录思考，不实现）
- 可以大量使用 ASCII 图表来可视化想法
- 不自动保存洞察 - 主动询问是否需要保存