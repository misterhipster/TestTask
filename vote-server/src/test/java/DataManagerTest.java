import Logic.DataManager;
import Logic.Section;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataManagerTest {
    private DataManager dataManager;

    @BeforeEach
    void setUp() {
        dataManager = new DataManager();
    }

    @Test
    void testCreateSection() {
        dataManager.createSection("Section 1");
        List<Section> sections = dataManager.getSections();
        assertEquals(1, sections.size());
        assertEquals("Section 1", sections.get(0).getSectionName());
    }

    @Test
    void testCreateVote() {
        dataManager.createSection("Section 1");
        dataManager.createVote("Section 1", "Vote 1", "Description 1", List.of("Option 1", "Option 2"), "Creator");

        Section section = dataManager.getSections().get(0);
        assertEquals(1, section.getVoteList().size());
        assertEquals("Vote 1", section.getVoteList().get(0).getName());
    }

    @Test
    void testVoteExist() {
        dataManager.createSection("Section 1");
        dataManager.createVote("Section 1", "Vote 1", "Description 1", List.of("Option 1", "Option 2"), "Creator");

        assertTrue(dataManager.VoteExist("Vote 1"));
        assertFalse(dataManager.VoteExist("Nonexistent Vote"));
    }

    @Test
    void testGetSectionsStringLikeJson() {
        dataManager.createSection("Section 1");
        String json = dataManager.getSectionsStringLikeJson();
        assertTrue(json.contains("\"sectionName\": \"Section 1\""));
    }
}
