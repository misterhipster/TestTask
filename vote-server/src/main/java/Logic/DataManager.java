package Logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    List<Section> sections;

    public DataManager() {
        sections = new ArrayList<>();
    }

    public void createSection(String sectionName) {
        Section section = new Section(sectionName);
        sections.add(section);
    }

    public void createVote(String sectionName, String voteName, String descr, List<String> options, String voteCreatorName) {
        Vote vote = new Vote(voteName, descr, options, voteCreatorName);
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getSectionName().equals(sectionName)) {
                sections.get(i).addVote(vote);
                break;
            }
        }
    }

    public boolean VoteExist(String voteName){
        for(Section section:sections){
            for(Vote vote:section.getVoteList()){
                if (vote.getName().equals(voteName)){
                    return true;
                }
            }
        }
        return false;
    }

    public String getSectionsStringLikeJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(sections);
//        System.out.println(json); // Выводим JSON в консоль
        return json;
    }

    public List<Section> getSections() {
        return sections;
    }
}
