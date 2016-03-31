package com.shubh.server.api;

import com.shubh.server.http.Request;
import com.shubh.server.http.Response;

public interface Handler {
  public Response handle(Request request);
}
