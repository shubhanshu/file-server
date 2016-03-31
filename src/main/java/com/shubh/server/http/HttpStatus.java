package com.shubh.server.http;

public enum HttpStatus {

  S_200(200, "OK"),
  S_404(404, "Not Found"),
  S_400(400, "Bad Request"),
  S_405(405, "Method not allowed");

  private int _code;
  private String _reason;

  HttpStatus(final int code, final String reason) {
    _code = code;
    _reason = reason;
  }

  public int getCode() {
    return _code;
  }

  public String getReason() {
    return _reason;
  }

  @Override
  public String toString() {
    return _code + " " + _reason;
  }
}
