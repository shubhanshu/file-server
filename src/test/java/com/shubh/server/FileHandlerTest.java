package com.shubh.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;

import com.shubh.server.http.Header;
import com.shubh.server.http.HttpStatus;
import com.shubh.server.http.Request;
import com.shubh.server.http.RequestLine;
import com.shubh.server.http.RequestMethod;
import com.shubh.server.http.Response;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class FileHandlerTest {

  private static final Clock CLOCK = new SettableClock(4444);


  @Test
  public void shouldHandleInvalidPath() throws URISyntaxException {
    URI[] uris = new URI[]{
        new URI("/../"),
        new URI("/~/"),
    };

    for (URI uri : uris) {
      FileProvider fileProvider = mock(FileProvider.class);
      File file = mock(File.class);
      when(fileProvider.get(uri.getPath())).thenReturn(file);
      FileHandler handler = new FileHandler(fileProvider, CLOCK);
      Request request = getRequest(uri);
      Response expectedResponse = Response.badRequest();
      assertEquals(expectedResponse, handler.handle(request));
      reset(file, fileProvider);
    }
  }

  @Test
  public void shouldHandleMissingFile() throws URISyntaxException {
    URI uri = new URI("/missing-file.txt");
    FileProvider fileProvider = mock(FileProvider.class);
    File file = mock(File.class);
    when(fileProvider.get(anyString())).thenReturn(file);
    when(file.exists()).thenReturn(false);
    FileHandler handler = new FileHandler(fileProvider, CLOCK);
    Request request = getRequest(uri);
    Response expectedResponse = Response.notFound();
    assertEquals(expectedResponse, handler.handle(request));
  }

  @Test
  public void shouldHandleDirectories() throws URISyntaxException {
    URI uri = new URI("/missing-file.txt");
    FileProvider fileProvider = mock(FileProvider.class);
    File file = mock(File.class);
    when(fileProvider.get(anyString())).thenReturn(file);
    when(file.exists()).thenReturn(true);
    when(file.isDirectory()).thenReturn(true);
    FileHandler handler = new FileHandler(fileProvider, CLOCK);
    Request request = getRequest(uri);
    Response expectedResponse = Response.badRequest();
    assertEquals(expectedResponse, handler.handle(request));
  }

  @Test
  @Ignore
  public void shouldHandleValidFileWithHeadRequest() throws URISyntaxException, FileNotFoundException {
    URI uri = new URI("/file.txt");
    FileProvider fileProvider = mock(FileProvider.class);
    File file = mock(File.class);
    when(fileProvider.get(anyString())).thenReturn(file);
    when(file.exists()).thenReturn(true);
    when(file.isDirectory()).thenReturn(false);
    when(file.isHidden()).thenReturn(false);
    when(file.length()).thenReturn(42L);
    when(file.lastModified()).thenReturn(423L);
    when(file.getPath()).thenReturn("file.txt");
    FileHandler handler = new FileHandler(fileProvider, CLOCK);
    Request request = new Request(new RequestLine(RequestMethod.HEAD, uri, "HTTP/1.1"), Collections.emptySet());
    Response.Builder builder = new Response.Builder().withStatus(HttpStatus.S_200)
                                                     .withHeader(new Header("Content-Length", " " + 42))
                                                     .withHeader(new Header("Date", " " + Instant.now(CLOCK)
                                                                                                 .atOffset(ZoneOffset.UTC)
                                                                                                 .format(FileHandler.DATE_FORMAT)))
                                                     .withHeader(new Header("Last-Modified", " " + Instant.ofEpochMilli(423L)
                                                                                                          .atOffset(ZoneOffset.UTC)
                                                                                                          .format(FileHandler.DATE_FORMAT)));
    Response expectedResponse = new Response(builder);
    assertEquals(expectedResponse, handler.handle(request));
  }

  private Request getRequest(final URI uri) {
    return new Request(new RequestLine(RequestMethod.GET, uri, "HTTP/1.1"), Collections.emptySet());
  }
}