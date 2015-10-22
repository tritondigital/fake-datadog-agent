# fake-datadog-agent [![Build Status](https://travis-ci.org/tritondigital/fake-datadog-agent.svg)](https://travis-ci.org/tritondigital/fake-datadog-agent)
Scala server acting as a fake datadog agent.

Use this for testing interactions with a Datadog client such as [`java-dogstats-d`](https://github.com/indeedeng/java-dogstatsd-client).

## sample usage
Browse to the [`com.tritondigital.datadog.FakeDataDogAgentTest`](src/test/scala/com/tritondigital/datadog/FakeDatadogAgentTest.scala) class to see example usage.

## building the lib
* `sbt test`
* `sbt publishLocal`
