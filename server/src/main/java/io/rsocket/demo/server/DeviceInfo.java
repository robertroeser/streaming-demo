package io.rsocket.demo.server;

import io.rsocket.RSocket;

import java.util.List;
import java.util.Objects;

public class DeviceInfo {
  private String deviceId;
  private RSocket rSocket;
  private List<String> files;

  public DeviceInfo(String deviceId, RSocket rSocket, List<String> files, Runnable onClose) {
    this.deviceId = deviceId;
    this.rSocket = rSocket;
    this.files = files;
  
    rSocket.onClose().doFinally(s -> onClose.run()).subscribe();
  }

  public DeviceInfo() {}

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public RSocket getrSocket() {
    return rSocket;
  }

  public void setrSocket(RSocket rSocket) {
    this.rSocket = rSocket;
  }

  public List<String> getFiles() {
    return files;
  }

  public void setFiles(List<String> files) {
    this.files = files;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeviceInfo that = (DeviceInfo) o;
    return Objects.equals(deviceId, that.deviceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deviceId);
  }

  @Override
  public String toString() {
    return "DeviceInfo{"
        + "deviceId='"
        + deviceId
        + '\''
        + ", rSocket="
        + rSocket
        + ", files="
        + files
        + '}';
  }
}
