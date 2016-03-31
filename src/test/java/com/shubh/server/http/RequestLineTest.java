package com.shubh.server.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;

public class RequestLineTest {
  private RequestLine.Parser _parser = new RequestLine.Parser();

  @Test
  public void buildForShouldHandleValidLine() throws URISyntaxException, InvalidRequestException, UnsupportedRequestException {
    String line = "GET / HTTP/1.1";
    RequestLine expected = new RequestLine(RequestMethod.GET, new URI("/"), "HTTP/1.1");
    assertEquals(expected, _parser.parse(line));
  }

  @Test
  public void buildForShouldHandleValidLineWithFullUri() throws URISyntaxException, InvalidRequestException, UnsupportedRequestException {
    String line = "GET www.google.com/ HTTP/1.1";
    RequestLine expected = new RequestLine(RequestMethod.GET, new URI("www.google.com/"), "HTTP/1.1");
    assertEquals(expected, _parser.parse(line));
  }

  @Test(expected = InvalidRequestException.class)
  public void buildForShouldThrowForUnssuportedMethod() throws URISyntaxException, InvalidRequestException, UnsupportedRequestException {
    String line = "POST www.google.com/ HTTP/1.1";
    _parser.parse(line);
  }

  @Test(expected = UnsupportedRequestException.class)
  public void buildForShouldThrowForUnssuportedProtocol() throws URISyntaxException, InvalidRequestException, UnsupportedRequestException {
    String line = "GET www.google.com/ HTTP/1";
    _parser.parse(line);
  }

  @Test
  public void buildForShouldHandleWildcardUri() throws URISyntaxException, InvalidRequestException, UnsupportedRequestException {
    String line = "GET * HTTP/1.1";
    RequestLine expected = new RequestLine(RequestMethod.GET, new URI("*"), "HTTP/1.1");
    assertEquals(expected, _parser.parse(line));
  }

  @Test(expected = InvalidRequestException.class)
  public void buildForShouldThrowForMalformedLine() throws URISyntaxException, InvalidRequestException, UnsupportedRequestException {
    String line = "hello";
    _parser.parse(line);
  }
}
