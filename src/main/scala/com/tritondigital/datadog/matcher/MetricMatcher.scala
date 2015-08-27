package com.tritondigital.datadog.matcher

import org.hamcrest.{Description, Matcher, TypeSafeMatcher}

class MetricMatcher(name: String, code: String, tags: String) extends TypeSafeMatcher[String] {

  override def matchesSafely(item: String): Boolean = {
    val Array(otherName, otherCode, otherTags) = item.split('|').map(_.trim)
    otherName.matches(name) && otherCode == code && otherTags == tags
  }

  override def describeTo(description: Description): Unit = {
    description
      .appendText("a metric matching `")
        .appendText(name).appendText("|").appendText(code).appendText("|").appendText(tags)
      .appendText("`")
  }
}

object MetricMatcher {
  def metricOf(name: String, code: String, tags: String): Matcher[String] = {
    new MetricMatcher(name, code, tags)
  }
}
