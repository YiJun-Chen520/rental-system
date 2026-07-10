# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在本仓库中工作时提供指导。

## 项目概述

rental-system — 房屋租赁管理系统，三端架构（房东端、租户端、管理端），共用同一数据库。

## 技术栈
- JSP + Servlet + JDBC（原生 Java Web）
- MySQL 8.0 数据库
- JWT 认证
- 支付功能为模拟实现，不接入真实支付接口

## 项目结构

```
rental-system/
├── backend/              # 后端（Servlet + JDBC）
│   └── src/main/java/com/rental/
│       ├── servlet/      # 接口层（owner/tenant/admin）
│       ├── service/      # 业务逻辑层
│       ├── dao/          # 数据访问层
│       ├── entity/       # 实体类
│       ├── dto/          # 数据传输对象
│       ├── util/         # 工具类
│       └── filter/       # 过滤器
├── frontend/             # 前端（Vue3）
├── docs/                 # 项目文档
└── 租赁系统.sql           # 数据库脚本
```

## 数据库

共 6 张核心表：Owner（房东）、House（房源）、Tenant（租户）、Contract（合同）、Payment（费用）、Review（评价），外加 Admin（管理员）表。

脚本文件：`租赁系统.sql`

## 接口规范

- 三端接口路径分离：`/api/owner/*`、`/api/tenant/*`、`/api/admin/*`
- 数据格式：JSON
- 认证：JWT Token（Header: `Authorization: Bearer <token>`）
- 响应格式：`{ "code": 200, "message": "操作成功", "data": {} }`

## 编码规范

- 注释使用中文
- 接口采用 RESTful 风格
- 前后端保持一致，避免中英文注释混用
- 专业术语可用英文

## 注意事项

- 涉及到金钱的，并不用真的调用支付接口，内部模拟支付即可
- 若没特殊要求，CLAUDE.md 严格禁止 AI 改动
- 每次运行完自动更新 memory-report.md
