package io.rsocket.demo.messages;

import java.util.List;

public class DeviceSetupData {
  private List<String> files;

  private String deviceId;

  public DeviceSetupData(List<String> files, String deviceId) {
    this.files = files;
    this.deviceId = deviceId;
  }

  public DeviceSetupData() {}

  public List<String> getFiles() {
    return files;
  }

  public void setFiles(List<String> files) {
    this.files = files;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
}
