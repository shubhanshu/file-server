package com.shubh.server.http;

public class InvalidRequestException extends Exception {
  public InvalidRequestException(String error) {
    super(error);
  }
}
