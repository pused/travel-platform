# 旅游攻略平台（梦想旅途）

一个前后端分离的旅游攻略与门票预订平台：用户可以浏览景点、阅读和发布旅游攻略、预订门票，并通过 AI 助手获取旅行建议；管理员可在后台管理全部内容并查看数据统计。

> 本项目为毕业设计，仅用于学习和展示。

## 技术栈

**后端**
- Spring Boot 2.6.13 / JDK 1.8
- MyBatis-Plus 3.5.2 + MySQL
- Sa-Token 1.38.0（JWT 登录认证与权限控制）
- Knife4j（接口文档）、Hutool、EasyExcel
- 接入 DeepSeek 大模型 API 实现 AI 旅行助手

**前端**
- Vue 3 + Vite 6
- Element Plus、Pinia（含持久化）、Vue Router
- ECharts（后台数据统计）
- 高德地图 JS API（景点地图展示）
- wangEditor（富文本编辑，用于发布攻略）、markdown-it、axios

## 主要功能

**前台（用户端）**
- 景点列表 / 景点详情（含地图展示、评论）
- 旅游攻略列表 / 详情，用户可发布、管理自己的攻略
- 门票列表 / 详情 / 下单，查看我的订单
- 收藏、点赞、评论
- AI 助手：基于 DeepSeek 的旅行问答
- 个人信息管理、注册 / 登录

**后台（管理端）**
- 用户、公告、轮播图管理
- 景点分类、景点、旅游攻略管理
- 评论、收藏、点赞管理
- 门票、门票订单管理
- 首页数据统计图表

## 项目结构

```
travel-platform
├── backend/        # Spring Boot 后端
│   └── src/main/
│       ├── java/com/example/backend/
│       │   ├── controller/   # 接口层
│       │   ├── entity/       # 实体、请求对象、VO
│       │   ├── config/       # 配置（Sa-Token、MyBatis-Plus、Swagger 等）
│       │   └── common/       # 常量、枚举、统一返回结构
│       └── resources/        # application.yaml、Mapper XML
├── frontend/       # Vue 3 前端（前台 + 后台）
└── db_travel.sql   # 数据库建表及初始数据
```

## 运行方式

**环境要求**：JDK 8+、Maven、MySQL 5.7+、Node.js 18+

1. **初始化数据库**
   - 创建数据库 `db_travel`，并导入根目录下的 `db_travel.sql`

2. **配置后端**
   - 修改 `backend/src/main/resources/application.yaml`：
     - `spring.datasource`：改成你本地的 MySQL 地址、账号、密码
     - `spring.ai.deepseek.apiKey`：填写你自己的 DeepSeek API Key（不填则 AI 助手不可用）

3. **启动后端**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   默认端口 `8080`，接口前缀 `/api`。

4. **启动前端**
   ```bash
   cd frontend
   npm install    # 或 pnpm install
   npm run dev
   ```
   按终端提示的地址访问即可。前台地址为 `/front/home`，后台管理入口为登录后的 `/home`。

5. **地图功能**
   - 在 `frontend/src/config/index.js` 中填入你自己的高德地图 `mapKey` 和 `safetyKey`

## 测试账号

数据库初始数据中包含测试账号（仅用于本地演示）：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123 |
| 普通用户 | user | 123 |

## 说明

- 上传的图片保存在 `backend/files/`，仅为演示数据。
- 请勿将真实的 API Key、数据库密码提交到公开仓库。
