# 测试资源目录说明

本目录包含文件查询系统的集成测试所需的真实测试文件和目录。

## 目录结构

```
src/test/resources/
├── test-data/                    # 主要测试数据目录
│   ├── config.properties         # 属性配置文件（用于测试.properties文件类型识别）
│   ├── sample.java               # Java源代码文件（用于测试.java文件类型识别）
│   ├── document.pdf.txt          # 模拟PDF文件（用于测试文件扩展名处理）
│   ├── image.png.txt             # 模拟PNG图片文件（用于测试文件扩展名处理）
│   └── subfolder/                # 子目录（用于测试目录查询功能）
│       └── nested-file.txt       # 嵌套文件（用于测试子目录文件查询）
└── empty-folder/                 # 空目录（用于测试空目录查询功能）
```

## 测试文件内容

### config.properties
- **用途**: 测试属性文件类型识别
- **内容**: 包含测试应用配置参数
- **文件类型**: properties

### sample.java
- **用途**: 测试Java源代码文件类型识别
- **内容**: 简单的Java Hello World程序
- **文件类型**: java

### document.pdf.txt / image.png.txt
- **用途**: 测试复杂文件扩展名的处理
- **内容**: 模拟不同类型文件的测试内容
- **文件类型**: txt (实际扩展名)

### subfolder/nested-file.txt
- **用途**: 测试子目录文件查询功能
- **内容**: 位于子目录中的测试文件
- **文件类型**: txt

## 使用这些测试资源的测试类

1. **FileSystemIntegrationTest**: 测试FileSystemRepository与真实文件系统的交互
2. **FileQueryServiceIntegrationTest**: 测试FileQueryService使用真实文件系统的完整流程
3. **FileQueryControllerIntegrationTest**: 测试从Controller到文件系统的完整请求处理流程

## 测试覆盖的场景

- ✅ 真实目录文件列表查询
- ✅ 空目录查询
- ✅ 不存在目录查询
- ✅ 真实文件信息查询
- ✅ 不存在文件查询
- ✅ 目录信息查询
- ✅ 子目录文件查询
- ✅ 多种文件类型识别
- ✅ 参数验证和异常处理

## 与Mock测试的对比

### 原有Mock测试的特点
- 使用Mockito模拟文件系统操作
- 测试速度快，但无法验证真实文件系统交互
- 适合单元测试，专注于业务逻辑验证

### 新增集成测试的特点
- 使用真实文件系统和测试资源
- 测试更接近实际使用场景
- 能够发现文件系统相关的实际问题
- 验证文件类型识别、路径处理等功能的正确性

## 运行说明

这些集成测试使用真实的测试资源文件，确保：
1. 所有测试文件在测试运行前已存在
2. 测试不会修改或删除测试资源文件
3. 测试结果基于真实的文件系统操作

建议同时保留Mock测试和集成测试，以获得完整的测试覆盖。