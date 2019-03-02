package io.rsocket.demo.messages;

public class ServerRequest {
  private Type type;
  private String deviceId;
  private String file;
  private int bufferSize;

  public ServerRequest(Type type, String deviceId, String file, int bufferSize) {
    this.type = type;
    this.deviceId = deviceId;
    this.file = file;
    this.bufferSize = bufferSize;
  }

  public ServerRequest(Type type, String deviceId, String file) {
    this.type = type;
    this.deviceId = deviceId;
    this.file = file;
  }

  public ServerRequest(Type type, String deviceId) {
    this.type = type;
    this.deviceId = deviceId;
  }

  public ServerRequest(Type type) {
    this.type = type;
  }

  public ServerRequest() {}

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public enum Type {
    STREAM_ALL_FILES,
    LIST_ALL_FILES,
    STREAM_ALL_DEVICE_FILES,
    LIST_ALL_DEVICE_FILES,
    STREAM_SPECIFIC_DEVICE_FILE
  }
}
