package personnummer

import scala.util.matching.Regex
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class Options(
    var allowCoordinationNumber: Boolean = true,
    var allowInterimNumber: Boolean = false
) {}

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
    * @param pin String
    */
  def this(pin: String, options: Options = new Options()) = {
    this()
    parse(pin, options)
  }

  /**
    * Check if a Swedish personal identity number is a coordination number or not.
    *
    * @return Boolean
    */
  def isCoordinationNumber(): Boolean = {
    testDate(fullYear, month, (day.toInt - 60).toString)
  }

  /**
    * Check if a Swedish personal identity number is a interim number or not.
    *
    * @return Boolean
    */
  def isInterimNumber(): Boolean = {
    !num.forall(Character.isDigit)
  }

  /**
    * Format a Swedish personal identity number as one of the official formats,
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
    * Get date from a Swedish personal identity number.
    *
    * @return Int
    */
  def getDate(): LocalDate = {
    var ageDay = day
    if (isCoordinationNumber()) {
      ageDay = padZero(ageDay.toInt - 60)
    }

    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    LocalDate.parse(f"${fullYear}-${month}-${ageDay}", df);
  }

  /**
    * Get age from a Swedish personal identity number.
    *
    * @return Int
    */
  def getAge(): Int = {
    var d = getDate();
    val p: Period = Period.between(
      d,
      LocalDate.now
    );
    p.getYears()
  }

  /**
    * Check if a Swedish personal identity number is for a female.
    *
    * @return Boolean
    */
  def isFemale(): Boolean = {
    isMale() == false
  }

  /**
    * Check if a Swedish personal identity number is for a male.
    *
    * @return Boolean
    */
  def isMale(): Boolean = {
    val sexDigit = num.takeRight(1)
    sexDigit.toInt % 2 == 1
  }

  /**
    * Parse Swedish personal identity number.
    */
  private def parse(pin: String, options: Options) = {
    val reg: Regex =
      """^(\d{2}){0,1}(\d{2})(\d{2})(\d{2})([+-]?)((?!000)\d{3}|[TRSUWXJKLMN]\d{2})(\d)$""".r

    if (reg.findAllIn(pin).toList.length == 0) {
      throw new Exception("Invalid swedish personal identity number")
    }

    val parts: List[String] = reg.findAllIn(pin).subgroups.toList

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

    if (!this.valid()) {
      throw new Exception("Invalid swedish personal identity number")
    }

    if (this.isCoordinationNumber() && !options.allowCoordinationNumber) {
      throw new Exception("Invalid swedish personal identity number")
    }

    if (this.isInterimNumber() && !options.allowInterimNumber) {
      throw new Exception("Invalid swedish personal identity number")
    }
  }

  /**
    * Check if Swedish personal identity number is valid or not.
    *
    * @return Boolean
    */
  private def valid(): Boolean = {
    var _num: String = num;
    if (isInterimNumber()) {
      _num = "1" + num.slice(1, 3)
    }
    val valid: Boolean = lunh(year + month + day + _num) == check.toInt
    valid && testDate(fullYear, month, day) || isCoordinationNumber() && valid
  }

  /**
    * Check if a Swedish personal identity number is for a male.
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
    * Parse Swedish personal identity number.
    *
    * @param pin String
    *
    * @return Personnummer
    */
  def parse(pin: String, options: Options = new Options()): Personnummer = {
    new Personnummer(pin, options)
  }

  /**
    * Check if Swedish personal identity number is valid or not.
    *
    * @return Boolean
    */
  def valid(pin: String, options: Options = new Options()): Boolean = {
    try {
      new Personnummer(pin, options)
      true
    } catch {
      case _: Throwable => false
    }
  }
}
