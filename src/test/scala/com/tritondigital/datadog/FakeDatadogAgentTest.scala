package com.tritondigital.datadog

import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.Matchers


class FakeDatadogAgentTest extends FlatSpec with Matchers with BeforeAndAfter {

  val datadog = new FakeDatadogAgent(9999)
  val client = new DatadogClient(9999, "fake.datadog.agent", "environment:env")

  before {
    datadog.start()
  }

  after {
    datadog.stop()
  }

  "This client" should "prefix all counters with the default prefix" in {
    val prefixedClient = new DatadogClient(9999, "some-prefix", "environment:env")
    datadog.expectRequests(1)
    prefixedClient.increment("a-counter")
    datadog.waitForRequest()

    all (datadog.lastMessages) should startWith("some-prefix.")
  }

  it should "specify the environment with a tag" in {
    val suffixedClient = new DatadogClient(9999, "fake.datadog.agent", "some-suffix")
    datadog.expectRequests(1)
    suffixedClient.recordDuration("gauge", 42)
    datadog.waitForRequest()

    all (datadog.lastMessages) should endWith("|#some-suffix")
  }

  it should "increment a counter" in {
    datadog.expectRequests(1)
    client.increment("some-counter")
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.some-counter:1|c|#environment:env")
  }

  it should "record a duration value" in {
    datadog.expectRequests(1)
    client.recordDuration("any-duration", 42)
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.any-duration:42|ms|#environment:env")
  }

  it should "record a gauge value" in {
    datadog.expectRequests(1)
    client.recordValue("my-gauge", 1204)
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.my-gauge:1204|g|#environment:env")
  }

  it should "support tags for counter" in {
    datadog.expectRequests(1)
    client.increment("counter", Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.counter:1|c|#environment:env,tag:value")
  }

  it should "support tags for duration" in {
    datadog.expectRequests(1)
    client.recordDuration("duration", 45, Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.duration:45|ms|#environment:env,tag:value")
  }

  it should "support tags for gauge" in {
    datadog.expectRequests(1)
    client.recordValue("gauge", 45, Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.gauge:45|g|#environment:env,tag:value")
  }

  it should "support multiple metrics" in {
    datadog.expectRequests(2)
    client.recordValue("gauge", 45, Some("tag:value"))
    client.increment("counter", Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain allOf ("fake.datadog.agent.gauge:45|g|#environment:env,tag:value", "fake.datadog.agent.counter:1|c|#environment:env,tag:value")
  }

  it should "support awaiting for no requests" in {
    datadog.expectRequests(0)
    client.increment("unwanted")
    datadog.waitForRequest()
    datadog.lastMessages should not be empty
  }

}
