package com.zs.gms.common.service.nettyclient;

import com.zs.gms.common.utils.GmsUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 处理websocket数据
 * */
@Slf4j
public class ClientActiveHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        buf.skipBytes(12);
        String data = buf.toString(Charset.forName("utf8"));
        WsData wsData = GmsUtil.toObj(data, WsData.class);
        ReferenceCountUtil.release(msg);
        DataHandler.handle(wsData);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("成功连接服务器");
        NettyClient.sendMessage(new WsData());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("和netty服务器断开连接!");
        NettyClient.instance.addRetry();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("连接异常捕获:"+cause);
        ctx.close();
    }
}
