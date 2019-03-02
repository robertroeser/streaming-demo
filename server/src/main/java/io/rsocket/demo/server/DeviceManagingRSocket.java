package io.rsocket.demo.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.demo.JsonUtil;
import io.rsocket.demo.messages.FileInfo;
import io.rsocket.demo.messages.RequestFileStream;
import io.rsocket.demo.messages.ServerRequest;
import io.rsocket.exceptions.UnsupportedSetupException;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

public class DeviceManagingRSocket extends AbstractRSocket {
  private ConcurrentHashMap<String, DeviceInfo> clientInfo;
  
  public DeviceManagingRSocket(ConcurrentHashMap<String, DeviceInfo> clientInfo) {
    this.clientInfo = clientInfo;
  }
  
  @Override
  public Flux<Payload> requestStream(Payload payload) {
    ServerRequest serverRequest = JsonUtil.deserialize(payload.getDataUtf8(), ServerRequest.class);

    switch (serverRequest.getType()) {
      case LIST_ALL_FILES:
        return listAllFiles();
      case STREAM_ALL_FILES:
        return streamAllFiles(serverRequest.getBufferSize());
      case LIST_ALL_DEVICE_FILES:
        return listAllClientFiles(serverRequest.getDeviceId());
      case STREAM_ALL_DEVICE_FILES:
        return streamAllClientFiles(serverRequest.getDeviceId(), serverRequest.getBufferSize());
      case STREAM_SPECIFIC_DEVICE_FILE:
        return streamSpecificClientFile(
            serverRequest.getDeviceId(), serverRequest.getBufferSize(), serverRequest.getFile());
      default:
        return Flux.error(new UnsupportedSetupException("unknown type " + serverRequest.getType()));
    }
  }
  
  // Listing Methods
  private Flux<Payload> listAllFiles() {
    return Flux.fromIterable(clientInfo.keySet()).flatMap(this::listAllClientFiles);
  }
  
  
  private Flux<Payload> listAllClientFiles(String clientId) {
    DeviceInfo deviceInfo = this.clientInfo.get(clientId);
    
    if (deviceInfo == null) {
      return Flux.empty();
    } else {
      return Flux.fromStream(
        deviceInfo
          .getFiles()
          .stream()
          .map(
            s -> {
              FileInfo info = new FileInfo();
              info.setDeviceId(clientId);
              info.setFile(s);
              return DefaultPayload.create(JsonUtil.serialize(info));
            }));
    }
  }

  
  // Streaming Methods
  private Flux<Payload> streamAllFiles(int bufferSize) {
    return Flux.fromIterable(clientInfo.keySet())
        .flatMap(clientId -> streamAllClientFiles(clientId, bufferSize));
  }


  private Flux<Payload> streamAllClientFiles(String clientId, int bufferSize) {
    DeviceInfo deviceInfo = this.clientInfo.get(clientId);
    if (deviceInfo == null) {
      return Flux.empty();
    } else {
      return Flux.fromIterable(deviceInfo.getFiles())
          .flatMap(file -> streamSpecificClientFile(clientId, bufferSize, file));
    }
  }

  private Flux<Payload> streamSpecificClientFile(String clientId, int bufferSize, String file) {
    DeviceInfo deviceInfo = this.clientInfo.get(clientId);
    if (deviceInfo == null) {
      return Flux.empty();
    } else {
      RequestFileStream request = new RequestFileStream(bufferSize, file);

      return deviceInfo
          .getrSocket()
          .requestStream(DefaultPayload.create(JsonUtil.serialize(request)));
    }
  }
}
