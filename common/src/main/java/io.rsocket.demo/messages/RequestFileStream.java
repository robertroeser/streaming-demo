package io.rsocket.demo.messages;

public class RequestFileStream {
  private int bufferSize;

  private String file;

  public RequestFileStream(int bufferSize, String file) {
    this.bufferSize = bufferSize;
    this.file = file;
  }

  public RequestFileStream() {}

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }
  
  @Override
  public String toString() {
    return "RequestFileStream{" +
             "bufferSize=" + bufferSize +
             ", file='" + file + '\'' +
             '}';
  }
}
