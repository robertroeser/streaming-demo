package io.rsocket.demo.device;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;

class FileStreamFactoryTest {
  @Test
  void testStreamFile() throws Exception {
    URI uri = Thread.currentThread().getContextClassLoader().getResource("file.txt").toURI();

    File file = new File(uri);
    FileStreamFactory.streamFile(file, 64)
        .doOnNext(byteBuf -> System.out.println(byteBuf.toString(StandardCharsets.UTF_8)))
        .blockLast();
  }

  @Test
  void testStreamAndTake() throws Exception {
    URI uri = Thread.currentThread().getContextClassLoader().getResource("file.txt").toURI();

    File file = new File(uri);
    FileStreamFactory.streamFile(file, 64)
        .doOnNext(byteBuf -> System.out.println(byteBuf.toString(StandardCharsets.UTF_8)))
        .take(10)
        .blockLast();
  }
  
  @Test
  void testStreamBufferOf1() throws Exception {
    URI uri = Thread.currentThread().getContextClassLoader().getResource("file.txt").toURI();
    
    File file = new File(uri);
    FileStreamFactory.streamFile(file, 1)
      .doOnNext(byteBuf -> System.out.println(byteBuf.toString(StandardCharsets.UTF_8)))
      .take(10)
      .blockLast();
  }
}
