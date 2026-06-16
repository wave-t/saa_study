# SAA Study — Spring AI Alibaba 学习项目

## 项目简介

**saa_study** 是一个基于 **Spring AI Alibaba** 的 AI 应用开发学习项目，通过 10 个递进式模块，系统性地演示了如何使用 Spring AI 框架集成阿里云百炼平台（DashScope）、Ollama 本地模型、Milvus 向量数据库等，构建对话、文生图、向量嵌入、RAG 检索增强生成等 AI 能力。

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.5.14 |
| Spring AI | 1.1.2 |
| Spring AI Alibaba | 1.1.2.0 |
| Maven | 多模块构建（POM） |

**核心依赖管理**：通过 BOM 统一管理版本

- `spring-ai-alibaba-bom` — 阿里云百炼平台集成（DashScope 对话 / 嵌入 / 图像模型）
- `spring-ai-bom` — Spring AI 核心依赖（ChatClient / ChatMemory / RAG / VectorStore）
- `spring-boot-dependencies` — Spring Boot 全家桶

## 项目结构
saa_study/ 
├── pom.xml # 父 POM，统一版本与模块管理 
├── saa_01_quickly/ # 模块一：快速入门 
├── saa_02_ollama_demo/ # 模块二：Ollama 本地模型 
├── saa_03_chatclient_demo/ # 模块三：ChatClient API 
├── saa_04_prompt_demo/ # 模块四：提示词工程 
├── saa_05_chat_memory01/ # 模块五：自定义对话记忆（MySQL） 
├── saa_06_chat_memory02/ # 模块六：内置对话记忆（Advisor） 
├── saa_07_text2image/ # 模块七：文生图 
├── saa_08_embedding/ # 模块八：文本向量化 
├── saa_09_milvus_demo/ # 模块九：Milvus 向量数据库 
└── saa_10_rag_demo/ # 模块十：RAG 检索增强生成
## 模块详解

### 1. saa_01_quickly — 快速入门

最简对话示例，演示 `ChatModel` 的基本用法。

| 接口 | 说明 |
|------|------|
| `GET /doChat?question=xxx` | 同步对话，一次性返回完整回答 |
| `GET /doChatStream?question=xxx` | 流式对话，SSE 流式输出，边想边出 |

**关键点**：`ChatModel.call()` vs `ChatModel.stream()` 返回 `Flux<String>`

---

### 2. saa_02_ollama_demo — Ollama 本地模型

演示如何注入本地 Ollama 大模型，无需云端 API Key。

| 接口 | 说明 |
|------|------|
| `GET /doChatStream?question=xxx` | 流式对话（本地 Ollama 模型） |

**关键点**：通过 `@Qualifier` 或 Bean 名称注入不同模型实例，支持同时使用远端 DashScope + 本地 Ollama

---

### 3. saa_03_chatclient_demo — ChatClient API

演示 Spring AI 推荐的 `ChatClient` 流式 API，采用 Builder 模式构建请求。

| 接口 | 说明 |
|------|------|
| `GET /doChatStream?question=xxx` | 使用 ChatClient 构建对话 |

**关键点**：`ChatClient` 提供 `.prompt().user().call().content()` 链式调用，是官方推荐的统一入口

---

### 4. saa_04_prompt_demo — 提示词工程

深入演示 Prompt 的构建方式，包括 SystemMessage / UserMessage、PromptTemplate、外部模板文件。

| 接口 | 说明 |
|------|------|
| `GET /prompt?question=xxx` | 基础 Prompt |
| `GET /prompt2?question=xxx` | 带 System 角色设定（行政总厨） |
| `GET /promptTemplate?companyName=&type=&name=&job=&entryDate=&range=&fare=` | 编程式模板（生成入职 Offer） |
| `GET /promptTemplate2?companyName=&type=&name=&job=&entryDate=&range=&fare=` | 文件式模板（从 `system.st` / `user.st` 加载） |

**关键点**：
- `PromptTemplate` 支持 `{placeholder}` 占位符替换
- 外部 `.st` 模板文件可实现提示词与代码分离
- 组装 `Prompt = SystemMessage + UserMessage` 明确区分角色

---

### 5. saa_05_chat_memory01 — 自定义对话记忆

基于 MySQL 实现自定义 `ChatMemory`，通过 AOP 切面拦截并持久化对话上下文。

**核心组件**：

| 组件 | 说明 |
|------|------|
| `MysqlChatMemory` | 自定义 ChatMemory 实现，基于 MySQL 存储 |
| `ChatMemoryAdvice` | AOP 环绕通知，自动保存对话记录 |
| `ChatMemoryHolder` | ThreadLocal 持有当前请求的对话上下文 |
| `ChatMemoryContext` | `@RequestScope` Bean，封装单次请求的会话信息 |

| 接口 | 说明 |
|------|------|
| `GET /ask?conversationId=&question=` | 带记忆的多轮对话 |

**关键点**：通过切面编程实现对话记忆与业务逻辑解耦

---

### 6. saa_06_chat_memory02 — 内置对话记忆

使用 Spring AI 内置的 `ChatMemory` 与 `Advisor` 机制，无需手写记忆逻辑。

| 接口 | 说明 |
|------|------|
| `GET /ask?userId=&conversationId=&question=` | 带记忆的多轮对话 |

**关键点**：`.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))` 一行配置即可启用内置记忆

---

### 7. saa_07_text2image — 文生图

调用 DashScope 图像生成模型，根据文本描述生成图片。

| 接口 | 说明 |
|------|------|
| `GET /text2image?text=xxx` | 文本生成图片，返回图片 URL |

**关键点**：`ImageModel.call(ImagePrompt)` → `DashScopeImageOptions` 配置模型参数

---

### 8. saa_08_embedding — 文本向量化

调用 DashScope Embedding 模型，将文本转换为向量表示。

| 接口 | 说明 |
|------|------|
| `GET /embedding?text=xxx` | 文本转向量，返回浮点数组 |

**关键点**：`EmbeddingModel.call(EmbeddingRequest)` → 向量可用于语义搜索、RAG 等下游任务

---

### 9. saa_09_milvus_demo — Milvus 向量数据库

完整的 Milvus 集成示例，涵盖 Collection 创建、数据增删查、向量存储。

**核心组件**：

| 组件 | 说明 |
|------|------|
| `MilvusCreateDemo` | Milvus SDK 原生操作（创建集合、插入、查询、删除） |
| `MilvusUtils` | Milvus 工具类，封装常用操作 |
| `MilvusDemoController` | 结合 Embedding 模型，将文本向量存储到 Milvus |

| 接口 | 说明 |
|------|------|
| `GET /embedding?text=xxx` | 文本向量化后存入 Milvus |

**关键点**：
- `MilvusClientV2` 客户端连接与认证
- `CollectionSchema` 定义字段（主键 / 向量 / 标量）
- `IndexParam` 配置 IVF_FLAT 索引 + COSINE 度量

---

### 10. saa_10_rag_demo — RAG 检索增强生成

结合 Milvus 向量数据库实现完整的 RAG 流程：文档向量化 → 语义检索 → 增强回答。

| 接口 | 说明 |
|------|------|
| `GET /rag?question=xxx` | RAG 增强问答 |

**关键点**：
- `RetrievalAugmentationAdvisor` 自动完成检索 → 增强 → 生成的完整链路
- 配置文件方式集成 Milvus（`spring.ai.vectorstore.milvus`）
- 支持 PDF 文档的自动向量化与入库

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- （可选）阿里云百炼 API Key — 用于云端模型调用
- （可选）Ollama 本地服务 — 用于本地模型调用
- （可选）MySQL — 用于 saa_05 自定义记忆存储
- （可选）Milvus 向量数据库 — 用于 saa_09 / saa_10 向量存储

### 运行步骤

1. **克隆项目并进入目录**
bash 
git clone <repo-url> 
cd saa_study

2. **配置 API Key**
在系统环境变量中设置：
bash
Windows
set AI_DASHSCOPE_API_KEY=your-dashscope-api-key
Linux / Mac
export AI_DASHSCOPE_API_KEY=your-dashscope-api-key

3. **编译项目**
bash
mvn clean compile

4. **启动目标模块**（以模块一为例）
bash 
cd saa_01_quickly 
mvn spring-boot:run

5. **测试接口**
bash 
curl "http://localhost:8080/doChat?question=你是谁？" 
curl "http://localhost:8080/doChatStream?question=用Java写一个HelloWorld"

### 各模块端口

| 模块 | 端口 |
|------|------|
| saa_01_quickly | 8080 |
| saa_02_ollama_demo | 8081 |
| saa_03_chatclient_demo | 8082 |
| saa_04_prompt_demo | 8083 |
| saa_05_chat_memory01 | 8085 |
| saa_06_chat_memory02 | 8086 |
| saa_07_text2image | 8087 |
| saa_08_embedding | 8088 |
| saa_09_milvus_demo | 8089 |
| saa_10_rag_demo | 8010 |

## 关键设计要点

- **多模型共存**：通过 Java Config 手动注入多个 `ChatModel` Bean，避免 `application.yml` 只能配置单一模型的限制
- **统一入口**：从 `ChatModel` 到 `ChatClient` 的演进，后者是官方推荐的统一 API 入口
- **记忆分层**：模块 5（手工 AOP）→ 模块 6（内置 Advisor），展示了从底层实现到框架抽象的渐进式理解
- **RAG 闭环**：模块 8（向量化）→ 模块 9（向量存储）→ 模块 10（检索增强），完整串联 RAG 技术链路

## 学习路线建议
快速入门（01） 
├── 对话：01 → 03（ChatClient）→ 04（提示词） 
│ ├── 05（手工记忆） 
│ └── 06（内置记忆） 
├── 本地模型：02（Ollama） 
└── 多模态 & RAG：07（文生图）→ 08（向量化）→ 09（Milvus）→ 10（RAG）

## 许可证

本项目仅用于学习目的。


