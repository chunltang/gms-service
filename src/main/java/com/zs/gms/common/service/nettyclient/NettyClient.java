package com.zs.gms.common.service.nettyclient;

import com.zs.gms.common.annotation.RedisLock;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.utils.GmsUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
@Data
public class NettyClient {

    private final static String FUNCTION_FIELD = "funcName";
    private final static String FUNCTION_DADA = "data";
    private final static String FUNCTION_TYPE = "type";

    private Channel channel;
    private Bootstrap bt;
    private String host;
    private int port;
    private NioEventLoopGroup workGroup;
    public static NettyClient instance = new NettyClient(StaticConfig.NETTY_HOST, 8081);

    private NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        bt = new Bootstrap();
        workGroup = new NioEventLoopGroup();
        start();
    }

    private void start() {
        bt.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, true)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ClientActiveHandler());
                    }
                });
        addRetry();
    }


    private boolean connect(Bootstrap bootstrap, String host, int port) {
        synchronized (this) {
            if (null != this.channel && this.channel.isActive()) {
                return true;
            }
            bootstrap.connect(host, port).addListener(future -> {
                if (future.isSuccess()) {
                    Channel channel = ((ChannelFuture) future).channel();
                    log.debug("连接服务器成功:{}", channel.remoteAddress());
                    this.channel = channel;
                }
            });
        }
        return false;
    }

    /**
     * 关闭连接
     * */
    public static void close() {
        instance.getChannel().close();
        instance.getWorkGroup().shutdownGracefully();
    }

    /**
     * 重试
     */
    public void addRetry() {
        DelayedService.Task task = DelayedService.buildTask();
        task.withNum(-1)
                //.withPrintLog(true)
                .withDesc("netty 连接重试!")
                .withTask(() -> {
                    boolean connect = connect(bt, host, port);
                    if (connect) {
                        task.withNum(0);
                    }
                });
        DelayedService.addTask(task, 5000);
    }

    private ByteBuf getByteBuf(String message) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(GmsConstant.CLIENT_SIGN);
        buffer.writeInt(GmsConstant.CLIENT_USER_SIGN);
        byte[] bytes = message.getBytes(Charset.forName("utf8"));
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        return buffer;
    }

    @RedisLock(key = RedisKeyPool.WS_NETTY_LOCK)
    public static void sendMessage(WsData wsData) {
        Channel channel = instance.getChannel();
        if (null != channel && channel.isActive()) {
            NettyClient instance = NettyClient.instance;
            ByteBuf byteBuf = instance.getByteBuf(GmsUtil.toJson(wsData));
            instance.getChannel().writeAndFlush(byteBuf);
        }
    }
}
