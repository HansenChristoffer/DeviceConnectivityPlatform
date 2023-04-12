package io.miso.tcp;

import io.miso.Message;
import io.miso.core.RemoteMessagePipeline;
import io.miso.core.WorkOperation;
import io.miso.core.handler.HeaderHandler;
import io.miso.core.handler.PayloadHandler;
import io.miso.core.handler.PipelineStep;

public class RemoteMessageTCP implements Message {
    private final WorkOperation workOperation;

    public RemoteMessageTCP(final WorkOperation workOperation) {
        this.workOperation = workOperation;
    }

    @Override
    public byte[] buildMessage() {
        final RemoteMessagePipeline pipeline = new RemoteMessagePipeline(false)
                .addHandler(PipelineStep.HEADER, new HeaderHandler(workOperation))
                .addHandler(PipelineStep.PAYLOAD, new PayloadHandler(workOperation));
        return pipeline.execute();
    }
}
