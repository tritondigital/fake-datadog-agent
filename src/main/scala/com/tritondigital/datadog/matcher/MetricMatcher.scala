package com.tritondigital.datadog.matcher

import org.scalatest.matchers.{MatchResult, Matcher}

trait MetricMatcher {

  class DatadogMetricMatcher(expectedName: String, expectedCode: String, expectedTags: String*) extends Matcher[String]  {

    override def apply(other: String): MatchResult = {
      val Array(otherName, otherCode, otherTags) = other.split('|').map(_.trim)
      MatchResult(
        otherName.matches(expectedName) && otherCode == expectedCode && otherTags == expectedTags.mkString(","),
        s"""Metric did not match : name was '$otherName' expected '$expectedName' and code was '$otherCode' expected '$expectedCode' and tags was '$otherTags' expected '$expectedTags'""".stripMargin,
        s"""Metric has values $expectedName""")
    }
  }

  def matchMetric(expectedName: String, expectedCode: String, expectedTags: String*)  = {
    new DatadogMetricMatcher(expectedName, expectedCode, expectedTags:_*)
  }

  def matchEvent(expectedTitle: String, expectedText: String, expectedTags: String*)  = {
    val fullTitle = s"_e\\{${expectedTitle.length},${expectedText.length}\\}:$expectedTitle"
    new DatadogMetricMatcher(fullTitle, expectedText, expectedTags:_*)
  }
}

object MetricMatcher extends MetricMatcher