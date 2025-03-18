import Logic.DataManager;
import Logic.Section;
import Logic.Vote;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.xml.crypto.Data;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.logging.Logger.global;

// Inbound - работаем на вход
//public class MainHandler extends ChannelInboundHandlerAdapter {
public class MainHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>();
    private static int newClientIndex = 1;
    private String username;
    private static boolean isLogged = false;
    private static DataManager dataManager;
    private static short phaseOfTheCreatingVote;
    private static String sectionNameForCreatingVote;
    private static String voteName;
    private static String description;
    private static int numOfOptions;

    private static List<String> options;
    private static List<Integer> numOfVotedUsers;
    private int globalIndex;

    public MainHandler(DataManager dataManager) {
        voteName = null;
        description = null;
        sectionNameForCreatingVote = null;
        numOfOptions = -1;
        phaseOfTheCreatingVote = 0;
        globalIndex = 0;
        this.options = new ArrayList<>();
        numOfVotedUsers = new ArrayList<>();
        this.dataManager = dataManager;
    }

    // Когда клиент подключаетя то срабатывает channelActive
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client now connected " + ctx);
        channels.add(ctx.channel());
        username = "user №" + newClientIndex;
        newClientIndex++;
    }

    // срабатывает кода клиент приислал сообщение
    // у Netty правило - то что пришло из сети и пришло в первый Handler заворачивается в ByteBuffer
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf byteBuf = (ByteBuf) msg;
//        while (byteBuf.readableBytes() > 0) {
//            System.out.print((char) byteBuf.readByte());
//        }
//        byteBuf.release();
//    }

    // отлично можно получать стринги от клиентов
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String string) throws Exception {
//        String out = String.format("[%s]: %s\n", username, string);
//        System.out.println(out);
//        for (Channel c : channels) {
//            c.writeAndFlush(out);
//        }
        System.out.println("Getted message from [" + username + "]: " + string);
        logMessage(username, string);
        Channel chnl = channelHandlerContext.channel();
        if (string.equalsIgnoreCase("exit")) {
            chnl.writeAndFlush("Goodbye!\n");
            chnl.close();
            channels.remove(chnl);
            System.out.println("User " + username + " disconnected.");
        }
        if (!isLogged) {
            if (string.startsWith("login -u=")) {
                String tmpUsername = string.substring("login -u=".length());
                username = tmpUsername;
//                System.out.println("Great! Now you are " + username);
                chnl.writeAndFlush("Great! Now you are " + username);
                isLogged = true;
            } else {
//                System.out.println("You need to be login user command: /login <username>");
                chnl.writeAndFlush("You need to be login user command: login -u=<username>");
            }
        } else {
            // создание новго раздела с уникальным именем
            if (string.startsWith("create topic -n=") && (string.length() > "create topic -n=".length())) {
                String sectionName = string.substring("create topic -n=".length());
                dataManager.createSection(sectionName);
                chnl.writeAndFlush("Section " + sectionName + " succesfully created!");
            }
            else if (string.equals("view")) {
//            String out = String.format("[%s]: %s\n", username, string);
                chnl.writeAndFlush(dataManager.getSectionsStringLikeJson());
            } else if (string.startsWith("create vote -t=") && (string.length() > "create vote -t=".length())) {
                String sectionName = string.substring("create vote -t=".length());
                List<Section> sections = dataManager.getSections();
                Section targerSection = null;
                for (Section s : sections) {
                    if (sectionName.equals(s.getSectionName())) {
                        targerSection = s;
                    }
                }
                if (targerSection != null) {
                    chnl.writeAndFlush("Ok, now enter name of the vote: ");
                    sectionNameForCreatingVote = targerSection.getSectionName();
                    phaseOfTheCreatingVote = 1;

                } else {
                    chnl.writeAndFlush("This section does not exist");
                }
            }
            else if (phaseOfTheCreatingVote > 0) {
                // фаза 1 - зарпос названия
                if (phaseOfTheCreatingVote == 1) {
                    voteName = string;
                    chnl.writeAndFlush("votename: " + voteName);
                    phaseOfTheCreatingVote = 2;
                    chnl.writeAndFlush("What a description of the vote: ");
                }
                // фаза 2 - какое описание
                else if (phaseOfTheCreatingVote == 2) {
                    description = string;
                    chnl.writeAndFlush("description: " + description);
                    chnl.writeAndFlush("Enter a number of options in the vote (more than 0, but not more 10): ");
                    phaseOfTheCreatingVote = 3;
                }
                // фаза 3 - сколько вариантов ответа
                else if (phaseOfTheCreatingVote == 3) {
                    if (isNumeric(string)) {
                        numOfOptions = Integer.parseInt(string);
                        if (numOfOptions > 0 && numOfOptions < 10) {
                            chnl.writeAndFlush("Enter all you arguments");
                            phaseOfTheCreatingVote = 4;
                        }
                    } else {
                        chnl.writeAndFlush("Incorrect datas");
                    }
                } else if (phaseOfTheCreatingVote == 4) {
                    String option = (globalIndex + 1) + ". " + string;
                    options.add(option);
                    chnl.writeAndFlush(option + " was added");
                    globalIndex++;
                    if (globalIndex >= numOfOptions) {
                        dataManager.createVote(sectionNameForCreatingVote, voteName, description, options, username);
                        chnl.writeAndFlush("Vote completely created");
                        phaseOfTheCreatingVote = 0;
                    }
                }
            } else if (string.startsWith("view -t=") && string.contains(" -v=")) {
                String[] parts = string.split(" -v=");
                if (parts.length < 2) {
                    chnl.writeAndFlush("Invalid command format. Use: view -t=<section> -v=<vote>");
                } else {
                    String sectionName = parts[0].substring("view -t=".length()).trim();
                    String voteName = parts[1].trim();

                    Section targetSection = null;
                    for (Section section : dataManager.getSections()) {
                        if (section.getSectionName().equals(sectionName)) {
                            targetSection = section;
                            break;
                        }
                    }

                    if (targetSection != null) {
                        Vote targetVote = null;
                        for (Vote vote : targetSection.getVoteList()) {
                            if (vote.getName().equals(voteName)) {
                                targetVote = vote;
                                break;
                            }
                        }

                        if (targetVote != null) {
                            StringBuilder voteInfo = new StringBuilder();
                            voteInfo.append("Vote Name: ").append(targetVote.getName()).append("\n");
                            voteInfo.append("Description: ").append(targetVote.getDescription()).append("\n");
                            voteInfo.append("Options: \n");
                            List<String> options = targetVote.getOptions();
                            for (int i = 0; i < options.size(); i++) {
                                voteInfo.append(i + 1).append(") ").append(options.get(i)).append("\n");
                            }
                            chnl.writeAndFlush(voteInfo.toString());
                        } else {
                            chnl.writeAndFlush("Vote '" + voteName + "' not found in section '" + sectionName + "'.");
                        }
                    } else {
                        chnl.writeAndFlush("Section '" + sectionName + "' not found.");
                    }
                }
            }
            else if (string.startsWith("vote -t=") && string.contains(" -v=")) {
                String[] parts = string.split(" -v=");
                if (parts.length == 2) {
                    String tmpSectionName = parts[0].substring("vote -t=".length());
                    String tmpVoteName = parts[1];
                    chnl.writeAndFlush(tmpSectionName + " " + tmpVoteName);
                    List<Section> tmpSections = dataManager.getSections();
                    Section targetSection = null;
                    Vote targetVote = null;
                    for (Section section : tmpSections) {
                        if (section.getSectionName().equals(tmpSectionName)) {
                            List<Vote> tmpVotes = section.getVoteList();
                            for (Vote vote : tmpVotes) {
                                if (vote.getName().equals(tmpVoteName)) {
                                    targetSection = section;
                                    targetVote = vote;
                                    break;
                                }
                            }
                            break;
                        }

                    }
                    if (targetSection != null && targetVote != null) {
                        List<String> tmpOptions = targetVote.getOptions();

                        for (String str : tmpOptions) {
                            chnl.writeAndFlush(str + "\n");
                        }
                        chnl.writeAndFlush("Enter a number of option: ");
                        options = targetVote.getOptions();
                        numOfVotedUsers = targetVote.getNumOfVotedUsers();
                        phaseOfTheCreatingVote = -1;
                    } else {
                        chnl.writeAndFlush("Error:");
                        if (targetSection == null) {
                            chnl.writeAndFlush("Section not found");
                        }
                        if (targetVote == null) {
                            chnl.writeAndFlush("Vote not found");
                        }
                    }

                } else {
                    chnl.writeAndFlush("Invalid command format. Use: vote -t=<section> -v=<vote>");
                }
            }
            else if (phaseOfTheCreatingVote == -1) {
                if (isNumeric(string)) {
                    int choice = Integer.parseInt(string);
                    if (choice > 0 && choice <= options.size()) {
                        chnl.writeAndFlush("You voted for: " + options.get(choice - 1));
                        numOfVotedUsers.set(choice - 1, numOfVotedUsers.get(choice - 1) + 1);
                        phaseOfTheCreatingVote = 0; // Завершаем голосование
                    } else {
                        chnl.writeAndFlush("Invalid choice. Enter a number from the list.");
                    }
                } else {
                    chnl.writeAndFlush("Invalid input. Enter a numeric value.");
                }
            }
            else if (string.startsWith("delete -t=") && string.contains(" -v=")) {
                String[] parts = string.split(" -v=");
                if (parts.length == 2) {
                    String tmpSectionName = parts[0].substring("delete -t=".length());
                    String tmpVoteName = parts[1];
                    List<Section> secionsList = dataManager.getSections();
                    Section targetSection = null;
                    Vote targetVote = null;
                    for (Section section : secionsList) {
                        if (tmpSectionName.equals(section.getSectionName())) {
                            targetSection = section;
                            List<Vote> tmpVoteList = targetSection.getVoteList();
                            for (Vote vote : tmpVoteList) {
                                if (vote.getName().equals(tmpVoteName)) {
                                    targetVote = vote;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (targetVote != null && targetSection != null) {
                        if (targetVote.getVoteCreatorName().equals(username)) {
                            targetSection.deleteVote(targetVote.getName());
                            chnl.writeAndFlush("Vote succesfully deleted");
                        } else {
                            chnl.writeAndFlush("You cannot delete this vote, because you not create this");
                        }
                    } else {
                        chnl.writeAndFlush("EROROROROORORR");
                        if (targetSection == null) {
                            chnl.writeAndFlush("Section not found");
                        }
                        if (targetVote == null) {
                            chnl.writeAndFlush("Vote not found");
                        }
                    }
                } else {
                    chnl.writeAndFlush("EROROROROORORR");
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("User " + username + " отвалился");
        channels.remove(ctx.channel());
        ctx.close();
    }

    public static void broadcastMessage(String message) {
        for (Channel c : channels) {
            c.writeAndFlush(message + "\n");
        }
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void logMessage(String username, String message) {
        try (FileWriter writer = new FileWriter("logs.txt", true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] [" + username + "]: " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}