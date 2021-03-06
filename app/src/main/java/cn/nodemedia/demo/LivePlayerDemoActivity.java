package cn.nodemedia.demo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

/**
 * 直播播放示例
 */
public class LivePlayerDemoActivity extends AppCompatActivity implements NodePlayerDelegate {

    NodePlayer np;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player_demo);

        //创建NodePlayer实例
        np = new NodePlayer(this);

        //查询播放视图
        NodePlayerView npv = (NodePlayerView)findViewById(R.id.live_player_view);
        //设置播放视图的渲染器模式,可以使用SurfaceView或TextureView. 默认SurfaceView
        npv.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        //设置视图的内容缩放模式
        npv.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);


        //将播放视图绑定到播放器
        np.setPlayerView(npv);

        //设置事件回调代理
        np.setNodePlayerDelegate(this);

        //开启硬件解码,支持4.3以上系统,初始化失败自动切为软件解码,默认开启.
        np.setHWEnable(true);


        /**
         * 设置启动缓冲区时长,单位毫秒.此参数关系视频流连接成功开始获取数据后缓冲区存在多少毫秒后开始播放
         */
        int bufferTime = Integer.valueOf(SharedPreUtil.getString(this, "bufferTime"));
        np.setBufferTime(bufferTime);

        /**
         * 设置最大缓冲区时长,单位毫秒.此参数关系视频最大缓冲时长.
         * RTMP基于TCP协议不丢包,网络抖动且缓冲区播完,之后仍然会接受到抖动期的过期数据包.
         * 设置改参数,sdk内部会自动清理超出部分的数据包以保证不会存在累计延迟,始终与直播时间线保持最大maxBufferTime的延迟
         */
        int maxBufferTime = Integer.valueOf(SharedPreUtil.getString(this, "maxBufferTime"));
        np.setMaxBufferTime(maxBufferTime);

        /**
         * 设置连接超时时长,单位毫秒.默认为0 一直等待.
         * 连接部分RTMP服务器,握手并连接成功后,当播放一个不存在的流地址时,会一直等待下去.
         * 如需超时,设置该值.超时后返回1006状态码.
         */
//        np.setConnectWaitTimeout(10*1000);

        /**
         * @brief rtmpdump 风格的connect参数
         * Append arbitrary AMF data to the Connect message. The type must be B for Boolean, N for number, S for string, O for object, or Z for null.
         * For Booleans the data must be either 0 or 1 for FALSE or TRUE, respectively. Likewise for Objects the data must be 0 or 1 to end or begin an object, respectively.
         * Data items in subobjects may be named, by prefixing the type with 'N' and specifying the name before the value, e.g. NB:myFlag:1.
         * This option may be used multiple times to construct arbitrary AMF sequences. E.g.
         */
//        np.setConnArgs("S:info O:1 NS:uid:10012 NB:vip:1 NN:num:209.12 O:0");


        /**
         * 设置RTSP使用TCP传输模式
         * 支持的模式有:
         * NodePlayer.RTSP_TRANSPORT_UDP
         * NodePlayer.RTSP_TRANSPORT_TCP
         * NodePlayer.RTSP_TRANSPORT_UDP_MULTICAST
         * NodePlayer.RTSP_TRANSPORT_HTTP
         */
//        np.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);

        /**
         * 设置播放直播视频url
         */
        String playUrl = SharedPreUtil.getString(this, "playUrl");
        np.setInputUrl(playUrl);


        /**
         * 在本地开起一个RTMP服务,并进行监听播放,局域网内其他手机或串流器能推流到手机上直接进行播放,无需中心服务器支持
         * 播放的ip可以是本机IP,也可以是0.0.0.0,但不能用127.0.0.1
         * app/stream 可加可不加,只要双方匹配就行
         */
//        np.setLocalRTMP(true);


        /**
         * 开始播放直播视频
         */
        np.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 停止播放
         */
        np.stop();

        /**
         * 释放资源
         */
        np.release();
    }


    /**
     * 事件回调
     * @param nodePlayer 对象
     * @param event 事件状态码
     * @param msg   事件描述
     */
    @Override
    public void onEventCallback(NodePlayer nodePlayer, int event, String msg) {
        Log.i("NodeMedia.NodePlayer","onEventCallback:"+event+" msg:"+msg);

        switch (event) {
            case 1000:
                // 正在连接视频
                break;
            case 1001:
                // 视频连接成功
                break;
            case 1002:
                // 视频连接失败 流地址不存在，或者本地网络无法和服务端通信，回调这里。5秒后重连， 可停止
//                nodePlayer.stopPlay();
                break;
            case 1003:
                // 视频开始重连,自动重连总开关
//                nodePlayer.stopPlay();
                break;
            case 1004:
                // 视频播放结束
                break;
            case 1005:
                // 网络异常,播放中断,播放中途网络异常，回调这里。1秒后重连，如不需要，可停止
//                nodePlayer.stopPlay();
                break;
            case 1006:
                //RTMP连接播放超时
                break;
            case 1100:
                // 播放缓冲区为空
//				System.out.println("NetStream.Buffer.Empty");
                break;
            case 1101:
                // 播放缓冲区正在缓冲数据,但还没达到设定的bufferTime时长
//				System.out.println("NetStream.Buffer.Buffering");
                break;
            case 1102:
                // 播放缓冲区达到bufferTime时长,开始播放.
                // 如果视频关键帧间隔比bufferTime长,并且服务端没有在缓冲区间内返回视频关键帧,会先开始播放音频.直到视频关键帧到来开始显示画面.
//				System.out.println("NetStream.Buffer.Full");
                break;
            case 1103:
//				System.out.println("Stream EOF");
                // 客户端明确收到服务端发送来的 StreamEOF 和 NetStream.Play.UnpublishNotify时回调这里
                // 注意:不是所有云cdn会发送该指令,使用前请先测试
                // 收到本事件，说明：此流的发布者明确停止了发布，或者因其网络异常，被服务端明确关闭了流
                // 本sdk仍然会继续在1秒后重连，如不需要，可停止
//                nodePlayer.stopPlay();
                break;
            case 1104:
                //解码后得到的视频高宽值 格式为:{width}x{height}
                break;
            default:
                break;
        }
    }
}
