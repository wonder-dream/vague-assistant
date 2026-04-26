# VagueAssistant

基于 DeepSeek + RAG + 工具调用的 AI 对话助手，支持知识库管理、多 Agent 配置和流式对话。

## 技术栈

- **后端**：Spring Boot 3 + MyBatis-Plus + PostgreSQL (pgvector)
- **AI**：DeepSeek API（对话 + 工具调用）+ Ollama bge-m3（向量化）
- **前端**：Vue 3 + Vite + Element Plus

## 功能

- 多会话管理（创建、切换、重命名、删除）
- 多 Agent 配置（自定义系统提示词、关联知识库）
- RAG 知识库（上传 txt/md 文档 → 自动切片 → 向量化 → 检索注入）
- 工具调用（Function Calling，内置计算器，可扩展）
- 流式对话（SSE）

## 快速开始

### 前置依赖

- JDK 17+
- PostgreSQL 15+（需安装 [pgvector](https://github.com/pgvector/pgvector) 扩展）
- [Ollama](https://ollama.com)（本地向量化）
- Node.js 18+

### 1. 初始化数据库

```sql
-- 执行项目根目录的 jchatmind.sql
psql -U postgres -d your_db -f jchatmind.sql
```

### 2. 拉取向量化模型

```bash
ollama pull bge-m3
```

### 3. 配置后端

```bash
cp vague-web/src/main/resources/application.yml.example \
   vague-web/src/main/resources/application.yml
```

编辑 `application.yml`，填入：
- `deepseek.api-key`：[DeepSeek 控制台](https://platform.deepseek.com) 获取
- `spring.datasource.*`：PostgreSQL 连接信息

### 4. 启动后端

```bash
mvn spring-boot:run -pl vague-web
```

### 5. 启动前端

```bash
cd vague-ui
npm install
npm run dev
```

打开 http://localhost:5173

## 项目结构

```
vagueAssistant/
├── vague-core/          # 核心业务逻辑（chat + rag + tool）
├── vague-web/           # Web 层（Controller + 配置）
├── vague-ui/            # Vue 3 前端
└── jchatmind.sql        # 数据库建表脚本
```

## 扩展工具

实现 `ToolExecutor` 接口并加 `@Component` 即可自动注册：

```java
@Component
public class MyTool implements ToolExecutor {
    @Override public String name() { return "my_tool"; }
    @Override public ToolDefinition definition() { /* 描述工具参数 */ }
    @Override public String execute(String argumentsJson) { /* 执行逻辑 */ }
}
```

## License

MIT
