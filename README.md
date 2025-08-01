# 文件查询服务 (File Query Service)

**该工程仅用于AI代码生成工具的测试**

## 项目简介

这是一个简单的Spring Boot项目，用于演示文件系统查询功能。项目采用分层架构设计，包含领域层、服务层、接口层和基础层。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Maven
- JUnit 5 + Mockito

## 项目结构

```
src/
├── main/
│   ├── java/com/example/filequery/
│   │   ├── domain/            # 领域层 - 实体类
│   │   ├── service/           # 服务层 - 业务逻辑
│   │   ├── controller/        # 接口层 - REST API
│   │   ├── infrastructure/    # 基础层 - 数据访问
│   │   └── FileQueryApplication.java
│   └── resources/
│       └── application.yml
└── test/                      # 单元测试
```

## 功能接口

### 1. 文件列表查询接口

**接口地址：** `GET /api/files/list`

**参数：** `directoryPath` - 目录路径

**功能：** 查询指定目录下所有文件的信息（文件名、文件类型、修改时间、大小）

**示例：**
```
GET /api/files/list?directoryPath=/home/user/documents
```

### 2. 文件详细信息查询接口

**接口地址：** `GET /api/files/info`

**参数：** `filePath` - 文件或目录路径

**功能：** 查询指定文件或目录的详细信息（名称、是否目录、修改时间、大小）

**示例：**
```
GET /api/files/info?filePath=/home/user/documents/readme.txt
```

## 运行方式

1. 确保已安装Java 17和Maven
2. 在项目根目录执行：
   ```bash
   mvn spring-boot:run
   ```
3. 应用启动后访问：`http://localhost:8080`

## 测试

运行单元测试：
```bash
mvn test
```

项目包含完整的单元测试，覆盖每个接口的多种场景：
- 正常查询场景
- 异常情况处理
- 边界条件测试

## 注意事项

- 该项目仅用于AI代码生成工具的测试和演示
- 代码设计简洁明了，便于理解和扩展
- 严格按照分层架构组织代码结构
