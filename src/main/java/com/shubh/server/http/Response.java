package com.shubh.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;

import com.google.common.base.Objects;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Response {
  private static final byte[] CRLF = "\r\n".getBytes(Charsets.UTF_8);
  private static final Charset UTF_8 = Charsets.UTF_8;
  private LinkedList<Header> _headers;
  private String _protocolVersion;
  private HttpStatus _httpStatus;
  private InputStream _body;

  public Response(Builder builder) {
    _headers = builder._headers;
    _protocolVersion = builder._protocolVersion;
    _httpStatus = builder._httpStatus;
    _body = builder._body;
  }

  public static Response badRequest() {
    return statusOnlyResponse(HttpStatus.S_400);
  }

  public static Response notFound() {
    return statusOnlyResponse(HttpStatus.S_404);
  }

  public static Response notSupported() {
    return statusOnlyResponse(HttpStatus.S_405);
  }

  private static Response statusOnlyResponse(HttpStatus status) {
    Response.Builder builder = new Response.Builder();
    builder.withStatus(status);
    return new Response(builder);
  }



  public void write(OutputStream outputStream) throws IOException {

    try {
      String line = _protocolVersion + " " + _httpStatus.toString();
      outputStream.write(line.getBytes(UTF_8));
      outputStream.write(CRLF);
      for (final Header _header : _headers) {
        outputStream.write(_header.toString().getBytes(UTF_8));
        outputStream.write(CRLF);
      }
      outputStream.write(new Header("Connection", " close").toString().getBytes(UTF_8));
      outputStream.write(CRLF);

      outputStream.write(CRLF);
      if (_body != null) IOUtils.copy(_body, outputStream);
    } finally {
      if (_body != null) _body.close();
    }

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Response that = (Response) o;

    return Objects.equal(this.CRLF, that.CRLF) &&
        Objects.equal(this.UTF_8, that.UTF_8) &&
        Objects.equal(this._headers, that._headers) &&
        Objects.equal(this._protocolVersion, that._protocolVersion) &&
        Objects.equal(this._httpStatus, that._httpStatus) &&
        Objects.equal(this._body, that._body);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(CRLF, UTF_8, _headers, _protocolVersion, _httpStatus, _body);
  }

  public static class Builder {
    LinkedList<Header> _headers = new LinkedList<>();
    String _protocolVersion = "HTTP/1.1";
    HttpStatus _httpStatus = HttpStatus.S_200;
    InputStream _body;

    public Builder withHeader(Header header) {
      _headers.add(header);
      return this;
    }

    public Builder withStatus(HttpStatus status) {
      _httpStatus = status;
      return this;
    }

    public Builder withBody(InputStream body) {
      _body = body;
      return this;
    }


  }

}
