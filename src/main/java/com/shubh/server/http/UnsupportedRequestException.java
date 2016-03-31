package com.shubh.server.http;

public class UnsupportedRequestException extends Exception {
  public UnsupportedRequestException(String error) {
    super(error);
  }
}
