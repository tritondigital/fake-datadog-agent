package com.tritondigital.datadog

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}


class FakeDatadogAgentTest extends FlatSpec with Matchers with BeforeAndAfter {

  val datadog = new FakeDatadogAgent(9999)
  val client = new DatadogClient(9999, "fake.datadog.agent")

  before {
    datadog.start()
  }

  after {
    datadog.stop()
  }

  "This client" should "prefix all counters with the default prefix" in {
    client.increment("a-counter")
    client.increment("another-counter")
    datadog.waitForRequest()

    all (datadog.lastMessages) should startWith("fake.datadog.agent.")
  }

  it should "specify the environment with a tag" in {
    client.increment("counter")
    client.recordDuration("gauge", 42)
    datadog.waitForRequest()

    all (datadog.lastMessages) should endWith("|#environment:env")
  }

  it should "increment a counter" in {
    client.increment("some-counter")
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.some-counter:1|c|#environment:env")
  }

  it should "record a duration value" in {
    client.recordDuration("any-duration", 42)
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.any-duration:42|ms|#environment:env")
  }

  it should "record a gauge value" in {
    client.recordValue("my-gauge", 1204)
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.my-gauge:1204|g|#environment:env")
  }

  it should "support tags for counter" in {
    client.increment("counter", Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.counter:1|c|#environment:env,tag:value")
  }

  it should "support tags for duration" in {
    client.recordDuration("duration", 45, Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.duration:45|ms|#environment:env,tag:value")
  }

  it should "support tags for gauge" in {
    client.recordValue("gauge", 45, Some("tag:value"))
    datadog.waitForRequest()

    datadog.lastMessages should contain only("fake.datadog.agent.gauge:45|g|#environment:env,tag:value")
  }

  it should "multiple metrics " in {
    client.recordValue("gauge", 45)
    client.increment("counter")
    datadog.waitForRequest()

    datadog.lastMessages should contain allOf ("fake.datadog.agent.gauge:45|g|#environment:env", "fake.datadog.agent.counter:1|c|#environment:env")
  }
}
