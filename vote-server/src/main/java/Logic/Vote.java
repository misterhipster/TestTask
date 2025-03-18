package Logic;

import java.util.ArrayList;
import java.util.List;

public class Vote {
        private String name;
        private String voteCreatorName;
        private String description;
        private int optionsCount;
        private List<String> options;
        private List<Integer> numOfVotedUsers;

    public Vote(String name, String description, List<String> options, String voteCreatorName) {
        this.name = name;
        this.description = description;
        this.voteCreatorName = voteCreatorName;
        numOfVotedUsers = new ArrayList<>();

        this.options = new ArrayList<>();
        for (String str: options){
            this.options.add(str);
            this.numOfVotedUsers.add(0);
        }

    }

    public String getVoteCreatorName() {
        return voteCreatorName;
    }

    public List<Integer> getNumOfVotedUsers() {
        return numOfVotedUsers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOptionsCount(int optionsCount) {
        this.optionsCount = optionsCount;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getOptionsCount() {
        return optionsCount;
    }

    public List<String> getOptions() {
        return options;
    }
}
