# 自动记忆库报告

> 项目：rental-system · 更新时间：2026-07-10

---

## 项目概述

rental-system — 房屋租赁管理系统，三端架构（房东端、租户端、管理端），共用同一数据库。

## 技术栈

- **后端**：JSP + Servlet + JDBC（原生 Java Web，无 Spring Boot）
- **数据库**：MySQL 8.0
- **前端**：JSP + CSS + JavaScript（纯 JSP，无 Vue）
- **认证**：JWT Token
- **支付**：模拟实现

## 已完成模块

### 数据库（租赁系统.sql）
- 7 张表：Admin、Owner、House、Tenant、Contract、Payment、Review
- 初始管理员：admin / admin123（BCrypt 加密）

### 后端（src/main/java/com/rental/）

| 层 | 数量 | 文件 |
|---|---|---|
| pom.xml | 1 | war 打包，依赖 Servlet API、MySQL JDBC、Gson、JJWT、BCrypt |
| 实体类 | 7 | Admin、Owner、House、Tenant、Contract、Payment、Review |
| DTO | 4 | Result、PageRequest、PageResult、LoginRequest |
| 工具类 | 6 | DBUtil、JwtUtil、JsonUtil、EncryptUtil、ResultUtil、ServletUtil |
| 过滤器 | 3 | CharacterEncodingFilter、CorsFilter、AuthFilter |
| DAO | 7 | AdminDao、OwnerDao、HouseDao、TenantDao、ContractDao、PaymentDao、ReviewDao |
| Service | 7组 | 接口 + 实现类（Admin/Owner/House/Tenant/Contract/Payment/Review） |
| Servlet | 21 | owner(6)、tenant(7)、admin(8) |

### 前端（src/main/webapp/）

| 模块 | 文件 |
|---|---|
| 公共资源 | static/css/style.css、static/js/common.js |
| 登录注册 | login.jsp、register.jsp |
| 房东端(6) | sidebar.jsp、dashboard.jsp、house.jsp、contract.jsp、payment.jsp、profile.jsp |
| 租户端(7) | sidebar.jsp、dashboard.jsp、house.jsp、house-detail.jsp、contract.jsp、payment.jsp、review.jsp、profile.jsp |
| 管理端(8) | sidebar.jsp、dashboard.jsp、owner.jsp、tenant.jsp、house.jsp、contract.jsp、payment.jsp、review.jsp |
| 首页 | index.jsp（重定向到登录页） |

### API 接口

| 模块 | 路径前缀 | 说明 |
|---|---|---|
| 房东端 | /api/owner/* | 认证、房源、合同、费用、租户、统计 |
| 租户端 | /api/tenant/* | 认证、房源、合同、费用、评价、统计 |
| 管理端 | /api/admin/* | 认证、房东、租户、房源、合同、费用、评价、统计 |

## 文件结构

```
rental-system/
├── pom.xml
├── 租赁系统.sql
├── src/main/
│   ├── java/com/rental/
│   │   ├── entity/       # 实体类（7个）
│   │   ├── dto/          # DTO（4个）
│   │   ├── util/         # 工具类（6个）
│   │   ├── filter/       # 过滤器（3个）
│   │   ├── dao/          # DAO层（7个）
│   │   ├── service/      # Service层（7组）
│   │   │   └── impl/
│   │   └── servlet/      # Servlet层（21个）
│   │       ├── owner/
│   │       ├── tenant/
│   │       └── admin/
│   ├── resources/
│   │   └── db.properties
│   └── webapp/
│       ├── index.jsp
│       ├── login.jsp
│       ├── register.jsp
│       ├── owner/        # 房东端JSP（6个）
│       ├── tenant/       # 租户端JSP（7个）
│       ├── admin/        # 管理端JSP（8个）
│       ├── static/
│       │   ├── css/
│       │   └── js/
│       └── WEB-INF/
│           └── web.xml
└── CLAUDE.md
```

## 开发与部署

### 后端
```bash
mvn clean package    # 编译打包
# 部署 target/rental-system.war 到 Tomcat
```

### 数据库
```bash
mysql -u root -p < 租赁系统.sql    # 导入数据库
```

### 访问
- 首页：http://localhost:8080/rental-system/
- 房东端：http://localhost:8080/rental-system/owner/dashboard.jsp
- 租户端：http://localhost:8080/rental-system/tenant/dashboard.jsp
- 管理端：http://localhost:8080/rental-system/admin/dashboard.jsp
