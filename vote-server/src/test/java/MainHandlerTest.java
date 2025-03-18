import Logic.DataManager;
import Logic.FileManager;
import Logic.Section;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainHandlerTest {
    private DataManager dataManager;
    private FileManager fileManager;

    @Test
    public void testSaveFile() throws Exception {
        dataManager.createSection("TestSection");
        fileManager.saveFile("test.json");
        // Verify that the file is written correctly (could check the contents of the file or mock file operations).
    }

    @Test
    public void testLoadFile() throws Exception {
        fileManager.loadFile("test.json");
        assertFalse(dataManager.getSections().isEmpty());
    }

}
