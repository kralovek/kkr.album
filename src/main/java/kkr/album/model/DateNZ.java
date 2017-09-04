package kkr.album.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import kkr.album.utils.UtilsPattern;

public class DateNZ implements Comparable<DateNZ>, Cloneable {

	private static TimeZone GMT = new SimpleTimeZone(0, "GMT");
	private static long MS_DAY = 24 * 60 * 60 * 1000;
	private static long MS_MONTH_28 = 28 * MS_DAY;
	private static long MS_MONTH_29 = 29 * MS_DAY;
	private static long MS_MONTH_30 = 30 * MS_DAY;
	private static long MS_MONTH_31 = 31 * MS_DAY;
	private static long MS_YEAR_28 = 7 * MS_MONTH_31 + 4 * MS_MONTH_30 + MS_MONTH_28;
	private static long MS_YEAR_29 = MS_YEAR_28 + MS_DAY;

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int ms;

	public DateNZ() {
		init(new GregorianCalendar());
	}

	public DateNZ(Date date) {
		Calendar calendar = new GregorianCalendar(GMT);
		calendar.setTime(date);
		init(calendar);
	}

	public void init(Calendar calendar) {
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
		ms = calendar.get(Calendar.MILLISECOND);

		TimeZone timeZone = calendar.getTimeZone();
		int msDiff = timeZone.getRawOffset();

		DateNZ dateNZ = moveMiliSeconds(-msDiff);

		year = dateNZ.getYear();
		month = dateNZ.getMonth();
		day = dateNZ.getDay();
		hour = dateNZ.getHour();
		minute = dateNZ.getMinute();
		second = dateNZ.getSecond();
		ms = dateNZ.getMs();
	}

	public DateNZ(int year, int month, int day) {
		this(year, month, day, 0, 0, 0, 0);
	}

	public DateNZ(int year, int month, int day, int hour, int minute, int second) {
		this(year, month, day, hour, minute, second, 0);
	}

	public DateNZ(int year, int month, int day, int hour, int minute, int second, int ms) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.ms = ms;
	}

	public DateNZ(String dateString, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(GMT);
		Date date;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException ex) {
			throw new IllegalArgumentException("String does not match the format", ex);
		}
		Calendar calendar = new GregorianCalendar(GMT);
		calendar.setTime(date);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
		ms = calendar.get(Calendar.MILLISECOND);
	}

	public DateNZ(String date) {
		if (!date.matches("[0-9]{4}[1-2][0-9][0-3][0-9](_[0-2][0-9][0-5][0-9][0-5][0-9](\\.[0-9]{3})?)?")) {
			throw new IllegalArgumentException("Date must be in the format yyyyMMdd, yyyyMMdd_HHmmss or yyyyMMdd_HHmmss.SSS");
		}

		try {
			year = Integer.parseInt(date.substring(0, 4));
			month = Integer.parseInt(date.substring(4, 6));
			day = Integer.parseInt(date.substring(6, 8));
			if (date.length() > 8) {
				hour = Integer.parseInt(date.substring(9, 11));
				minute = Integer.parseInt(date.substring(11, 13));
				second = Integer.parseInt(date.substring(13, 15));
			}
			if (date.length() > 15) {
				ms = Integer.parseInt(date.substring(16, 19));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Date must be in the format yyyyMMdd or yyyyMMdd_HHmmss");
		}

		if (month > 12) {
			throw new IllegalArgumentException("Date must be in the format yyyyMMdd or yyyyMMdd_HHmmss. Bad month: " + month);
		}
		{
			int dayM = daysOfMonth(year, month);

			if (day > dayM) {
				throw new IllegalArgumentException("Date must be in the format yyyyMMdd or yyyyMMdd_HHmmss. Bad day: " + day + " of month: " + month);
			}
		}

		if (day > 23) {
			throw new IllegalArgumentException("Date must be in the format yyyyMMdd or yyyyMMdd_HHmmss. Bad hour: " + hour);
		}
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public int getMs() {
		return ms;
	}

	public int compareTo(DateNZ dateNZ) {
		return //
		year < dateNZ.year
				? -1
				: year > dateNZ.year ? +1 : //
						month < dateNZ.month
								? -1
								: month > dateNZ.month ? +1 : //
										day < dateNZ.day
												? -1
												: day > dateNZ.day ? +1 : //
														hour < dateNZ.hour
																? -1
																: hour > dateNZ.hour ? +1 : //
																		minute < dateNZ.minute
																				? -1
																				: minute > dateNZ.minute ? +1 : //
																						second < dateNZ.second
																								? -1
																								: second > dateNZ.second ? +1 : //
																										ms < dateNZ.ms ? -1 : ms > dateNZ.ms ? +1 : //
																												0;
	}

	public DateNZ moveSeconds(int moveSeconds) {
		return moveMiliSeconds(moveSeconds * 1000);
	}

	public DateNZ moveMiliSeconds(int moveMiliseconds) {
		if (moveMiliseconds == 0) {
			return (DateNZ) clone();
		}

		//
		// Miliseconds
		//
		int deltaMiliseconds = moveMiliseconds % 1000;
		int moveSeconds = (moveMiliseconds - moveMiliseconds % 1000) / 1000;
		int newMilisecond = this.ms + deltaMiliseconds;

		if (newMilisecond > 1000) {
			newMilisecond -= 1000;
			moveSeconds++;
		} else if (newMilisecond < 0) {
			newMilisecond += 1000;
			moveSeconds--;
		}

		//
		// Seconds
		//
		int deltaSeconds = moveSeconds % 60;
		int moveMinutes = (moveSeconds - moveSeconds % 60) / 60;
		int newSecond = this.second + deltaSeconds;

		if (newSecond > 60) {
			newSecond -= 60;
			moveMinutes++;
		} else if (newSecond < 0) {
			newSecond += 60;
			moveMinutes--;
		}

		//
		// Minutes
		//
		int deltaMinutes = moveMinutes % 60;
		int moveHours = (moveMinutes - moveMinutes % 60) / 60;
		int newMinute = this.minute + deltaMinutes;

		if (newMinute > 60) {
			newMinute -= 60;
			moveHours++;
		} else if (newMinute < 0) {
			newMinute += 60;
			moveHours--;
		}

		//
		// Hours
		//
		int deltaHours = moveHours % 24;
		int moveDays = (moveHours - moveHours % 24) / 24;
		int newHour = this.minute + deltaHours;

		if (newHour > 24) {
			newHour -= 24;
			moveDays++;
		} else if (newHour < 0) {
			newHour += 24;
			moveDays--;
		}

		//
		// Days
		//
		int newDay = this.day + moveDays;

		int newYear = this.year;
		int newMonth = this.month;

		if (moveMiliseconds < 0) {
			for (; newDay <= 0; newDay += daysOfMonth(newYear, newMonth)) {
				newMonth--;
				if (newMonth == 0) {
					newYear--;
					if (newYear == 0) {
						newYear = -1;
					}
				}
			}
		} else {
			int currentDaysOfMonth;

			for (; newDay > (currentDaysOfMonth = daysOfMonth(newYear, newMonth)); newDay -= currentDaysOfMonth) {
				newMonth++;
				if (newMonth == 13) {
					newMonth = 1;
					newYear++;
				}
			}
		}
		DateNZ retval = new DateNZ(newYear, newMonth, newDay, newHour, newMinute, newSecond, newMilisecond);
		return retval;
	}

	/**
	 * 
	 * @param dateNZ
	 * @return
	 */
	public long durationMs(DateNZ dateNZ) {
		int retval = compareTo(dateNZ);
		if (retval == 0) {
			return 0;
		}
		DateNZ dateMin;
		DateNZ dateMax;
		long direction;

		if (retval < 0) {
			dateMin = this;
			dateMax = dateNZ;
			direction = -1;
		} else {
			dateMin = dateNZ;
			dateMax = this;
			direction = +1;
		}

		long msDifference;

		if (dateMin.getYear() == dateMax.getYear()) {
			msDifference = msOfYearCurrent(dateMax) - msOfYearCurrent(dateMin);
		} else {
			msDifference = msOfYearCurrent(dateMax) - msOfYearCurrent(dateMin) + msOfYear(dateMin.getYear());
			for (int i = dateMin.getYear() + 1; i < dateMax.getYear(); i++) {
				msDifference += msOfYear(i);
			}
		}

		return direction * msDifference;
	}

	private static long msOfYear(int year) {
		return isYear29(year) ? MS_YEAR_29 : MS_YEAR_28;
	}

	private static boolean isYear29(int year) {
		return year % 500 == 0 ? true : year % 100 == 0 ? false : year % 4 == 0;
	}

	private static long msOfYearCurrent(DateNZ date) {
		long ms = 0;
		for (int i = 1; i < date.getMonth(); i++) {
			int days = daysOfMonth(date.getYear(), i);
			ms += days * MS_DAY;
		}
		ms += (date.getDay() - 1) * MS_DAY + date.getHour() * 60 * 60 * 1000 + date.getMinute() * 60 * 1000 + date.getSecond() * 1000 + date.getMs();
		return ms;
	}

	private static int daysOfMonth(int year, int month) {
		int dayM;
		switch (month) {
			case 4 :
			case 6 :
			case 9 :
			case 11 : {
				dayM = 30;
				break;
			}
			case 2 : {
				dayM = isYear29(year) ? 29 : 28;
				break;
			}
			default :
				dayM = 31;
		}
		return dayM;
	}

	protected Object clone() {
		DateNZ retval = new DateNZ(year, month, day, hour, minute, second, ms);
		return retval;
	}

	public Date toDate() {
		Calendar calendar = new GregorianCalendar(UtilsPattern.GMT);
		calendar.set(year, month - 1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, ms);
		Date date = calendar.getTime();
		return date;
	}

	public String toString(String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(UtilsPattern.GMT);
		Date date = toDate();
		return dateFormat.format(date);
	}

	public int hashCode() {
		return (((((year * 12 + month) * 31 + day) * 24 + hour) * 60 + minute) * 60 + second) * 1000 * ms;
	}

	public String toString() {
		return String.format("%04d/%02d/%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, ms);
	}

	private DateNZ f(int moveMiliSeconds) {
		if (moveMiliSeconds < 0) {
			int newMiliSeconds = this.ms + moveMiliSeconds;
			int ms = newMiliSeconds < 0 ? 1000 + newMiliSeconds % 1000 : newMiliSeconds;
			int moveSeconds = moveMiliSeconds / 1000;

			int newSeconds = this.second + moveSeconds;
			int second = newSeconds < 0 ? 60 + newSeconds % 60 : newSeconds;
			int moveMinutes = moveSeconds / 60;

			int newMinutes = this.minute + moveMinutes;
			int minute = newMinutes < 0 ? 60 + newMinutes % 60 : newMinutes;
			int moveHours = moveMinutes / 60;

			int newHours = this.hour + moveHours;
			int hour = newHours < 0 ? 24 + newHours % 24 : newHours;
			int moveDays = moveHours / 24;

			int newDays = this.day + moveDays;

			int year = this.year;
			int month = this.month;
			int day;

			for (day = newDays; day <= 0; day += daysOfMonth(year, month)) {
				month--;
				if (month == 0) {
					year--;
					if (year == 0) {
						year = -1;
					}
				}
			}

			DateNZ retval = new DateNZ(year, month, day, hour, minute, second, ms);
			return retval;
		} else {
			int newMiliSeconds = (int) (this.ms + moveMiliSeconds);
			int ms = newMiliSeconds % 1000;
			int moveSeconds = moveMiliSeconds / 1000;

			int newSeconds = this.second + moveSeconds;
			int second = newSeconds % 60;
			int moveMinutes = moveSeconds / 60;

			int newMinutes = this.minute + moveMinutes;
			int minute = newMinutes % 60;
			int moveHours = moveMinutes / 60;

			int newHours = this.hour + moveHours;
			int hour = newHours % 24;
			int moveDays = moveHours / 24;

			int newDays = this.day + moveDays;

			int year = this.year;
			int month = this.month;
			int day;
			int currentDaysOfMonth;

			for (day = newDays; day > (currentDaysOfMonth = daysOfMonth(year, month)); day -= currentDaysOfMonth) {
				month++;
				if (month == 13) {
					month = 1;
					year++;
				}
			}

			DateNZ retval = new DateNZ(year, month, day, hour, minute, second, ms);
			return retval;
		}
	}

	public static void main(String[] argv) {
		DateNZ dateNZ = new DateNZ("20170902 020315.012", "yyyyMMdd HHmmss.SSS");

		DateNZ dateNZmoved = dateNZ.moveMiliSeconds(-390015);

		System.out.println("BEFORE: " + dateNZ);
		System.out.println("AFTER:  " + dateNZmoved);
	}
}
