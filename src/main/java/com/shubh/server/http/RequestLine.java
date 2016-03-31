package com.shubh.server.http;

import java.net.URI;

import com.google.common.base.Objects;

public class RequestLine {
  private RequestMethod _method;
  private URI _uri;
  private String _protocolVersion;

  public RequestMethod getMethod() {
    return _method;
  }

  public URI getUri() {
    return _uri;
  }

  public RequestLine(final RequestMethod method, final URI uri, final String protocolVersion) {
    _method = method;
    _uri = uri;
    _protocolVersion = protocolVersion;
  }

  public static class Parser {
    public RequestLine parse(String line) throws InvalidRequestException, UnsupportedRequestException {
      if (line == null) throw new InvalidRequestException("No request line found");
      line = line.trim();
      String[] bits = line.split(" ");
      if (bits.length != 3) throw new InvalidRequestException("Request line format is invalid");
      try {
        RequestMethod method = RequestMethod.valueOf(RequestMethod.class, bits[0]);
        URI uri = URI.create(bits[1]);
        if (!"HTTP/1.1".equals(bits[2]) && !"HTTP/1.0".equals(bits[2])) throw new UnsupportedRequestException(bits[2] + " protocol is not supported");
        return new RequestLine(method, uri, bits[2]);
      } catch (NullPointerException | IllegalArgumentException e) {
        throw new InvalidRequestException("Request line format is invalid");
      }
    }
  }


  @Override
  public String toString() {
    return _method.toString() + " " + _uri.toString() + " " + _protocolVersion + "/r/n";
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RequestLine that = (RequestLine) o;

    return Objects.equal(this._method, that._method) &&
        Objects.equal(this._uri, that._uri) &&
        Objects.equal(this._protocolVersion, that._protocolVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(_method, _uri, _protocolVersion);
  }
}
