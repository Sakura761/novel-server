# novel-server 项目文档

## 1. 项目概览
- 小说阅读与管理平台的后端服务，基于 Spring Boot 3.5.6 构建
- 核心职责：提供用户、书籍、章节、分类、书架、统计、排行等业务接口
- 采用 RESTful API 风格，统一使用 `ResultVO` 作为响应包装格式
- 通过 Swagger/OpenAPI 提供在线接口文档，默认访问路径为 `/swagger-ui.html`

## 2. 核心能力
- 用户注册、登录、信息查询与维护，支持头像上传到 MinIO 与 jsDelivr CDN
- 书籍增删改查、聚合详情查询、分页检索、ElasticSearch 全文搜索
- 章节管理与阅读导航，支持批量导入、分页列表、章节上下文查询
- 分类管理（增删改查、频道筛选、层级查询）
- 书架收藏、阅读进度维护、分页列表与去重校验
- 书籍阅读/推荐/月票/收藏统计，Redis 实时累积 + MySQL 持久化
- 日榜/周榜/月榜/巅峰榜排行生成与对外查询

## 3. 技术栈
- 语言与运行时：Java 21
- 框架：Spring Boot、Spring MVC、Spring Security、Spring Scheduling
- 数据访问：MyBatis (注解接口 + XML)、PageHelper 分页插件
- 数据库：MySQL 8.x（需要手工维护表结构）
- 缓存与统计：Redis (Lettuce 客户端)
- 搜索：Elasticsearch 8.x（`books` 索引）
- 存储：MinIO 对象存储 + GitHub 仓库 + jsDelivr CDN
- 认证：JWT (jjwt 0.11.5)
- 文档：springdoc-openapi 2.8.12
- 其他：Lombok、Spring Boot DevTools

## 4. 架构设计
```
客户端 -> Controller 层 -> Service 层 -> Mapper 层 -> MyBatis XML -> MySQL
                                └─> RedisTemplate (统计缓存)
                                └─> ElasticsearchClient (全文检索)
                                └─> MinioClient / GitHub API (文件上传)
```
- `controller`：暴露 REST 接口，使用 Swagger 注解完善文档
- `service`：编排业务逻辑，拆分接口 (`service`) 与实现 (`service.impl`)
- `mapper`：MyBatis 映射接口，配套 XML 位于 `src/main/resources/mapper`
- `entity`：与数据库表一一映射的数据对象
- `DTO.Request` / `DTO.Response`：请求与响应载体，减轻实体泄露
- `core.common.vo.ResultVO`：统一响应封装，含 `code/message/data`
- `core.config`：公共配置（安全、Redis、Swagger、JWT 过滤器等）
- `scheduler`：基于 `@Scheduled` 的后台任务（排行生成、统计落库）
- `utils`：JWT、用户上下文等辅助工具

## 5. 关键业务流程
- **用户注册**：接收 multipart 请求 → 校验唯一性 → 密码加密 → 头像上传 → MySQL 持久化 → 返回脱敏数据
- **用户登录**：验证凭据 → 检查状态 → 使用 `JwtUtil` 签发 Token → 返回 `UserLoginResponse`
- **书籍详情页**：`BookServiceImpl#getBookDetailById` 聚合书籍、作者、分类、最新章节 → 访问详情接口时额外递增 Redis 阅读量
- **搜索流程**：`EsSearchServiceImpl#searchBooks` 构建 bool 查询 + 分页 + 高亮 → ElasticSearch → DTO 转换 → 包装分页结果
- **统计流水**：各业务接口通过 `BookStatsRedisService` 在 Redis 累加 → `StatsPersistenceScheduler` 每日离线扫描 `book:stats:{date}:{bookId}` → 持久化到 `book_daily_stats` 与总表
- **排行榜生成**：`RankingScheduler` 每日 02:00 触发 → 依次数值生成日榜/巅峰榜，按周一/月初补充对应榜单

## 6. 数据与集成
- **MySQL 表**（需在数据库手工建表，与实体/Mapper 对应）：`users`、`authors`、`books`、`chapters`、`categories`、`user_bookshelf`、`book_stats`、`book_daily_stats` 等
- **Redis**：
  - 实时统计键：`book:stats:{yyyy-MM-dd}:{bookId}`，Hash 字段包括 `readCount`、`recommendVotes`、`monthlyTickets`、`collectionCount`
  - 计划任务扫描模式：`book:stats:{date}:*`
- **ElasticSearch**：索引名 `books`，映射自 `DTO.es.BookDocument`，字段包含书籍/作者/分类/章节信息，支持关键字及筛选查找
- **MinIO**：`minio.bucket-name` 存储用户上传头像，最终访问路径 `{endpoint}/{bucket}/{object}`
- **GitHub + jsDelivr**：头像另行推送到 GitHub 仓库（`github.owner/repo`），jsDelivr 生成 CDN 链接

## 7. 配置说明（`application.yml`）
- `spring.datasource.*`：MySQL 连接信息
- `spring.data.redis.*`：Redis 单节点配置
- `spring.elasticsearch.uris`：ElasticSearch 地址
- `github.token/owner/repo`：GitHub API 凭据（用于头像 CDN）
- `jwt.secret/expiration`：JWT 签名密钥与过期时间（毫秒）
- `minio.endpoint/access-key/secret-key/bucket-name`：MinIO 客户端配置
- `springdoc.*`：OpenAPI 文档路径
- `pagehelper.*`：分页插件参数
> 在生产环境请通过环境变量或配置中心覆盖敏感信息

## 8. 接口分组（按 Controller）
| 模块 | 路径前缀 | 主要职责 |
| --- | --- | --- |
| UserController | `/api/users` | 用户 CRUD、注册、登录、分页查询 |
| BookController | `/api/books` | 书籍 CRUD、详情聚合、分页列表、ES 搜索、统计累积 |
| ChapterController | `/api/chapters` | 章节 CRUD、分页列表、阅读导航 |
| CategoryController | `/api/categories` | 分类 CRUD、层级/频道查询、存在性校验 |
| BookShelfController | `/api/bookshelf` | 书架增删查、分页与存在性检查（需认证） |
| BookStatsController | `/api/book-stats` | 书籍统计指标的增量更新、查询、Redis 健康检查 |
| RankController | `/api/rankings` | 日/周/月/巅峰榜查询 |
| EsController | `/api/es` | 书籍索引同步与搜索（主要用于内部调试） |
| AuthorController 等 | `/api/authors` | 作者信息维护（结构类似，上述略） |

## 9. 定时任务
- `RankingScheduler#generateRankings`：每日 02:00 生成日榜与巅峰榜，周一补充周榜，月初补充月榜
- `StatsPersistenceScheduler#persistDailyStats`：每日 01:00 抽取昨日 Redis 统计写入 MySQL，同时刷新书籍总统计
> 需要在应用入口类或配置上启用 `@EnableScheduling`

## 10. 安全与认证
- `SecurityConfig` 默认放行除 `/api/bookshelf/**` 以外的接口，后者需要 JWT 认证
- `JwtAuthenticationFilter`：从 `Authorization: Bearer <token>` 中解析用户信息，写入 `SecurityContext`
- `JwtUtil`：封装 token 生成、校验与解析逻辑，密钥来源 `jwt.secret`
- `UserContextUtil`：提供线程上下文的 `userId`/`username` 读取
- 建议在后续扩展中结合自定义注解 `@RequireJwtAuth` 细化权限校验

## 11. 开发与运行指南
1. 准备依赖：JDK 21、Maven 3.9.x、MySQL、Redis、Elasticsearch、MinIO（可选）
2. 创建数据库并导入表结构，更新 `application.yml` 中的数据源/Redis/ES/MinIO/GitHub 配置
3. 如需头像 CDN，设置环境变量 `GITHUB_TOKEN`/`GITHUB_OWNER`/`GITHUB_REPO`
4. 启动依赖服务（MySQL、Redis、Elasticsearch、MinIO）
5. 安装依赖并启动应用：
   - `./mvnw.cmd clean package`
   - `./mvnw.cmd spring-boot:run`
6. 访问 `http://localhost:8080/swagger-ui.html` 查看接口文档

## 12. 测试与质量
- 目前仅包含 `ServerApplicationTests#contextLoads`，覆盖度为 0
- 建议补充：
  - Service 层单元测试（Mock Mapper）
  - Controller 层集成测试（WebMvcTest 或 Testcontainers）
  - 定时任务与 Redis 统计的集成回归测试

## 13. 运维建议
- 监控 Redis key 数量，确保统计数据按时清理
- ElasticSearch 索引同步：通过 `GET /api/es/insertAll` 全量导入
- MinIO 存储桶需提前创建或授予创建权限
- 生产环境应将敏感配置移出 Git 仓库，启用 HTTPS、跨域白名单等安全策略

## 14. 后续可优化方向
- 引入领域事件或消息队列，异步解耦统计与排行生成
- 统一异常与校验处理（如使用 `@ControllerAdvice`）
- 引入角色/权限模型，细化 Spring Security 规则
- 为 ES 构建索引模板与自动刷新策略，支持增量更新
- 提供数据库迁移脚本（Flyway/Liquibase）与示例数据集
