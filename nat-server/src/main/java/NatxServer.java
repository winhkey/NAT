
import codec.NatxMessageDecoder;
import codec.NatxMessageEncoder;
import handler.NatxServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import net.TcpServer;
import org.apache.commons.cli.*;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServer {

    public static void main(String[] args) throws InterruptedException, ParseException {

        // args
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("port", true, "Natx server port");
        options.addOption("password", true, "Natx server password");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            // print help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {

            int port = Integer.parseInt(cmd.getOptionValue("port", "8888"));
            String password = cmd.getOptionValue("password");

            TcpServer natxClientServer = new TcpServer();
            natxClientServer.bind(port, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    NatxServerHandler natxServerHandler = new NatxServerHandler(password);
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                            new NatxMessageDecoder(), new NatxMessageEncoder(),
                            new IdleStateHandler(60, 30, 0), natxServerHandler);
                }
            });
            System.out.println("Natx server started on port " + port);
        }
    }
}
