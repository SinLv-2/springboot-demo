# CLAUDE.md

本项目是一个 Spring Boot 2.5.5 应用，集成了 **Activiti 7** 工作流引擎（版本 7.1.0.M6），配合 MySQL、MyBatis-Plus、Swagger 3 和 FastJSON2 使用。包名为 `com.ybchen`，artifactId 为 `ybchen-activiti7`。

## 常用命令

```powershell
# 构建
mvn clean compile    # 项目中无 Maven Wrapper，直接使用 mvn
mvn package

# 运行应用（端口 18080）
mvn spring-boot:run

# 运行测试
mvn test

# 运行单个测试
mvn test -Dtest=ActivitiApplicationTests
```

## 架构

### 核心技术栈

- **Spring Boot 2.5.5** + **Java 8**
- **Activiti 7**（`activiti-spring-boot-starter`）—— 工作流引擎。`ActivitiApplication.java` 中排除了 `SecurityAutoConfiguration`，因为 Activiti 默认引入了 Spring Security。
- **MyBatis-Plus 3.4.0** —— Activiti 7 内部使用 MyBatis 作为 ORM，此处排除了 starter 自带的 MyBatis 以避免冲突。
- **MySQL** —— 存储 Activiti 引擎表和流程历史数据。
- **Swagger 3**（`springfox-boot-starter 3.0.0`）—— API 文档，仅扫描 `com.ybchen.controller` 包。

### 包结构

```
com.ybchen/
├── ActivitiApplication.java          # @SpringBootApplication 入口，排除 Security 自动配置
├── config/
│   └── SwaggerConfiguration.java     # Swagger 3 Docket 配置 Bean
├── controller/
│   └── ActivitiController.java       # 唯一 REST 控制器，包含所有工作流操作
├── exception/
│   └── GlobalException.java          # @RestControllerAdvice 全局异常处理器
├── listener/
│   └── MangerExecutionListener.java  # Activiti 执行监听器，用于经理审批节点
├── service/
│   └── SpringSecurityUserService.java # 内存版 UserDetailsService，供 Activiti 身份服务使用
└── utils/
    └── ReturnData.java               # 统一 JSON 响应封装类（code/data/msg）
```

### ActivitiController — REST API 接口一览

所有接口路径前缀为 `/`（通过 Swagger 映射）：

| 方法 | 接口 | 说明 |
|------|------|------|
| POST | `/deploy` | 上传 BPMN ZIP 压缩包部署流程 |
| POST | `/queryDeploymentInfo` | 查询所有流程部署信息 |
| POST | `/queryProcessInfo` | 查询所有流程定义信息 |
| GET | `/deleteDeploymentById?deploymentId=...` | 根据部署 ID 删除流程部署 |
| GET | `/startProcess?processDefinitionId=...` | 启动流程实例，传入变量（userName、day） |
| GET | `/completeTask?processInstanceId=...` | 完成当前流程实例的待办任务 |
| GET | `/queryHistoryProcessInstance` | 查询历史流程实例 |
| GET | `/queryHistoryTask` | 查询历史任务 |
| GET | `/queryActivityInstance` | 查询历史活动实例 |
| GET | `/queryByAssigneeTask?assignee=...` | 根据办理人查询待办任务 |
| GET | `/updateAssigneeByTaskId?taskId=...&assignee=...` | 更新任务办理人 |
| GET | `/addComment?taskId=...&processInstanceId=...&message=...` | 添加审批意见 |
| GET | `/queryComment?taskId=...` | 查询个人审批意见 |
| GET | `/queryTaskByCandidateUser?userName=...` | 根据候选人查询任务 |
| GET | `/claimTask?taskId=...&userName=...` | 候选人拾取任务 |
| GET | `/delegateTask?taskId=...&userName=...` | 任务委派 |
| GET | `/setAssignee?taskId=...&userName=...` | 任务转办 |

控制器注入了 Activiti 的四大核心服务：`RepositoryService`、`RuntimeService`、`TaskService`、`HistoryService`。

### 身份服务

`SpringSecurityUserService` 实现了 `UserDetailsService`，在内存中创建用户，供 Activiti 的 `IdentityService` 进行任务候选人/办理人查找。用户名和密码相同（`User.withUsername(username).password(username)...`）。

### 流程定义文件

BPMN 2.0 XML 文件和 PNG 流程图位于 `src/main/resources/` 目录下：

- `oa-leave.bpmn20.xml` —— OA 请假审批流程：开始 → 经理审批 → 人事审批 → 结束
- `test01.bpmn20.xml` ~ `test08.bpmn20.xml` —— 各种测试用流程定义
- 部分流程打包为 ZIP 归档文件（`test01.zip`、`test02.zip`），可通过 `/deploy` 接口上传部署

### 配置说明（application.properties）

- 服务端口：**18080**
- 数据库：MySQL `39.104.81.97:3306/activity`
- Activiti 历史级别 `history-level=full` —— 保存全部流程相关细节数据
- `database-schema-update=true` —— 启动时自动创建/更新 Activiti 数据库表
- MyBatis-Plus 将 SQL 日志输出到控制台

### ReturnData 统一响应格式

`ReturnData<T>` 为可序列化的统一响应对象，包含三个字段：`code`（200=成功，-1=失败）、`data`（T）、`msg`（描述信息）。提供的静态构造方法：`buildSuccess()`、`buildSuccess(data)`、`buildError(msg)`、`buildCodeAndMsg(code, msg)`、`buildBizCodeAndMsg(bizCode, bizContent)`。
