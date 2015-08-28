package com.tritondigital.datadog.matcher

import org.scalatest.matchers.{MatchResult, Matcher}

trait MetricMatcher {

  class DatadogMetricMatcher(expectedName: String, expectedCode: String, expectedTags: String) extends Matcher[String]  {

    override def apply(left: String): MatchResult = {
      val Array(otherName, otherCode, otherTags) = left.split('|').map(_.trim)
      MatchResult(
        otherName.matches(expectedName) && otherCode == expectedCode && otherTags == expectedTags,
        s"""Metric did not match : name was '$otherName' expected '$expectedName' and code was '$otherCode' expected '$expectedCode' and tags was '$otherTags' expected '$expectedTags'""".stripMargin,
        s"""Metric has values $expectedName""")
    }
  }

  def matchMetric(expectedName: String, expectedCode: String, expectedTags: String) = new DatadogMetricMatcher(expectedName, expectedCode, expectedTags)
}

object MetricMatcher extends MetricMatcher