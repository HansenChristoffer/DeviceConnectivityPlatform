package io.miso;

import io.miso.tcp.TcpAckHandler;
import io.miso.tcp.TcpAuthHandler;
import io.miso.tcp.TcpMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/*
 * TODO: It is not working as intended. The problem at the moment is that setAccessible(true) is disabled.
 *  Therefore, need to find a way to enable it or test differently.
 */
@Ignore
class TcpNettyHandlerTest {
    private ChannelHandlerContext ctx;

    @BeforeEach
    public void setUp() {
        // Create a new NettyServer and mock ChannelHandlerContext
        ctx = mock(ChannelHandlerContext.class);
    }

    @Test
    void testChannelRead() {
        // Create a new EmbeddedChannel with the server pipeline
        final EmbeddedChannel channel = new EmbeddedChannel(
                new TcpAuthHandler(),
                new TcpMessageHandler(),
                new TcpAckHandler()
        );

        // Create a new message to send to the pipeline
        final ByteBuf message = mock(ByteBuf.class);

        // Call channelRead on the pipeline with the message and context
        channel.writeInbound(message);
        channel.pipeline().fireChannelRead(message);

        // Verify that the message was processed by the pipeline
        verify(ctx, times(2)).fireChannelRead(any());
    }
}

