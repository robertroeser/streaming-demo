package io.rsocket.demo.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.demo.JsonUtil;
import io.rsocket.demo.messages.FileInfo;
import io.rsocket.demo.messages.ServerRequest;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.apache.logging.log4j.util.Strings;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

public class CLIClient {
  public static void main(String... args) {
    ConsumerOptions options = new ConsumerOptions();
    new CommandLine(options).parse(args);

    if (!options.list && !options.stream) {
      System.out.println("you must select either stream or list");
      CommandLine.usage(options, System.out);
      return;
    }

    ServerRequest serverRequest = new ServerRequest();
    boolean streaming = false;
    String deviceId = options.deviceId;
    if (options.list) {
      if (Strings.isNotEmpty(deviceId)) {
        System.out.println("listing client files for device id " + deviceId);
        serverRequest.setDeviceId(deviceId);
        serverRequest.setType(ServerRequest.Type.LIST_ALL_DEVICE_FILES);
      } else {
        System.out.println("listing all files");
        serverRequest.setType(ServerRequest.Type.LIST_ALL_FILES);
      }
    } else { // if options isn't null
      streaming = true;
      serverRequest.setBufferSize(options.bufferSize);
      if (Strings.isNotEmpty(deviceId)) {
        serverRequest.setDeviceId(deviceId);
        String file = options.file;
        if (Strings.isNotEmpty(file)) {
          System.out.println("streaming file " + file + " for device id " + deviceId);
          serverRequest.setFile(file);
          serverRequest.setType(ServerRequest.Type.STREAM_SPECIFIC_DEVICE_FILE);
        } else {
          System.out.println("streaming all files for client id " + deviceId);
          serverRequest.setType(ServerRequest.Type.STREAM_ALL_DEVICE_FILES);
        }
      } else {
        System.out.println("streaming all files");
        serverRequest.setType(ServerRequest.Type.STREAM_ALL_FILES);
      }
    }

    RSocket rsocket =
        RSocketFactory.connect()
            .transport(WebsocketClientTransport.create(options.host, options.port))
            .start()
            .block();

    Flux<Payload> stream =
        rsocket
            .requestStream(DefaultPayload.create(JsonUtil.serialize(serverRequest)))
            .doOnError(Throwable::printStackTrace);

    Integer rate = options.rate;
    if (rate != null) {
      System.out.println("applying rate " + rate);
      stream = stream.limitRate(rate);
    }

    Integer take = options.take;
    if (take != null) {
      System.out.println("taking up to only " + take + " items");
      stream = stream.take(take);
    }

    Flux<String> stringFlux;
    if (streaming) {
      stringFlux = stream.map(payload -> payload.sliceData().toString(StandardCharsets.UTF_8));
    } else {
      stringFlux =
          stream.map(
              payload -> {
                FileInfo info =
                    JsonUtil.deserialize(payload.getDataUtf8(), FileInfo.class);
                return info.toString();
              });
    }

    String regex = options.regex;
    if (Strings.isNotEmpty(regex)) {
      System.out.println("apply filter " + regex);
      Pattern compile = Pattern.compile(regex);
      stringFlux.filter(string -> compile.matcher(string).find());
    }

    if (options.delay != null) {
      stringFlux =
          stringFlux.onBackpressureBuffer().delaySequence(Duration.ofMillis(options.delay));
    }

    stringFlux.doOnNext(System.out::println).blockLast();
  }

  static class ConsumerOptions {
    @Option(names = {"-H", "--host"})
    String host = "localhost";

    @Option(names = {"-p", "--port"})
    int port = 9091;

    @Option(names = {"-r", "--rate"})
    Integer rate;

    @Option(names = {"-t", "--take"})
    Integer take;

    @Option(names = {"-g", "--regex"})
    String regex;

    @Option(names = {"-b", "--buffer"})
    int bufferSize = 64;

    @Option(names = {"-c", "--deviceId"})
    String deviceId;

    @Option(names = {"-f", "--file"})
    String file;

    @Option(names = {"-l", "--list"})
    boolean list = false;

    @Option(names = {"-s", "--stream"})
    boolean stream = false;

    @Option(names = {"-d", "--delay"})
    Integer delay;
  }
}
