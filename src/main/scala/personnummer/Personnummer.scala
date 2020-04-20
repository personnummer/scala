package personnummer

import scala.util.matching.Regex
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class Personnummer {

  /**
    * Personnummer century.
    *
    * @var String
    */
  var century: String = ""

  /**
    * Personnummer year.
    *
    * @var String
    */
  var year: String = ""

  /**
    * Personnummer full year.
    *
    * @var String
    */
  var fullYear: String = ""

  /**
    * Personnummer month.
    *
    * @var String
    */
  var month: String = ""

  /**
    * Personnummer day.
    *
    * @var String
    */
  var day: String = ""

  /**
    * Personnummer seperator.
    *
    * @var String
    */
  var sep: String = ""

  /**
    * Personnummer first three of the last four numbers.
    *
    * @var String
    */
  var num: String = ""

  /**
    * The last number of the personnummer.
    *
    * @var String
    */
  var check: String = ""

  /**
    * Personnummer constructor.
    *
    * @param ssn String
    */
  def this(ssn: String) = {
    this()
    parse(ssn)
  }

  /**
    * Check if a Swedish social security number is a coordination number or not.
    *
    * @return Boolean
    */
  def isCoordinationNumber(): Boolean = {
    return testDate(fullYear, month, (day.toInt - 60).toString)
  }

  /**
    * Format a Swedish social security number as one of the official formats,
    * A long format or a short format.
    *
    * If the input number could not be parsed a empty string will be returned.
    *
    * @param long Boolean
    *
    * @return String
    */
  def format(long: Boolean = false) = {
    if (long) {
      f"$century$year$month$day$num$check"
    } else {
      f"$year$month$day$sep$num$check"
    }
  }

  /**
    * Get age from a Swedish social security number.
    *
    * @return Int
    */
  def getAge(): Int = {
    var ageDay = day
    if (isCoordinationNumber()) {
      ageDay = padZero(ageDay.toInt - 60)
    }

    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val p: Period = Period.between(
      LocalDate.parse(f"${fullYear}-${month}-${ageDay}", df),
      LocalDate.now
    );
    p.getYears()
  }

  /**
    * Check if a Swedish social security number is for a female.
    *
    * @return Boolean
    */
  def isFemale(): Boolean = {
    isMale() == false
  }

  /**
    * Check if a Swedish social security number is for a male.
    *
    * @return Boolean
    */
  def isMale(): Boolean = {
    val sexDigit = num.takeRight(1)
    sexDigit.toInt % 2 == 1
  }

  /**
    * Parse Swedish social security number.
    */
  private def parse(ssn: String) = {
    val reg: Regex =
      "^(\\d{2}){0,1}(\\d{2})(\\d{2})(\\d{2})([\\-|\\+]{0,1})?(\\d{3})(\\d{0,1})$".r
    if (reg.findAllIn(ssn).toList.length == 0) {
      throw new Exception("Invalid swedish social security number")
    }

    val parts: List[String] = reg.findAllIn(ssn).subgroups.toList

    century = parts(0)
    year = parts(1)
    month = parts(2)
    day = parts(3)
    sep = parts(4)
    num = parts(5)
    check = parts(6)

    if (century == null) {
      var baseYear: Int = 0

      if (sep == "+") {
        baseYear = LocalDate.now.getYear() - 100
      } else {
        sep = "-"
        baseYear = LocalDate.now.getYear()
      }

      century =
        (baseYear - ((baseYear - year.toInt) % 100)).toString.slice(0, 2)
    } else {
      if (LocalDate.now.getYear() - (century + year).toInt < 100) {
        sep = "-";
      } else {
        sep = "+";
      }
    }

    fullYear = century + year

    if (this.valid() == false) {
      throw new Exception("Invalid swedish social security number")
    }
  }

  /**
    * Check if Swedish social security number is valid or not.
    *
    * @return Boolean
    */
  private def valid(): Boolean = {
    val valid: Boolean = lunh(year + month + day + num) == check.toInt
    valid && testDate(fullYear, month, day) || isCoordinationNumber() && valid
  }

  /**
    * Check if a Swedish social security number is for a male.
    *
    * @return Int
    */
  private def lunh(number: String): Int = {
    var sum: Int = 0

    number.toList.zipWithIndex.foreach {
      case (item, index) =>
        var v: Int = item.asDigit
        v = v * (2 - (index % 2))

        if (v > 9) {
          v = v - 9
        }
        sum += v
    }

    (sum.toDouble / 10).ceil.toInt * 10 - sum
  }

  /**
    * Add zero as first number if number is lower than 10.
    *
    * @return String
    */
  private def padZero(number: Int): String = {
    if (number < 10) {
      return "0" + number.toString
    } else {
      return number.toString
    }
  }

  /**
    * Test if the input parameters are a valid date or not.
    *
    * @return Boolean
    */
  private def testDate(year: String, month: String, day: String): Boolean = {
    try {
      val d = padZero(day.toInt)
      val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      val date: LocalDate = LocalDate.parse(f"${year}-${month}-${d}", df)
      year.toInt == date.getYear() && month.toInt == date
        .getMonthValue() && day.toInt == date.getDayOfMonth()
    } catch {
      case _: Throwable => false
    }
  }
}

object Personnummer {

  /**
    * Parse Swedish social security number.
    *
    * @param ssn String
    *
    * @return Personnummer
    */
  def parse(ssn: String): Personnummer = {
    new Personnummer(ssn)
  }

  /**
    * Check if Swedish social security number is valid or not.
    *
    * @return Boolean
    */
  def valid(ssn: String): Boolean = {
    try {
      new Personnummer(ssn)
      true
    } catch {
      case _: Throwable => false
    }
  }
}
