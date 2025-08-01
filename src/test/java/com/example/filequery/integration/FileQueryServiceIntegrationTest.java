package com.example.filequery.integration;

import com.example.filequery.domain.FileInfo;
import com.example.filequery.infrastructure.FileSystemRepository;
import com.example.filequery.service.FileQueryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件查询服务集成测试 - 基于真实文件系统
 * 测试 FileQueryService 与真实文件系统的完整交互流程
 */
class FileQueryServiceIntegrationTest {

    private static FileQueryService fileQueryService;
    private static String testDataPath;
    private static String emptyFolderPath;

    @BeforeAll
    static void setUp() throws IOException {
        // 创建真实的依赖对象
        FileSystemRepository fileSystemRepository = new FileSystemRepository();
        fileQueryService = new FileQueryService(fileSystemRepository);
        
        // 获取测试资源目录的绝对路径
        ClassPathResource testDataResource = new ClassPathResource("test-data");
        ClassPathResource emptyFolderResource = new ClassPathResource("empty-folder");
        
        testDataPath = testDataResource.getFile().getAbsolutePath();
        emptyFolderPath = emptyFolderResource.getFile().getAbsolutePath();
    }

    /**
     * 测试真实目录文件列表查询 - 成功场景
     */
    @Test
    void testListDirectoryFiles_RealDirectory_Success() {
        // 执行测试
        List<FileInfo> fileList = fileQueryService.listDirectoryFiles(testDataPath);

        // 验证结果
        assertNotNull(fileList);
        assertTrue(fileList.size() >= 4); // 至少包含我们创建的4个文件和1个子目录
        
        // 验证具体文件
        boolean hasConfigFile = fileList.stream()
            .anyMatch(file -> "config.properties".equals(file.getName()));
        boolean hasJavaFile = fileList.stream()
            .anyMatch(file -> "sample.java".equals(file.getName()));
        boolean hasSubfolder = fileList.stream()
            .anyMatch(file -> "subfolder".equals(file.getName()) && file.isDirectory());
        
        assertTrue(hasConfigFile, "应该包含config.properties文件");
        assertTrue(hasJavaFile, "应该包含sample.java文件");
        assertTrue(hasSubfolder, "应该包含subfolder子目录");
        
        // 验证文件详细信息
        FileInfo configFile = fileList.stream()
            .filter(file -> "config.properties".equals(file.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(configFile);
        assertEquals("properties", configFile.getType());
        assertFalse(configFile.isDirectory());
        assertTrue(configFile.getSize() > 0);
        assertNotNull(configFile.getLastModified());
    }

    /**
     * 测试查询空目录
     */
    @Test
    void testListDirectoryFiles_EmptyDirectory() {
        // 执行测试
        List<FileInfo> fileList = fileQueryService.listDirectoryFiles(emptyFolderPath);

        // 验证结果
        assertNotNull(fileList);
        assertTrue(fileList.isEmpty(), "空目录应该返回空列表");
    }

    /**
     * 测试目录路径参数验证
     */
    @Test
    void testListDirectoryFiles_ParameterValidation() {
        // 测试null参数
        assertThrows(IllegalArgumentException.class, () -> 
            fileQueryService.listDirectoryFiles(null),
            "null路径应该抛出IllegalArgumentException");
        
        // 测试空字符串参数
        assertThrows(IllegalArgumentException.class, () -> 
            fileQueryService.listDirectoryFiles(""),
            "空路径应该抛出IllegalArgumentException");
        
        // 测试空白字符串参数
        assertThrows(IllegalArgumentException.class, () -> 
            fileQueryService.listDirectoryFiles("   "),
            "空白路径应该抛出IllegalArgumentException");
    }

    /**
     * 测试查询真实文件详情 - 成功场景
     */
    @Test
    void testGetFileDetails_RealFile_Success() {
        String configFilePath = testDataPath + "/config.properties";
        
        // 执行测试
        Optional<FileInfo> fileInfo = fileQueryService.getFileDetails(configFilePath);

        // 验证结果
        assertTrue(fileInfo.isPresent(), "真实文件应该返回文件信息");
        
        FileInfo file = fileInfo.get();
        assertEquals("config.properties", file.getName());
        assertEquals("properties", file.getType());
        assertFalse(file.isDirectory());
        assertTrue(file.getSize() > 0);
        assertNotNull(file.getLastModified());
        assertTrue(file.getPath().endsWith("config.properties"));
    }

    /**
     * 测试查询真实目录详情
     */
    @Test
    void testGetFileDetails_RealDirectory() {
        String subfolderPath = testDataPath + "/subfolder";
        
        // 执行测试
        Optional<FileInfo> fileInfo = fileQueryService.getFileDetails(subfolderPath);

        // 验证结果
        assertTrue(fileInfo.isPresent(), "真实目录应该返回目录信息");
        
        FileInfo directory = fileInfo.get();
        assertEquals("subfolder", directory.getName());
        assertEquals("directory", directory.getType());
        assertTrue(directory.isDirectory());
        assertEquals(0, directory.getSize());
        assertNotNull(directory.getLastModified());
    }

    /**
     * 测试查询不存在的文件
     */
    @Test
    void testGetFileDetails_NonExistentFile() {
        String nonExistentFilePath = testDataPath + "/non-existent-file.txt";
        
        // 执行测试
        Optional<FileInfo> fileInfo = fileQueryService.getFileDetails(nonExistentFilePath);

        // 验证结果
        assertFalse(fileInfo.isPresent(), "不存在的文件应该返回空结果");
    }

    /**
     * 测试文件路径参数验证
     */
    @Test
    void testGetFileDetails_ParameterValidation() {
        // 测试null参数
        assertThrows(IllegalArgumentException.class, () -> 
            fileQueryService.getFileDetails(null),
            "null路径应该抛出IllegalArgumentException");
        
        // 测试空字符串参数
        assertThrows(IllegalArgumentException.class, () -> 
            fileQueryService.getFileDetails(""),
            "空路径应该抛出IllegalArgumentException");
        
        // 测试空白字符串参数
        assertThrows(IllegalArgumentException.class, () -> 
            fileQueryService.getFileDetails("   "),
            "空白路径应该抛出IllegalArgumentException");
    }

    /**
     * 测试子目录文件查询
     */
    @Test
    void testListDirectoryFiles_Subfolder() {
        String subfolderPath = testDataPath + "/subfolder";
        
        // 执行测试
        List<FileInfo> fileList = fileQueryService.listDirectoryFiles(subfolderPath);

        // 验证结果
        assertNotNull(fileList);
        assertTrue(fileList.size() >= 1); // 至少包含nested-file.txt
        
        // 验证是否包含嵌套文件
        boolean hasNestedFile = fileList.stream()
            .anyMatch(file -> "nested-file.txt".equals(file.getName()));
        assertTrue(hasNestedFile, "子目录应该包含nested-file.txt");
        
        // 验证嵌套文件详情
        FileInfo nestedFile = fileList.stream()
            .filter(file -> "nested-file.txt".equals(file.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(nestedFile);
        assertEquals("txt", nestedFile.getType());
        assertFalse(nestedFile.isDirectory());
        assertTrue(nestedFile.getSize() > 0);
    }

    /**
     * 测试各种文件类型的识别
     */
    @Test
    void testFileTypeRecognition() {
        // 执行测试
        List<FileInfo> fileList = fileQueryService.listDirectoryFiles(testDataPath);

        // 验证Java文件类型
        Optional<FileInfo> javaFile = fileList.stream()
            .filter(file -> "sample.java".equals(file.getName()))
            .findFirst();
        assertTrue(javaFile.isPresent());
        assertEquals("java", javaFile.get().getType());
        
        // 验证Properties文件类型
        Optional<FileInfo> propertiesFile = fileList.stream()
            .filter(file -> "config.properties".equals(file.getName()))
            .findFirst();
        assertTrue(propertiesFile.isPresent());
        assertEquals("properties", propertiesFile.get().getType());
        
        // 验证目录类型
        Optional<FileInfo> directory = fileList.stream()
            .filter(file -> "subfolder".equals(file.getName()))
            .findFirst();
        assertTrue(directory.isPresent());
        assertEquals("directory", directory.get().getType());
        assertTrue(directory.get().isDirectory());
    }
}