package io.rsocket.demo.messages;

public class FileInfo {
  String deviceId;
  String file;
  
  public FileInfo(String deviceId, String file) {
    this.deviceId = deviceId;
    this.file = file;
  }
  
  public FileInfo() {
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
  
  @Override
  public String toString() {
    return "FileInfo{" +
             "deviceId='" + deviceId + '\'' +
             ", file='" + file + '\'' +
             '}';
  }
}
