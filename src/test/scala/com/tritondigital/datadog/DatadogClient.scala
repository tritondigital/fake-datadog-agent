package com.tritondigital.datadog

import com.timgroup.statsd.Event.builder
import com.timgroup.statsd.{Event, NonBlockingStatsDClient}

case class DatadogClient(host: String = "localhost", port: Int = 9999)(prefix: String, constantTags: String*) {
  val statsd = new NonBlockingStatsDClient(prefix, host, port, constantTags:_*)

  def increment(name: String, tags: String*): Unit = {
    statsd.increment(name, tags:_*)
  }

  def recordDuration(name: String, value: Long, tags: String*): Unit = {
    statsd.recordExecutionTime(name, value, tags:_*)
  }

  def recordValue(name: String, value: Long, tags: String*): Unit = {
    statsd.recordGaugeValue(name, value, tags:_*)
  }

  def recordEvent(title: String, text: String, tags: String*): Unit = {
    statsd.recordEvent(builder().withText(text).withTitle(title).build(), tags:_*)
  }
}
