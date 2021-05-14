package com.pps.movie.im;

import com.pps.PpsNettyImChannelPipelineCostom;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author
 * @discription;
 * @time 2021/1/22 23:08
 */
@Component
public class PipieCustomService implements PpsNettyImChannelPipelineCostom {
    @Override
    public Consumer<ChannelPipeline> custommChannelPipeline() {
        return (p)->{

            p.addLast("ping", new IdleStateHandler(22, 22, 60, TimeUnit.SECONDS));

        };
    }
}
