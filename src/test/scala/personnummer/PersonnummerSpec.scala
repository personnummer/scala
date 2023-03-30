package personnummer

import io.circe._, io.circe.parser, io.circe.generic.auto._
import collection.mutable.Stack
import org.scalatest._
import flatspec._
import matchers._
import scala.io.Source
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

case class TestListItem(
    integer: Long,
    long_format: String,
    short_format: String,
    separated_format: String,
    separated_long: String,
    `type`: String,
    valid: Boolean,
    isMale: Boolean,
    isFemale: Boolean
) {
  def get(key: String): String = {
    key match {
      case "integer"          => integer.toString
      case "long_format"      => long_format
      case "short_format"     => short_format
      case "separated_format" => separated_format
      case "separated_long"   => separated_long
    }
  }
}

class PersonnummerTests
    extends AnyFlatSpec
    with BeforeAndAfter
    with should.Matchers {
  var testList: List[TestListItem] = _
  var availableListFormats: List[String] = List[String](
    "integer",
    "long_format",
    "short_format",
    "separated_format",
    "separated_long"
  )

  def padZero(number: Int): String = {
    if (number < 10) {
      return "0" + number.toString
    } else {
      return number.toString
    }
  }

  before {
    val url =
      "https://raw.githubusercontent.com/personnummer/meta/master/testdata/list.json"
    val result = Source.fromURL(url).mkString.stripMargin
    testList =
      parser.decode[List[TestListItem]](result).getOrElse(List[TestListItem]())
  }

  it should "test personnummer list" in {
    for (item <- testList) {
      for (format <- availableListFormats) {
        item.valid shouldEqual Personnummer.valid(item.get(format))
      }
    }
  }

  it should "test personnummer format" in {
    for (item <- testList) {
      if (item.valid) {
        for (format <- availableListFormats) {
          if (format != "short_format") {
            item.separated_format shouldEqual new Personnummer(item.get(format))
              .format()
            item.long_format shouldEqual Personnummer
              .parse(item.get(format))
              .format(true)
          }
        }
      }
    }
  }

  it should "test personnummer exceptions" in {
    for (item <- testList) {
      if (item.valid == false) {
        for (format <- availableListFormats) {
          an[Exception] should be thrownBy Personnummer.parse(item.get(format))
        }
      }
    }
  }

  it should "test personnummer sex" in {
    for (item <- testList) {
      if (item.valid) {
        for (format <- availableListFormats) {
          item.isMale shouldEqual Personnummer.parse(item.get(format)).isMale()
          item.isFemale shouldEqual Personnummer
            .parse(item.get(format))
            .isFemale()
        }
      }
    }
  }

  it should "test personnummer date" in {
    for (item <- testList) {
      if (item.valid) {
        var year = item.separated_long.slice(0, 4)
        var month = item.separated_long.slice(4, 6)
        var day = item.separated_long.slice(6, 8)

        if (item.`type` == "con") {
          day = padZero(day.toInt - 60).toString
        }

        var df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val expected: LocalDate =
          LocalDate.parse(f"${year}-${month}-${day}", df)

        for (format <- availableListFormats) {
          if (format != "short_format") {
            expected shouldEqual Personnummer.parse(item.get(format)).getDate()
          }
        }
      }
    }
  }

  it should "test personnummer age" in {
    for (item <- testList) {
      if (item.valid) {
        var year = item.separated_long.slice(0, 4)
        var month = item.separated_long.slice(4, 6)
        var day = item.separated_long.slice(6, 8)

        if (item.`type` == "con") {
          day = padZero(day.toInt - 60).toString
        }

        var df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val p: Period = Period.between(
          LocalDate.parse(f"${year}-${month}-${day}", df),
          LocalDate.now
        );
        val expected: Int = p.getYears()

        for (format <- availableListFormats) {
          if (format != "short_format") {
            expected shouldEqual Personnummer.parse(item.get(format)).getAge()
          }
        }
      }
    }
  }
}
