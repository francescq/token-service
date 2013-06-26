package com.workshare.micro.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MicroConfigServiceTest {

    private String userHome;
    
    @Before
    public void setup() {
        userHome = System.getProperty("user.home");
        System.setProperty("user.home", System.getProperty("java.io.tmpdir"));
    }

    @After
    public void teardown() {
        System.setProperty("user.home", userHome);
    }

    @Test
    public void shouldCreateFileWithDefaultsIfMissing() {
        cleanup();
        
        MicroConfigService.load();
        
        assertTrue(MicroConfigService.configFile().exists());
    }

    private void cleanup() {
        File configFile = MicroConfigService.configFile();
        File configfolder = configFile.getParentFile();
        
        configFile.delete();
        configfolder.delete();
    }
}
