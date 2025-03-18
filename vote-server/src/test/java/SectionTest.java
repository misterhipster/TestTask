package Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SectionTest {
    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section("Section 1");
    }

    @Test
    void testAddVote() {
        Vote vote = new Vote("Vote 1", "Description", List.of("Option 1", "Option 2"), "Creator");
        section.addVote(vote);

        assertEquals(1, section.getVoteList().size());
        assertEquals("Vote 1", section.getVoteList().get(0).getName());
    }

    @Test
    void testDeleteVote() {
        Vote vote = new Vote("Vote 1", "Description", List.of("Option 1", "Option 2"), "Creator");
        section.addVote(vote);

        section.deleteVote("Vote 1");
        assertEquals(0, section.getVoteList().size());
    }

    @Test
    void testDeleteNonexistentVote() {
        Vote vote = new Vote("Vote 1", "Description", List.of("Option 1", "Option 2"), "Creator");
        section.addVote(vote);

        section.deleteVote("Nonexistent Vote");
        assertEquals(1, section.getVoteList().size());
    }
}
