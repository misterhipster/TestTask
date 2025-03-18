package Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class VoteTest {
    private Vote vote;

    @BeforeEach
    void setUp() {
        vote = new Vote("Vote 1", "Description", List.of("Option 1", "Option 2"), "Creator");
    }

    @Test
    void testGetName() {
        assertEquals("Vote 1", vote.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Description", vote.getDescription());
    }

    @Test
    void testGetOptions() {
        List<String> options = vote.getOptions();
        assertEquals(2, options.size());
        assertTrue(options.contains("Option 1"));
        assertTrue(options.contains("Option 2"));
    }

    @Test
    void testGetNumOfVotedUsers() {
        List<Integer> numOfVotedUsers = vote.getNumOfVotedUsers();
        assertEquals(2, numOfVotedUsers.size());
        assertEquals(0, numOfVotedUsers.get(0)); // Assuming no votes have been cast
    }
}
