package com.shubh.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.input.BoundedInputStream;

public class Request {
  private final RequestLine _line;
  private final ImmutableSet<Header> _headers;
  //TODO: Add support for message body

  public Request(final RequestLine line, final Set<Header> headers) {
    _line = line;
    _headers = ImmutableSet.copyOf(headers);
  }

  public RequestLine getLine() {
    return _line;
  }

  public Set<Header> getHeaders() {
    return _headers;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("_line", _line)
                      .add("_headers", _headers)
                      .toString();
  }


  public static class Parser {

    //Support 8KB request
    private static long MAX_REQUEST_SIZE = 8 * 1024;

    private final Header.Parser _headerParser;
    private final RequestLine.Parser _requestLineParser;

    public Parser() {
      this(new Header.Parser(), new RequestLine.Parser());
    }

    Parser(final Header.Parser headerParser, final RequestLine.Parser requestLineParser) {
      _headerParser = headerParser;
      _requestLineParser = requestLineParser;
    }

    public Request parse(InputStream inputStream) throws InvalidRequestException, UnsupportedRequestException {
      //TODO: Signal error in case of larger than supported requests
      BufferedReader reader = new BufferedReader(new InputStreamReader(new BoundedInputStream(inputStream, MAX_REQUEST_SIZE)));
      try {
        RequestLine requestLine = _requestLineParser.parse(reader.readLine());
        Set<Header> headers = new HashSet<>(16);
        //Delineation between headers and request body is achieved using an empty line. Use that as a signal to stop parsing headers
        for (; ; ) {
          String line = reader.readLine();
          if (!Strings.isNullOrEmpty(line)) {
            Header header = _headerParser.parse(line);
            headers.add(header);
          } else break;
        }
        return new Request(requestLine, headers);
      } catch (IOException e) {
        throw new InvalidRequestException("Invalid request");
      }
    }
  }
}
