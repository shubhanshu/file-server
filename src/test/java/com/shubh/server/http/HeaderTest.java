package com.shubh.server.http;

import org.junit.Test;
import static org.junit.Assert.*;

public class HeaderTest {
  private Header.Parser _parser = new Header.Parser();

  @Test
  public void buildForShouldHandleValidHeader() throws InvalidRequestException {
    String line = "Host: www.google.com";
    Header expected = new Header("Host", " www.google.com");
    assertEquals(expected, _parser.parse(line));
  }

  @Test
  public void buildForShouldHandleValidHeaderWithColonsInValue() throws InvalidRequestException {
    String line = "X-Custom-Header:1:2";
    Header expected = new Header("X-Custom-Header", "1:2");
    assertEquals(expected, _parser.parse(line));
  }

  @Test(expected = InvalidRequestException.class)
  public void shouldThrowForInvalidHeader() throws InvalidRequestException {
    String line = "busted";
    _parser.parse(line);
  }
}
