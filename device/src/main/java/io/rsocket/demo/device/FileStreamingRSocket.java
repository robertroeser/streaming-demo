package io.rsocket.demo.device;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.demo.JsonUtil;
import io.rsocket.demo.messages.RequestFileStream;
import io.rsocket.exceptions.ApplicationErrorException;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class FileStreamingRSocket extends AbstractRSocket {
  private Logger logger = LoggerFactory.getLogger(FileStreamingRSocket.class);
  private ConcurrentHashMap<String, File> files;

  public FileStreamingRSocket(ConcurrentHashMap<String, File> files) {
    this.files = files;
  }

  @Override
  public Flux<Payload> requestStream(Payload payload) {
    RequestFileStream request =
        JsonUtil.deserialize(payload.getDataUtf8(), RequestFileStream.class);

    logger.info("received request to stream file: " + request.toString());
  
    String fileName = request.getFile();
    File file = files.get(fileName);
    if (file == null) {
      logger.error("no file named {} found", fileName);
      return Flux.error(new ApplicationErrorException("no file named " + fileName));
    }

    return FileStreamFactory.streamFile(file, request.getBufferSize()).map(DefaultPayload::create);
  }
}
