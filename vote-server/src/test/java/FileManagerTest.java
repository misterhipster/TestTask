package Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileManagerTest {
    private DataManager dataManager;
    private FileManager fileManager;
    private final String filename = "test.json";

    @BeforeEach
    void setUp() {
        dataManager = new DataManager();
        fileManager = new FileManager(dataManager);
    }

    @Test
    void testSaveFile() {
        dataManager.createSection("Section 1");
        fileManager.saveFile(filename);

        File file = new File(filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void testLoadFile() {
        dataManager.createSection("Section 1");
        fileManager.saveFile(filename);

        dataManager.getSections().clear();
        fileManager.loadFile(filename);

        List<Section> sections = dataManager.getSections();
        assertEquals(1, sections.size());
        assertEquals("Section 1", sections.get(0).getSectionName());
    }

}
