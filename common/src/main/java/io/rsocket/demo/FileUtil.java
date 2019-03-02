package io.rsocket.demo;

import reactor.core.Exceptions;

import java.io.File;
import java.net.URI;

public class FileUtil {
  public static File getFileFromResource(String name) {
    try {
      URI uri = Thread.currentThread().getContextClassLoader().getResource(name).toURI();
      return new File(uri);
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }
}
