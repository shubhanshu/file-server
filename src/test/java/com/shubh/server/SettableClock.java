package com.shubh.server;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;


public class SettableClock extends Clock {
  private Instant _instant;
  private ZoneId _zone;

  public SettableClock(Instant fixedInstant, ZoneId zone) {
    _instant = fixedInstant;
    _zone = zone;
  }

  public SettableClock(Instant fixedInstant) {
    this(fixedInstant, ZoneOffset.UTC);
  }

  public SettableClock(long epochMillis) {
    this(Instant.ofEpochMilli(epochMillis));
  }

  public SettableClock() {
    this(Instant.now());
  }

  public void setCurrentTimeMillis(long epochMillis) {
    _instant = Instant.ofEpochMilli(epochMillis);
  }


  @Override
  public ZoneId getZone() {
    return _zone;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    if (zone.equals(this._zone)) {  // intentional NPE
      return this;
    }
    return new SettableClock(_instant, zone);
  }

  @Override
  public long millis() {
    return _instant.toEpochMilli();
  }

  @Override
  public Instant instant() {
    return _instant;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SettableClock) {
      SettableClock other = (SettableClock) obj;
      return _instant.equals(other._instant) && _zone.equals(other._zone);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return _instant.hashCode() ^ _zone.hashCode();
  }

  @Override
  public String toString() {
    return "SettableClock[" + _instant + "," + _zone + "]";
  }
}
