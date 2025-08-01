package com.example.filequery.service;

import com.example.filequery.domain.FileInfo;
import com.example.filequery.infrastructure.FileSystemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 文件查询服务测试类
 */
class FileQueryServiceTest {

    @Mock
    private FileSystemRepository fileSystemRepository;

    @InjectMocks
    private FileQueryService fileQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试场景1：正常查询目录文件
    @Test
    void testListDirectoryFiles_Success() {
        // 准备测试数据
        String directoryPath = "/test/directory";
        List<FileInfo> mockFileList = Arrays.asList(
            new FileInfo("document.pdf", "pdf", false, 5120, LocalDateTime.now(), "/test/directory/document.pdf"),
            new FileInfo("image.png", "png", false, 2048, LocalDateTime.now(), "/test/directory/image.png"),
            new FileInfo("subfolder", "directory", true, 0, LocalDateTime.now(), "/test/directory/subfolder")
        );
        
        when(fileSystemRepository.listFiles(directoryPath)).thenReturn(mockFileList);

        // 执行测试
        List<FileInfo> result = fileQueryService.listDirectoryFiles(directoryPath);

        // 验证结果
        assertEquals(3, result.size());
        assertEquals("document.pdf", result.get(0).getName());
        assertEquals("image.png", result.get(1).getName());
        assertEquals("subfolder", result.get(2).getName());
        assertTrue(result.get(2).isDirectory());
        verify(fileSystemRepository, times(1)).listFiles(directoryPath);
    }

    // 测试场景2：查询空目录
    @Test
    void testListDirectoryFiles_EmptyDirectory() {
        // 准备测试数据
        String directoryPath = "/empty/directory";
        
        when(fileSystemRepository.listFiles(directoryPath)).thenReturn(Collections.emptyList());

        // 执行测试
        List<FileInfo> result = fileQueryService.listDirectoryFiles(directoryPath);

        // 验证结果
        assertTrue(result.isEmpty());
        verify(fileSystemRepository, times(1)).listFiles(directoryPath);
    }

    // 测试场景3：目录路径为空时抛出异常
    @Test
    void testListDirectoryFiles_EmptyPath() {
        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            fileQueryService.listDirectoryFiles("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fileQueryService.listDirectoryFiles(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fileQueryService.listDirectoryFiles("   ");
        });
    }

    // 测试场景4：正常查询文件详情
    @Test
    void testGetFileDetails_Success() {
        // 准备测试数据
        String filePath = "/test/config.properties";
        FileInfo mockFileInfo = new FileInfo("config.properties", "properties", false, 512, LocalDateTime.now(), filePath);
        
        when(fileSystemRepository.getFileInfo(filePath)).thenReturn(Optional.of(mockFileInfo));

        // 执行测试
        Optional<FileInfo> result = fileQueryService.getFileDetails(filePath);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("config.properties", result.get().getName());
        assertEquals("properties", result.get().getType());
        assertFalse(result.get().isDirectory());
        verify(fileSystemRepository, times(1)).getFileInfo(filePath);
    }

    // 测试场景5：查询不存在的文件
    @Test
    void testGetFileDetails_FileNotFound() {
        // 准备测试数据
        String filePath = "/nonexistent/file.txt";
        
        when(fileSystemRepository.getFileInfo(filePath)).thenReturn(Optional.empty());

        // 执行测试
        Optional<FileInfo> result = fileQueryService.getFileDetails(filePath);

        // 验证结果
        assertFalse(result.isPresent());
        verify(fileSystemRepository, times(1)).getFileInfo(filePath);
    }

    // 测试场景6：文件路径为空时抛出异常
    @Test
    void testGetFileDetails_EmptyPath() {
        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            fileQueryService.getFileDetails("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fileQueryService.getFileDetails(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fileQueryService.getFileDetails("   ");
        });
    }
}