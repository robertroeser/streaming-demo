package io.rsocket.demo.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FileStreamFactory {
  public static final Flux<ByteBuf> streamFile(File file, int bufferSize) {
    Objects.requireNonNull(file, "must include a file");
    if (!file.isFile()) {
      throw new IllegalArgumentException("must pass in file");
    }
    
    try {
      FileChannel channel = openFileForReading(file);
      AtomicLong position = new AtomicLong();
      
      return Flux.<ByteBuf>generate(
              sink -> {
                ByteBuf byteBuf = readBytes(channel, position, bufferSize);
                if (byteBuf == Unpooled.EMPTY_BUFFER) {
                  sink.complete();
                } else {
                  sink.next(byteBuf);
                }
              })
          .doFinally(s -> closeChannel(channel));
    } catch (Throwable t) {
      return Flux.error(t);
    }
  }

  static ByteBuf readBytes(FileChannel channel, AtomicLong position, int bufferSize) {
    try {
      
      ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
      int read = channel.read(byteBuffer, position.get());
      position.addAndGet(read);
      byteBuffer.flip();
      if (read == 0) {
        return Unpooled.EMPTY_BUFFER;
      } else {
        return Unpooled.wrappedBuffer(byteBuffer);
      }
    } catch (Exception t) {
      throw Exceptions.propagate(t);
    }
  }

  static FileChannel openFileForReading(File file) {
    try {
      return FileChannel.open(file.toPath(), StandardOpenOption.READ);
    } catch (Exception t) {
      throw Exceptions.propagate(t);
    }
  }

  static void closeChannel(SeekableByteChannel channel) {
    try {
      channel.close();
    } catch (Exception t) {
      throw Exceptions.propagate(t);
    }
  }
}
