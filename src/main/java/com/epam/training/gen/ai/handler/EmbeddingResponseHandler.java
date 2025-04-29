package com.epam.training.gen.ai.handler;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingResponseHandler {
    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;

    public <T> Mono<T> handleMonoResponse(ListenableFuture<T> future) {
        return Mono.create(monoSink -> {
            Futures.addCallback(future, new FutureCallback<T>() {
                        @Override
                        public void onSuccess(T result) {
                            monoSink.success(result);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            monoSink.error(throwable);
                        }
                    },
                    taskExecutor);
        });
    }
}
