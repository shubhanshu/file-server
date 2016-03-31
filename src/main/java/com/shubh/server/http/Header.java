package com.shubh.server.http;

import com.google.common.base.Objects;
import org.apache.commons.lang3.Validate;

public class Header {
  private String _name;
  private String _value;

  public Header(final String name, final String value) {
    _name = name;
    _value = value;
  }


  public static class Parser {
    public Header parse(String line) throws InvalidRequestException {
      Validate.notNull(line);
      String[] bits = line.split(":", 2);
      if (bits.length != 2) throw new InvalidRequestException("Invalid header field " + line);
      return new Header(bits[0], bits[1]);
    }
  }

  @Override
  public String toString() {
    return _name + ":" + _value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Header that = (Header) o;

    return Objects.equal(this._name, that._name) &&
        Objects.equal(this._value, that._value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(_name, _value);
  }
}
