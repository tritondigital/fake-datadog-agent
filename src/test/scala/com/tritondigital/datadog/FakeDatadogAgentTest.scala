package com.tritondigital.datadog

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FlatSpec, Matchers}

import scala.util.Random

class FakeDatadogAgentTest extends FlatSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {
  val PORT = 9998

  val datadog = new FakeDatadogAgent(PORT)
  var client : DatadogClient = null

  override def beforeAll() {
    client = new DatadogClient(port = PORT)("fake.datadog.agent", "environment:env")
  }

  before {
    datadog.expectRequests(1)
    datadog.start()
  }

  after {
    datadog.stop()
  }

  override def afterAll {
    client.statsd.stop()
  }

  "This client" should "prefix all counters with the default prefix" in {
    val prefixedClient = new DatadogClient(port = PORT)("some-prefix", "environment:env")
    prefixedClient.increment("a-counter")
    datadog.waitForRequest()

    all(datadog.lastMessages) should startWith("some-prefix.")

    prefixedClient.statsd.stop()
  }

  it should "specify the environment with a tag" in {
    val suffixedClient = new DatadogClient(port = PORT)("some-prefix")
    suffixedClient.recordDuration("gauge", 42, "environment:env")
    datadog.waitForRequest()

    all(datadog.lastMessages) should endWith("|#environment:env")

    suffixedClient.statsd.stop()
  }

  it should "increment a counter" in {
    client.increment("some-counter")
    datadog.waitForRequest()

    datadog.lastMessages should contain only "fake.datadog.agent.some-counter:1|c|#environment:env"
  }

  it should "record a duration value" in {
    client.recordDuration("any-duration", 42)
    datadog.waitForRequest()

    datadog.lastMessages should contain only "fake.datadog.agent.any-duration:42|ms|#environment:env"
  }

  it should "record a gauge value" in {
    client.recordValue("my-gauge", 1204)
    datadog.waitForRequest()

    datadog.lastMessages should contain only "fake.datadog.agent.my-gauge:1204|g|#environment:env"
  }

  it should "support tags for counter" in {
    client.increment("counter", "tag:value")
    datadog.waitForRequest()

    datadog.lastMessages should contain only "fake.datadog.agent.counter:1|c|#environment:env,tag:value"
  }

  it should "support tags for duration" in {
    client.recordDuration("duration", 45, "tag:value")
    datadog.waitForRequest()

    datadog.lastMessages should contain only "fake.datadog.agent.duration:45|ms|#environment:env,tag:value"
  }

  it should "support tags for gauge" in {
    client.recordValue("gauge", 45, "tag:value")
    datadog.waitForRequest()

    datadog.lastMessages should contain only "fake.datadog.agent.gauge:45|g|#environment:env,tag:value"
  }

  it should "support awaiting for no requests" in {
    datadog.expectRequests(0)
    client.increment("unwanted")
    datadog.waitForRequest()
    datadog.lastMessages should not be empty
  }

  it should "support events with text and a title" in {
    client.recordEvent("my title", "my text", "tag:value", "tag2:value2")
    datadog.waitForRequest()

    datadog.lastMessages should contain only "_e{27,7}:fake.datadog.agent.my title|my text|#environment:env,tag2:value2,tag:value"
  }

  it should "read messages up to 1399 bytes (just like those java-dogstatsd-client can write)" in {
    client.recordValue("a-huge-name-" + Random.alphanumeric.take(1338).mkString + "-the-end", 22)
    datadog.waitForRequest()

    all(datadog.lastMessages) should startWith("fake.datadog.agent.a-huge-name")
    all(datadog.lastMessages) should endWith("the-end:22|g|#environment:env")
  }

  it should "support multiple metrics across different metric types" in {
    datadog.expectRequests(3)
    client.recordValue("gauge", 45)
    client.increment("counter")
    client.recordEvent("mytitle", "mytext")
    datadog.waitForRequest()

    datadog.lastMessages should contain("fake.datadog.agent.gauge:45|g|#environment:env")
    datadog.lastMessages should contain("fake.datadog.agent.counter:1|c|#environment:env")
    datadog.lastMessages should contain("_e{26,6}:fake.datadog.agent.mytitle|mytext|#environment:env")
  }
}
