package com.tritondigital.datadog.matcher

import com.tritondigital.datadog.matcher.MetricMatcher._
import org.scalatest.{FlatSpec, Matchers}

class MetricHamcrestTest extends FlatSpec with Matchers {

  "This matcher" should "match a metric with same attributes" in {
    "name|code|tags" should matchMetric("name", "code", "tags")
  }

  it should "match an event with title and text" in {
    "_e{12,4}:prefix.title|text|tag1:value1" should matchEvent("prefix.title", "text", "tag1:value1")
  }

  it should "match a metric with regex on name" in {
    "datadog.metric.45|code|tags" should matchMetric("datadog.metric.\\p{Digit}+", "code", "tags")
  }

  it should "not match a metric with different name" in {
    "different|code|tags" shouldNot matchMetric("name", "code", "tags")
  }

  it should "not match a metric with different code" in {
    "name|different|tags" shouldNot matchMetric("name", "code", "tags")
  }

  it should "not match a metric with different tags" in {
    "name|tags|different" shouldNot matchMetric("name", "code", "tags")
  }

  it should "not match a metric with all different attributes" in {
    "riri|fifi|loulou" shouldNot matchMetric("name", "code", "tags")
  }
}
