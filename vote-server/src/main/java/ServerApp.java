import Logic.DataManager;
import Logic.FileManager;
import io.netty.channel.Channel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerApp {
    private static final int port = 8180;
    private static Channel serverChannel;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(3);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static FileManager fileManager;// = new FileManager();
    private static DataManager dataManager;// = new DataManager();

    public static void main(String[] args) {
//        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
//        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        dataManager = new DataManager();
        fileManager = new FileManager(dataManager);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new StringDecoder(), new StringEncoder(), new MainHandler(dataManager));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            serverChannel = future.channel();
            startConsoleListener();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startConsoleListener() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Shutting down server...");
                    shutdown();
                    break;
                }
                else if (command.startsWith("save ")) {
                    String filename = command.substring("save ".length());
//
//                    dataManager.createSection("asd");
//                    List<String> options = new ArrayList<>();
//                    options.add("Чел а");
//                    options.add("Чел б");
//                    options.add("Чел в");
//                    dataManager.createVote("asd", "кто мэр города?", "близятся выборы", options);
                    fileManager.saveFile(filename);
                    System.out.println("File is creating. Pls, wait");
                }
                else if (command.startsWith("load ")) {
                    String filename = command.substring("load ".length());
                    System.out.println(filename + " was loaded");
                    fileManager.loadFile(filename);
                    dataManager = fileManager.getDataManager();
//                    System.out.println(fileManager.getDataManager().getSections().get(0).getSectionName());
                } else {
                    System.out.println("Unknown command. Available commands: exit, load <filename>, save <filename>");
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    private static void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        if (serverChannel != null) {
            serverChannel.close();
        }
        System.out.println("Server stopped.");

    }
}
