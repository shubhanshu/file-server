package com.shubh.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.shubh.server.api.Handler;
import com.shubh.server.http.Header;
import com.shubh.server.http.HttpStatus;
import com.shubh.server.http.Request;
import com.shubh.server.http.Response;

public class FileHandler implements Handler {
  static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;
  private static final String SPACE = " ";
  private final FileProvider _fileProvider;
  private final Clock _clock;

  public FileHandler() {
    this(new FileProvider(), Clock.systemDefaultZone());
  }

  FileHandler(FileProvider fileProvider, Clock clock) {
    _fileProvider = fileProvider;
    _clock = clock;
  }

  @Override
  public Response handle(final Request request) {
    String path = request.getLine().getUri().getPath();
    if (isInValidPath(path))  return Response.badRequest();

    // Remove leading /
    path = path.substring(1);
    File file = _fileProvider.get(path);
    if (!file.exists()) return Response.notFound();
    if (file.isDirectory() || file.isHidden()) return Response.badRequest();

    Response.Builder builder = new Response.Builder();
    builder.withStatus(HttpStatus.S_200);
    writeMetaHeaders(file, builder);
    switch (request.getLine().getMethod()) {
      case HEAD: {
        //Meta headers is all we send
        break;
      }
      case GET: {
        try {
          //TODO: Add content type
          builder.withBody(new FileInputStream(file));
        } catch (IOException e) {
          return Response.badRequest();
        }
        break;
      }
    }
    return new Response(builder);
  }

  private boolean isInValidPath(final String path) {
    return path.isEmpty() || path.contains("..") || path.contains("~");
  }

  private void writeMetaHeaders(final File file, final Response.Builder builder) {
    builder.withHeader(new Header("Content-Length", SPACE + file.length()));
    builder.withHeader(new Header("Date", SPACE + Instant.now(_clock).atOffset(ZoneOffset.UTC).format(DATE_FORMAT)));
    builder.withHeader(new Header("Last-Modified", SPACE + Instant.ofEpochMilli(file.lastModified()).atOffset(ZoneOffset.UTC).format(DATE_FORMAT)));
  }
}
