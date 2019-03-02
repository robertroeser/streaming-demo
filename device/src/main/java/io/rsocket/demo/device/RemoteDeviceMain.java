package io.rsocket.demo.device;

import io.rsocket.RSocketFactory;
import io.rsocket.demo.FileUtil;
import io.rsocket.demo.JsonUtil;
import io.rsocket.demo.messages.DeviceSetupData;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteDeviceMain {
  private static final Logger logger = LogManager.getLogger(RemoteDeviceMain.class);

  private static final String clientId = UUID.randomUUID().toString();

  public static void main(String... args) {
    logger.info("Starting Client...");
    ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();
    files.computeIfAbsent("file.txt", FileUtil::getFileFromResource);
    files.computeIfAbsent("file2.txt", FileUtil::getFileFromResource);
    files.computeIfAbsent("file3.txt", FileUtil::getFileFromResource);

    logger.info("files available for streaming", files.keySet().toString());

    DeviceSetupData setupData = new DeviceSetupData(new ArrayList<>(files.keySet()), clientId);

    RSocketFactory.connect()
        .setupPayload(DefaultPayload.create(JsonUtil.serialize(setupData)))
        .acceptor(rSocket -> new FileStreamingRSocket(files))
        .transport(TcpClientTransport.create(9090))
        .start()
        .doFinally(s -> logger.info("Client Started"))
        .block()
        .onClose()
        .block();
  }
}
