package com.example.filequery.integration;

import com.example.filequery.controller.FileQueryController;
import com.example.filequery.domain.FileInfo;
import com.example.filequery.infrastructure.FileSystemRepository;
import com.example.filequery.service.FileQueryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件查询控制器集成测试 - 基于真实文件系统
 * 测试完整的请求处理流程：Controller -> Service -> Repository -> FileSystem
 */
class FileQueryControllerIntegrationTest {

    private static FileQueryController fileQueryController;
    private static String testDataPath;
    private static String emptyFolderPath;

    @BeforeAll
    static void setUp() throws IOException {
        // 创建完整的依赖链：Controller -> Service -> Repository
        FileSystemRepository fileSystemRepository = new FileSystemRepository();
        FileQueryService fileQueryService = new FileQueryService(fileSystemRepository);
        fileQueryController = new FileQueryController(fileQueryService);
        
        // 获取测试资源目录的绝对路径
        ClassPathResource testDataResource = new ClassPathResource("test-data");
        ClassPathResource emptyFolderResource = new ClassPathResource("empty-folder");
        
        testDataPath = testDataResource.getFile().getAbsolutePath();
        emptyFolderPath = emptyFolderResource.getFile().getAbsolutePath();
    }

    /**
     * 测试文件列表查询接口 - 成功场景（真实文件）
     */
    @Test
    void testListFiles_RealDirectory_Success() {
        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(testDataPath);

        // 验证HTTP响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        List<FileInfo> fileList = response.getBody();
        assertTrue(fileList.size() >= 4); // 至少包含我们创建的4个文件和1个子目录
        
        // 验证具体文件内容
        boolean hasConfigFile = fileList.stream()
            .anyMatch(file -> "config.properties".equals(file.getName()));
        boolean hasJavaFile = fileList.stream()
            .anyMatch(file -> "sample.java".equals(file.getName()));
        boolean hasSubfolder = fileList.stream()
            .anyMatch(file -> "subfolder".equals(file.getName()) && file.isDirectory());
        
        assertTrue(hasConfigFile, "响应应该包含config.properties文件");
        assertTrue(hasJavaFile, "响应应该包含sample.java文件");
        assertTrue(hasSubfolder, "响应应该包含subfolder子目录");
        
        // 验证文件详细信息的正确性
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
     * 测试文件列表查询接口 - 空目录场景
     */
    @Test
    void testListFiles_EmptyDirectory() {
        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(emptyFolderPath);

        // 验证HTTP响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty(), "空目录应该返回空列表");
    }

    /**
     * 测试文件列表查询接口 - 无效参数场景
     */
    @Test
    void testListFiles_InvalidParameter() {
        // 测试空字符串参数
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles("");

        // 验证HTTP响应
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * 测试文件信息查询接口 - 成功场景（真实文件）
     */
    @Test
    void testGetFileInfo_RealFile_Success() {
        String configFilePath = testDataPath + "/config.properties";
        
        // 执行测试
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(configFilePath);

        // 验证HTTP响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        FileInfo fileInfo = response.getBody();
        assertEquals("config.properties", fileInfo.getName());
        assertEquals("properties", fileInfo.getType());
        assertFalse(fileInfo.isDirectory());
        assertTrue(fileInfo.getSize() > 0);
        assertNotNull(fileInfo.getLastModified());
        assertTrue(fileInfo.getPath().endsWith("config.properties"));
    }

    /**
     * 测试文件信息查询接口 - 真实目录
     */
    @Test
    void testGetFileInfo_RealDirectory() {
        String subfolderPath = testDataPath + "/subfolder";
        
        // 执行测试
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(subfolderPath);

        // 验证HTTP响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        FileInfo directoryInfo = response.getBody();
        assertEquals("subfolder", directoryInfo.getName());
        assertEquals("directory", directoryInfo.getType());
        assertTrue(directoryInfo.isDirectory());
        assertEquals(0, directoryInfo.getSize());
        assertNotNull(directoryInfo.getLastModified());
    }

    /**
     * 测试文件信息查询接口 - 文件不存在场景
     */
    @Test
    void testGetFileInfo_FileNotFound() {
        String nonExistentFilePath = testDataPath + "/non-existent-file.txt";
        
        // 执行测试
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(nonExistentFilePath);

        // 验证HTTP响应
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * 测试文件信息查询接口 - 无效参数场景
     */
    @Test
    void testGetFileInfo_InvalidParameter() {
        // 测试null参数
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(null);

        // 验证HTTP响应
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * 测试子目录文件查询的完整流程
     */
    @Test
    void testListFiles_Subfolder_CompleteFlow() {
        String subfolderPath = testDataPath + "/subfolder";
        
        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(subfolderPath);

        // 验证HTTP响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        List<FileInfo> fileList = response.getBody();
        assertTrue(fileList.size() >= 1); // 至少包含nested-file.txt
        
        // 验证嵌套文件
        boolean hasNestedFile = fileList.stream()
            .anyMatch(file -> "nested-file.txt".equals(file.getName()));
        assertTrue(hasNestedFile, "子目录应该包含nested-file.txt");
        
        // 获取嵌套文件的详细信息
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
     * 测试各种文件类型在完整流程中的识别
     */
    @Test
    void testFileTypeRecognition_CompleteFlow() {
        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(testDataPath);

        // 验证HTTP响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        List<FileInfo> fileList = response.getBody();
        
        // 验证Java文件类型识别
        FileInfo javaFile = fileList.stream()
            .filter(file -> "sample.java".equals(file.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(javaFile, "应该包含sample.java文件");
        assertEquals("java", javaFile.getType());
        assertFalse(javaFile.isDirectory());
        
        // 验证Properties文件类型识别
        FileInfo propertiesFile = fileList.stream()
            .filter(file -> "config.properties".equals(file.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(propertiesFile, "应该包含config.properties文件");
        assertEquals("properties", propertiesFile.getType());
        assertFalse(propertiesFile.isDirectory());
        
        // 验证目录类型识别
        FileInfo directory = fileList.stream()
            .filter(file -> "subfolder".equals(file.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(directory, "应该包含subfolder目录");
        assertEquals("directory", directory.getType());
        assertTrue(directory.isDirectory());
    }

    /**
     * 测试不存在目录的处理
     */
    @Test
    void testListFiles_NonExistentDirectory() {
        String nonExistentPath = testDataPath + "/non-existent-folder";
        
        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(nonExistentPath);

        // 验证HTTP响应 - 不存在的目录应该返回空列表而不是错误
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty(), "不存在的目录应该返回空列表");
    }
}