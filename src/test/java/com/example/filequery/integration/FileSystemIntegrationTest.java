package com.example.filequery.integration;

import com.example.filequery.domain.FileInfo;
import com.example.filequery.infrastructure.FileSystemRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件系统集成测试 - 基于真实文件系统
 * 测试 FileSystemRepository 与真实文件系统的交互
 */
class FileSystemIntegrationTest {

    private static FileSystemRepository fileSystemRepository;
    private static String testDataPath;
    private static String emptyFolderPath;

    @BeforeAll
    static void setUp() throws IOException {
        fileSystemRepository = new FileSystemRepository();
        
        // 获取测试资源目录的绝对路径
        ClassPathResource testDataResource = new ClassPathResource("test-data");
        ClassPathResource emptyFolderResource = new ClassPathResource("empty-folder");
        
        testDataPath = testDataResource.getFile().getAbsolutePath();
        emptyFolderPath = emptyFolderResource.getFile().getAbsolutePath();
    }

    /**
     * 测试查询真实目录中的文件列表
     */
    @Test
    void testListFiles_RealDirectory() {
        // 执行测试
        List<FileInfo> fileList = fileSystemRepository.listFiles(testDataPath);

        // 验证结果
        assertNotNull(fileList);
        assertTrue(fileList.size() >= 4); // 至少包含我们创建的4个文件和1个子目录
        
        // 验证是否包含我们创建的文件
        boolean hasConfigProperties = fileList.stream()
            .anyMatch(file -> "config.properties".equals(file.getName()));
        boolean hasSampleJava = fileList.stream()
            .anyMatch(file -> "sample.java".equals(file.getName()));
        boolean hasSubfolder = fileList.stream()
            .anyMatch(file -> "subfolder".equals(file.getName()) && file.isDirectory());
        
        assertTrue(hasConfigProperties, "应该包含 config.properties 文件");
        assertTrue(hasSampleJava, "应该包含 sample.java 文件");
        assertTrue(hasSubfolder, "应该包含 subfolder 子目录");
        
        // 验证文件类型识别
        Optional<FileInfo> configFile = fileList.stream()
            .filter(file -> "config.properties".equals(file.getName()))
            .findFirst();
        assertTrue(configFile.isPresent());
        assertEquals("properties", configFile.get().getType());
        assertFalse(configFile.get().isDirectory());
        assertTrue(configFile.get().getSize() > 0);
        
        // 验证Java文件类型识别
        Optional<FileInfo> javaFile = fileList.stream()
            .filter(file -> "sample.java".equals(file.getName()))
            .findFirst();
        assertTrue(javaFile.isPresent());
        assertEquals("java", javaFile.get().getType());
        assertFalse(javaFile.get().isDirectory());
        assertTrue(javaFile.get().getSize() > 0);
    }

    /**
     * 测试查询空目录
     */
    @Test
    void testListFiles_EmptyDirectory() {
        // 执行测试
        List<FileInfo> fileList = fileSystemRepository.listFiles(emptyFolderPath);

        // 验证结果
        assertNotNull(fileList);
        assertTrue(fileList.isEmpty(), "空目录应该返回空列表");
    }

    /**
     * 测试查询不存在的目录
     */
    @Test
    void testListFiles_NonExistentDirectory() {
        String nonExistentPath = testDataPath + "/non-existent-folder";
        
        // 执行测试
        List<FileInfo> fileList = fileSystemRepository.listFiles(nonExistentPath);

        // 验证结果
        assertNotNull(fileList);
        assertTrue(fileList.isEmpty(), "不存在的目录应该返回空列表");
    }

    /**
     * 测试获取真实文件信息
     */
    @Test
    void testGetFileInfo_RealFile() {
        String configFilePath = testDataPath + "/config.properties";
        
        // 执行测试
        Optional<FileInfo> fileInfo = fileSystemRepository.getFileInfo(configFilePath);

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
     * 测试获取目录信息
     */
    @Test
    void testGetFileInfo_Directory() {
        String subfolderPath = testDataPath + "/subfolder";
        
        // 执行测试
        Optional<FileInfo> fileInfo = fileSystemRepository.getFileInfo(subfolderPath);

        // 验证结果
        assertTrue(fileInfo.isPresent(), "真实目录应该返回目录信息");
        
        FileInfo directory = fileInfo.get();
        assertEquals("subfolder", directory.getName());
        assertEquals("directory", directory.getType());
        assertTrue(directory.isDirectory());
        assertEquals(0, directory.getSize()); // 目录大小为0
        assertNotNull(directory.getLastModified());
        assertTrue(directory.getPath().endsWith("subfolder"));
    }

    /**
     * 测试获取不存在文件的信息
     */
    @Test
    void testGetFileInfo_NonExistentFile() {
        String nonExistentFilePath = testDataPath + "/non-existent-file.txt";
        
        // 执行测试
        Optional<FileInfo> fileInfo = fileSystemRepository.getFileInfo(nonExistentFilePath);

        // 验证结果
        assertFalse(fileInfo.isPresent(), "不存在的文件应该返回空结果");
    }

    /**
     * 测试文件扩展名识别功能
     */
    @Test
    void testFileExtensionRecognition() {
        // 执行测试
        List<FileInfo> fileList = fileSystemRepository.listFiles(testDataPath);

        // 验证不同类型文件的扩展名识别
        Optional<FileInfo> pdfFile = fileList.stream()
            .filter(file -> file.getName().contains("document.pdf"))
            .findFirst();
        if (pdfFile.isPresent()) {
            assertEquals("txt", pdfFile.get().getType()); // 实际是.txt文件
        }

        Optional<FileInfo> pngFile = fileList.stream()
            .filter(file -> file.getName().contains("image.png"))
            .findFirst();
        if (pngFile.isPresent()) {
            assertEquals("txt", pngFile.get().getType()); // 实际是.txt文件
        }

        // 验证properties文件
        Optional<FileInfo> propertiesFile = fileList.stream()
            .filter(file -> "config.properties".equals(file.getName()))
            .findFirst();
        assertTrue(propertiesFile.isPresent());
        assertEquals("properties", propertiesFile.get().getType());
    }
}