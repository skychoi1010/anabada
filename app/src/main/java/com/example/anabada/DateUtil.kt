package com.example.anabada


import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object DateUtil {

    // 날짜만 타임스탬프 변환 2020-01-01 - timestamp
    fun String.convertDateToTimestamp(): Long =
            SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(this).time

    fun Long.convertTimestampToDate(): String = DateFormat.format("yyyy-MM-dd", this).toString()

    fun Long.convertTimestampToPointFullDate(): String =
            DateFormat.format("yyyy.MM.dd", this).toString()

    // 날짜,시간,분 포함된 타임스탬프 변환 2020-01-01-22-30 - timestamp
    fun String.convertDateFullToTimestamp(): Long =
            SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.KOREA).parse(this).time

    fun Long.convertTimestampToDateFull(): String =
            DateFormat.format("yyyy-MM-dd-HH:mm", this).toString()

    fun Long.convertCurrentTimestampToDateTimestamp(): Long =
            this.convertTimestampToDate().convertDateToTimestamp()

    // timestamp -> 13:40
    fun Long.convertTimestampToTime(): String = DateFormat.format("HH:mm", this).toString()

    //fun Long.convertTimesToTimestamp(): Long = DateFormat.format("HH:mm", this).toString()

    // 시간 한자리면 앞에 0 붙여주어 변환
    fun String.convertHourDoubleDigit(): String = if (this.length < 2) "0$this" else this

    // 분 한자리면 앞에 0 붙여주어 반환
    fun String.convertMinuteDoubleDigit(): String = if (this.length < 2) "0$this" else this

    // 한자리 숫자면 두자리로 변환
    fun String.convertSingleToDoubleDigit(): String = if (this.length < 2) "0$this" else this

    fun Long.convertTimestampToHour(): Int = DateFormat.format("HH", this).toString().toInt()

    fun Long.convertTimestampToMinute(): Int = DateFormat.format("mm", this).toString().toInt()

    fun Int.convertNextHour(): Int = if (this == 23) 0 else this + 1

    fun Int.convertNextMinute(): Int = if (this == 59) 0 else this + 1

    // 현재 Year
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    // 현재 Month
    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    // 현재 Day
    fun getCurrentDay(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // timestamp -> year
    fun Long.convertTimestampToYear(): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = this
        return cal[Calendar.YEAR]
    }

    // timestamp -> month
    fun Long.convertTimestampToMonth(): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = this
        return cal[Calendar.MONTH] + 1
    }

    // timestamp -> day
    fun Long.convertTimestampToDay(): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = this
        return cal[Calendar.DAY_OF_MONTH]
    }


    // 1시간 뒤 타임스탬프
    fun Long.convertNextHourTimestamp(): Long = this + (60 * 60 * 1000)

    // 2020, 1, 20 -> timestamp
    fun convertDateToTimestamp(_year: Int, _month: Int, _day: Int): Long {
        val month = _month.toString().convertSingleToDoubleDigit().toInt()
        val day = _day.toString().convertSingleToDoubleDigit().toInt()
        val date = "$_year-$month-$day"
        return date.convertDateToTimestamp()
    }

    // timestamp -> 9.6~9.8
    fun convertTimestampToTerm(startTimestamp: Long, endTimestamp: Long): String {
        return DateFormat.format("MM-dd", startTimestamp)
                .toString() + "~" + DateFormat.format("MM-dd", endTimestamp).toString()
    }

    // FCM 메시지로 사용
    fun convertTimeToFcmMessage(date: Long, startTime: Long): String =
            date.convertTimestampToDate() + " " + startTime.convertTimestampToTime() + "에 회의실 예약이 있습니다."

    fun combineTimestamp(x: Long, y: Long) = (x.toString() + y.toString()).toLong()

    // 랜덤키값
    fun getRandomKey(): Long = Random(System.currentTimeMillis()).nextLong(100000, 999999)

    // 타임스탬프
    fun getTimestamp(): Long = System.currentTimeMillis()


    /////////////joda/////////////////
//    fun dateNow() : Date {
//        return Date(System.currentTimeMillis())
//    }
//
//    /**
//     * yyyy년 MM월 dd일 HH(24), hh(12)시 mm분 ss초  aa:AM/PM
//     */
//    fun dateNow(pattern: String) : Date {
//        return strParseToDate(pattern, dateFormatToStr(pattern, dateNow()))
//    }
//
//    fun patternAndMillisToStr(pattern: String, millis: Long) : String {
//        return dateFormatToStr(pattern, patternAndMillisToDate(pattern, millis))
//    }
//
//    fun patternAndMillisToDate(pattern: String, millis: Long) : Date {
//        return strParseToDate(pattern, dateFormatToStr(pattern, Date(millis)))
//    }
//
//    fun formatToString(pattern: String, date: String): String {
//        return SimpleDateFormat(pattern, Locale.getDefault()).format(strParseToDate(pattern, date))
//    }
//
//    fun dateFormatToStr(pattern: String, date: Date) : String {
//        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
//    }
//
//    fun dateFormatToStr(pattern: String, date: Date, locale: Locale) : String {
//        return SimpleDateFormat(pattern, locale).format(date)
//    }
//
//    fun strParseToDate(pattern: String, dateStr: String) : Date {
//        return SimpleDateFormat(pattern, Locale.getDefault()).parse(dateStr)
//    }
//
//    fun timeStampParseToStr(pattern: String, timeStamp: String) : String {
//        val date: Date = strParseToDate("yyyy-MM-dd'T'HH:mm:ss.SSS", timeStamp)
//        return dateFormatToStr(pattern, date, Locale.getDefault())
//    }
//
//    fun timeStampParseToStr(pattern: String, timeStamp: String, locale: Locale) : String {
//        val date: Date = strParseToDate("yyyy-MM-dd'T'HH:mm:ss.SSS", timeStamp)
//        return dateFormatToStr(pattern, date, locale)
//    }
//
//    fun millisToSecond(millis: Long) : Long = millis / 1000L
//
//    fun millisToMinute(millis: Long) : Long = millisToSecond(millis) / 60L
//
//    fun millisToHours(millis: Long) : Long = millisToMinute(millis) / 60L
//
//    fun millisToDays(millis: Long) : Long = millisToHours(millis) / 24L
//
//    fun timeZoneStandardTime() : String {
//        return TimeZone.getTimeZone(TimeZone.getDefault().id).getDisplayName(false, TimeZone.SHORT)
//    }
//
//    fun startMonth(date: DateTime): DateTime =
//            DateTime(date.year,
//                    date.monthOfYear,
//                    date.dayOfMonth().minimumValue,
//                    date.hourOfDay().minimumValue,
//                    date.minuteOfHour().minimumValue,
//                    date.secondOfMinute().minimumValue,
//                    date.millisOfSecond().minimumValue
//            )
//
//    fun endMonth(date: DateTime): DateTime =
//            DateTime(date.year,
//                    date.monthOfYear,
//                    date.dayOfMonth().maximumValue,
//                    date.hourOfDay().maximumValue,
//                    date.minuteOfHour().maximumValue,
//                    date.secondOfMinute().maximumValue,
//                    date.millisOfSecond().maximumValue
//            )
//
//    fun startDay(date: DateTime): DateTime =
//            DateTime(date.year,
//                    date.monthOfYear,
//                    date.dayOfMonth,
//                    date.hourOfDay().minimumValue,
//                    date.minuteOfHour().minimumValue,
//                    date.secondOfMinute().minimumValue,
//                    date.millisOfSecond().minimumValue
//            )
//
//    fun endDay(date: DateTime): DateTime =
//            DateTime(date.year,
//                    date.monthOfYear,
//                    date.dayOfMonth,
//                    date.hourOfDay().maximumValue,
//                    date.minuteOfHour().maximumValue,
//                    date.secondOfMinute().maximumValue,
//                    date.millisOfSecond().maximumValue
//            )
//
//    fun startHour(date: DateTime): DateTime =
//            DateTime(date.year,
//                    date.monthOfYear,
//                    date.dayOfMonth,
//                    date.hourOfDay,
//                    date.minuteOfHour().minimumValue,
//                    date.secondOfMinute().minimumValue,
//                    date.millisOfSecond().minimumValue
//            )
//
//    fun endHour(date: DateTime): DateTime =
//            DateTime(date.year,
//                    date.monthOfYear,
//                    date.dayOfMonth,
//                    date.hourOfDay,
//                    date.minuteOfHour().maximumValue,
//                    date.secondOfMinute().maximumValue,
//                    date.millisOfSecond().maximumValue
//            )
//

}