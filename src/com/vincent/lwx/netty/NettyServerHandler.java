package com.vincent.lwx.netty;

import java.util.Date;
import java.util.List;


import com.vincent.lwx.controller.AskMessageUtils;
import com.vincent.lwx.netty.msg.AskMessage;
import com.vincent.lwx.netty.msg.BaseMsg;
import com.vincent.lwx.netty.msg.ChatMsg;
import com.vincent.lwx.netty.msg.LoginMsg;
import com.vincent.lwx.netty.msg.MsgType;
import com.vincent.lwx.netty.msg.PingMsg;
import com.vincent.lwx.netty.msg.PushMsg;
import com.vincent.lwx.netty.msg.SystemMsg;
import com.vincent.lwx.util.SystemMsgUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author 徐飞
 * @version 2016/02/25 12:00
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

//	private static final Logger logger = LogManager.getLogger(NettyServerHandler.class);
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //channel失效，从Map中移除
    	System.out.println("移除通道");
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("服务器端出现异常！");
    }


    //这里是从客户端过来的消息
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        if (MsgType.LOGIN.equals(baseMsg.getType())) {
            LoginMsg loginMsg = (LoginMsg) baseMsg;
            if (NettyChannelMap.get(loginMsg.getPhoneNum()) == null) {
				// 登录成功,把channel存到服务端的map中
            	System.out.println("用户-->"+loginMsg.getPhoneNum()+":登录成功");
            	NettyChannelMap.add(loginMsg.getPhoneNum(), (SocketChannel) channelHandlerContext.channel());
            	SocketChannel s = (SocketChannel) NettyChannelMap.get(loginMsg.getPhoneNum());
				//TODO 当用户登录成功的时候应该检查数据库是否有未推送的消息 如果有，就推送
				List<AskMessage> data = AskMessageUtils.selectNoSendAskMsg(loginMsg.getPhoneNum());
				if(data!=null){
//					s.writeAndFlush(data);
					for(int i = 0;i<data.size();i++){
						s.writeAndFlush(data.get(i));
					}
					//修改已推送的消息状态
					AskMessageUtils.alterListAskMsgStatus(data);
					System.out.println("NettyServerHandler-->未接受消息已推送完毕");
				}else{
					System.out.println("NettyServerHandler-->没有待推送消息");
				}
				//检查是否有未推送的系统消息
				System.out.println("检查"+loginMsg.getPhoneNum()+"有没有带推送的系统消息");
				List<SystemMsg> systemMsg = SystemMsgUtils.getNoSendSystemMsg(loginMsg.getPhoneNum());
				if(systemMsg!=null){
					for(SystemMsg msg:systemMsg){
						System.out.println("msg.getPhoneNum-->"+msg.getPhoneNum());
						PushServer.push(msg);
						//把这条消息的状态改为已推送
						if(SystemMsgUtils.alterSystemMsgStatus(msg)){
							System.out.println("装填已修改");
						}else{
							System.out.println("修改失败了");
						}
					}
				}else{
					System.out.println("没有未推送的系统消息");
				}
			}
        } else {
            if (NettyChannelMap.get(baseMsg.getPhoneNum()) == null) {
                //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                LoginMsg loginMsg = new LoginMsg();
                channelHandlerContext.channel().writeAndFlush(loginMsg);
            }
        }
        switch (baseMsg.getType()) {
            case PING:
                PingMsg pingMsg = (PingMsg) baseMsg;
                PingMsg replyPing = new PingMsg();
                NettyChannelMap.get(pingMsg.getPhoneNum()).writeAndFlush(replyPing);
                System.out.println("收到客户端:"+pingMsg.getPhoneNum()+" PING类型" 
                		+ new Date());
                break;
            case PUSH:
            	PushMsg pushMsg=(PushMsg)baseMsg;
            	System.out.println("收到客户端"+pushMsg.getPhoneNum()+"消息:"+":"+pushMsg.getContent());
                break;
            case CHAT:
            	ChatMsg chatMsg = (ChatMsg)baseMsg;
            	PushServer.push(chatMsg);
            	break;
            default:
                System.out.println("default。。");
                break;
        }
        ReferenceCountUtil.release(baseMsg);
    }

}