import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.Socket;

public class Connect {
    private SocketChannel channel;
    private Callback onMessageRecivedCallback;

    // Нужно в параллельном потоке запустить клиент
    public Connect(Callback onMessageRecivedCallback) {
        this.onMessageRecivedCallback = onMessageRecivedCallback;
        Thread t = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap()
                        .group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(io.netty.channel.socket.SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(),
                                        new SimpleChannelInboundHandler<String>() {
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String string) throws Exception {
                                                if (onMessageRecivedCallback != null) {
                                                    onMessageRecivedCallback.callback(string);
                                                }
                                            }
                                        });
                            }
                        });
                ChannelFuture future = bootstrap.connect("localhost", 8180).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void sendMessage(String msg) {
        channel.writeAndFlush(msg);
    }
}
