package com.shubh.server;

import java.io.File;

// A simple file provider to aid in testing
public class FileProvider {
  public File get(String path) {
    return new File(path);
  }
}
