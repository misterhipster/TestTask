package Logic;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private String sectionName;
    List<Vote> voteList;

    public Section(String sectionName) {
        this.sectionName = sectionName;
        voteList = new ArrayList<>();
    }

    public void addVote(Vote vote) {
        voteList.add(vote);
    }

    public List<Vote> getVoteList() {
        return voteList;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void deleteVote(String voteName){
        int i=0;
        for (Vote vote:voteList){
            if (vote.getName().equals(voteName)){
                voteList.remove(i);
               break;
            }
            i++;
        }

    }
}
