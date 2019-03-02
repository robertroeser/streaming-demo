package io.rsocket.demo.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.demo.JsonUtil;
import io.rsocket.demo.messages.DeviceSetupData;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
  private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

  public static void main(String... args) {
    ConcurrentHashMap<String, DeviceInfo> deviceInfoMap = new ConcurrentHashMap<>();

    SocketAcceptor socketAcceptor =
        (setup, sendingSocket) ->
            Mono.fromSupplier(
                () -> {
                  DeviceSetupData setupData =
                      JsonUtil.deserialize(setup.getDataUtf8(), DeviceSetupData.class);

                  deviceInfoMap.computeIfAbsent(
                      setupData.getDeviceId(),
                      deviceId -> {
                        logger.info("received new incoming connection from device id {}", deviceId);

                        return new DeviceInfo(
                            deviceId,
                            sendingSocket,
                            setupData.getFiles(),
                            () -> {
                              logger.info("device id {} connection closed", deviceId);
                              deviceInfoMap.remove(deviceId);
                            });
                      });
                  return new AbstractRSocket() {};
                });

    CloseableChannel tcp =
        RSocketFactory.receive()
            .errorConsumer(
                throwable -> logger.error("unhandled error in the tcp transport", throwable))
            .acceptor(socketAcceptor)
            .transport(TcpServerTransport.create(9090))
            .start()
            .block();

    logger.info("TCP listening started on port 9090");

    CloseableChannel ws =
        RSocketFactory.receive()
            .errorConsumer(
                throwable -> logger.error("unhandled error in the ws transport", throwable))
            .acceptor((setup, sendingSocket) -> Mono.just(new DeviceManagingRSocket(deviceInfoMap)))
            .transport(WebsocketServerTransport.create("localhost", 9091))
            .start()
            .block();

    logger.info("WS listening started on port 9091");

    Flux.merge(tcp.onClose(), ws.onClose()).blockLast();
  }
}
