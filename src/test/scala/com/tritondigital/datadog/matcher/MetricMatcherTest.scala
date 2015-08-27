package com.tritondigital.datadog.matcher

import org.scalatest.{FlatSpec, Matchers}

class MetricMatcherTest extends FlatSpec with Matchers {

  "This matcher" should "match a metric with same attributes" in {

    val matcher = new MetricMatcher("name", "code", "tags")
    matcher.matches("name|code|tags") shouldBe true
  }

  it should "match a metric with regex on name" in {
    val matcher = new MetricMatcher("datadog.metric.\\p{Digit}+", "code", "tags")
    matcher.matches("datadog.metric.45|code|tags") shouldBe true
  }

  it should "not match a metric with different name" in {

    val matcher = new MetricMatcher("name", "code", "tags")
    matcher.matches("different|code|tags") shouldBe false
  }

  it should "not match a metric with different code" in {

    val matcher = new MetricMatcher("name", "code", "tags")
    matcher.matches("name|different|tags") shouldBe false
  }

  it should "not match a metric with different tags" in {

    val matcher = new MetricMatcher("name", "code", "tags")
    matcher.matches("name|tags|different") shouldBe false
  }

  it should "not match a metric with all different attributes" in {

    val matcher = new MetricMatcher("name", "code", "tags")
    matcher.matches("riri|fifi|loulou") shouldBe false
  }
}
