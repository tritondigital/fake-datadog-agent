package com.tritondigital.datadog

import com.timgroup.statsd.NonBlockingStatsDClient

case class DatadogClient(port : Int = 9999, prefix : String = "fake.datadog.agent", host: String = "localhost") {
  val statsd = new NonBlockingStatsDClient(prefix, host, port, "environment:env")

  def increment(name: String, tag: Option[String] = None): Unit = {
    tag.fold(statsd.increment(name))(statsd.increment(name, _))
  }

  def recordDuration(name: String, value: Long, tag: Option[String] = None): Unit = {
    tag.fold(statsd.recordExecutionTime(name, value))(statsd.recordExecutionTime(name, value, _))
  }

  def recordValue(name: String, value: Long, tag: Option[String] = None): Unit = {
    tag.fold(statsd.recordGaugeValue(name, value))(statsd.recordGaugeValue(name, value, _))
  }
}
