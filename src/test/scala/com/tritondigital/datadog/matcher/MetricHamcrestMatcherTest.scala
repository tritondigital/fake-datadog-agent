package com.tritondigital.datadog.matcher

import com.tritondigital.datadog.matcher.MetricHamcrestMatcher.{eventOf, metricOf}
import org.scalatest.{FlatSpec, Matchers}

class MetricHamcrestMatcherTest extends FlatSpec with Matchers {

  "This matcher" should "match a metric with same attributes" in {
    metricOf("name", "code", "tags").matches("name|code|tags") shouldBe true
  }

  it should "match a metric with regex on name" in {
    metricOf("datadog.metric.\\p{Digit}+", "code", "tags").matches("datadog.metric.45|code|tags") shouldBe true
  }

  it should "not match a metric with different name" in {
    metricOf("name", "code", "tags").matches("different|code|tags") shouldBe false
  }

  it should "not match a metric with different code" in {
    metricOf("name", "code", "tags").matches("name|different|tags") shouldBe false
  }

  it should "not match a metric with different tags" in {
    metricOf("name", "code", "tags").matches("name|tags|different") shouldBe false
  }

  it should "not match a metric with all different attributes" in {
    metricOf("name", "code", "tags").matches("riri|fifi|loulou") shouldBe false
  }

  it should "match an event with the same attributes" in {
    eventOf("prefix", "title", "text", "tags").matches("_e{12,4}:prefix.title|text|tags") shouldBe true
  }
}
