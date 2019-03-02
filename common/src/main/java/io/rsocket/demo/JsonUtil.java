package io.rsocket.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.Exceptions;

public class JsonUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static <T> String serialize(T target) {
    try {
      return OBJECT_MAPPER.writeValueAsString(target);
    } catch (Throwable t) {
      throw Exceptions.propagate(t);
    }
  }

  public static <T> T deserialize(String source, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(source, clazz);
    } catch (Throwable t) {
      throw Exceptions.propagate(t);
    }
  }
}
