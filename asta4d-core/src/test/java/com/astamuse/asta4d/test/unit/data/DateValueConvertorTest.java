package com.astamuse.asta4d.test.unit.data;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.YearMonth;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.data.convertor.String2Date;
import com.astamuse.asta4d.data.convertor.String2Java8Instant;
import com.astamuse.asta4d.data.convertor.String2Java8LocalDateTime;
import com.astamuse.asta4d.data.convertor.String2Java8LocalTime;
import com.astamuse.asta4d.data.convertor.String2Java8YearMonth;
import com.astamuse.asta4d.data.convertor.String2JodaDateTime;
import com.astamuse.asta4d.data.convertor.String2JodaLocalDate;
import com.astamuse.asta4d.data.convertor.String2JodaLocalDateTime;
import com.astamuse.asta4d.data.convertor.String2JodaLocalTime;
import com.astamuse.asta4d.data.convertor.String2JodaYearMonth;

@Test
public class DateValueConvertorTest {

    //@formatter:off
    @DataProvider(name="string2Date")
    Object[][] string2DateData(){
        return new Object[][] { 
            {"2014-10-23T12:00:23.000+1100", new Date(2014-1900, 10-1, 23, 12, 0, 23), DateTimeZone.forOffsetHours(11)},
            
            {"2014-10-23T12:00:23.000", new Date(2014-1900, 10-1, 23, 12, 0, 23), null},
            {"2014-10-23T12:00:23", new Date(2014-1900, 10-1, 23, 12, 0, 23), null},
            {"2014-10-23", new Date(2014-1900, 10-1, 23, 0, 0, 0), null},

            {"20141023T120023.000-0900", new Date(2014-1900, 10-1, 23, 12, 0, 23),  DateTimeZone.forOffsetHours(-9)},
            {"20141023T120023.000", new Date(2014-1900, 10-1, 23, 12, 0, 23), null},
            {"20141023T120023", new Date(2014-1900, 10-1, 23, 12, 0, 23), null},
            
            {"20141023", new Date(2014-1900, 10-1, 23, 0, 0, 0), null},
            {"", null, null},
            
        };
    }
    //@formatter:on

    @Test(dataProvider = "string2Date")
    public void testString2Date(String s, Date d, DateTimeZone tz) throws Exception {
        String2Date convertor = new String2Date();
        if (tz == null) {
            Assert.assertEquals(convertor.convert(s), d);
        } else {
            long expectedTimeGMT = d.getTime();

            expectedTimeGMT += DateTimeZone.getDefault().toTimeZone().getRawOffset() - tz.toTimeZone().getRawOffset();
            Date actualDate = convertor.convert(s);

            Assert.assertEquals(actualDate, new Date(expectedTimeGMT));
        }

    }

    @Test(dataProvider = "string2Date")
    public void testString2JodaDateTime(String s, Date d, DateTimeZone tz) throws Exception {
        String2JodaDateTime convertor = new String2JodaDateTime();
        if (d == null) {
            Assert.assertEquals(convertor.convert(s), null);
        } else {
            if (tz == null) {
                Assert.assertEquals(convertor.convert(s), new DateTime(d.getTime()));
            } else {
                long expectedTimeGMT = d.getTime();

                expectedTimeGMT += DateTimeZone.getDefault().toTimeZone().getRawOffset() - tz.toTimeZone().getRawOffset();
                DateTime actualDate = convertor.convert(s);

                Assert.assertEquals(actualDate, new DateTime(expectedTimeGMT));
            }
        }
    }

    @Test(dataProvider = "string2Date")
    public void testString2Java8Instant(String s, Date d, DateTimeZone tz) throws Exception {
        String2Java8Instant convertor = new String2Java8Instant();
        Instant acturalInstant = convertor.convert(s);
        if (d == null) {
            Assert.assertEquals(acturalInstant, null);
        } else {
            if (tz == null) {
                long expectedTimeGMT = d.getTime();
                Assert.assertEquals(acturalInstant, Instant.ofEpochMilli(expectedTimeGMT));
            } else {
                long expectedTimeGMT = d.getTime();

                expectedTimeGMT += DateTimeZone.getDefault().toTimeZone().getRawOffset() - tz.toTimeZone().getRawOffset();

                Assert.assertEquals(acturalInstant, Instant.ofEpochMilli(expectedTimeGMT));
            }
        }
    }

    @Test(dataProvider = "string2Date")
    public void testString2JodaLocalDate(String s, Date d, DateTimeZone tz) throws Exception {
        String2JodaLocalDate convertor = new String2JodaLocalDate();
        if (d == null) {
            Assert.assertEquals(convertor.convert(s), null);
        } else {
            Assert.assertEquals(convertor.convert(s), new LocalDate(d.getTime()));
        }

    }

    @Test(dataProvider = "string2Date")
    public void testString2JodaLocalDateTime(String s, Date d, DateTimeZone tz) throws Exception {
        String2JodaLocalDateTime convertor = new String2JodaLocalDateTime();
        if (d == null) {
            Assert.assertEquals(convertor.convert(s), null);
        } else {
            Assert.assertEquals(convertor.convert(s), new LocalDateTime(d.getTime()));
        }
    }

    @Test(dataProvider = "string2Date")
    public void testString2Java8LocalDateTime(String s, Date d, DateTimeZone tz) throws Exception {
        String2Java8LocalDateTime convertor = new String2Java8LocalDateTime();
        if (d == null) {
            Assert.assertEquals(convertor.convert(s), null);
        } else {
            long expectedTimeGMT = d.getTime();
            expectedTimeGMT += DateTimeZone.getDefault().toTimeZone().getRawOffset();
            Assert.assertEquals(convertor.convert(s), java.time.LocalDateTime.ofEpochSecond(expectedTimeGMT / 1000, 0, ZoneOffset.UTC));
        }
    }

    //@formatter:off
    @DataProvider(name="string2Time")
    Object[][] string2TimeData(){
        return new Object[][] { 
            {"12:00:23.000+1100", LocalTime.parse("12:00:23")},
            {"12:00:23.000", LocalTime.parse("12:00:23")},
            {"12:00:23", LocalTime.parse("12:00:23")},

            {"120023.000-0900", LocalTime.parse("12:00:23")},
            {"120023.000", LocalTime.parse("12:00:23")},
            {"120023", LocalTime.parse("12:00:23")},
            
        };
    }
    //@formatter:on
    @Test(dataProvider = "string2Time")
    public void testString2JodaLocalTime(String s, LocalTime t) throws Exception {
        String2JodaLocalTime convertor = new String2JodaLocalTime();
        Assert.assertEquals(convertor.convert(s), t);
    }

    @Test(dataProvider = "string2Time")
    public void testString2Java8LocalTime(String s, LocalTime t) throws Exception {
        String2Java8LocalTime convertor = new String2Java8LocalTime();
        java.time.LocalTime jlt = java.time.LocalTime.ofNanoOfDay(((long) t.getMillisOfDay()) * 1000 * 1000);
        Assert.assertEquals(convertor.convert(s), jlt);
    }

    //@formatter:off
    @DataProvider(name="string2ym")
    Object[][] string2ymData(){
        return new Object[][] { 
            {"2014-10", YearMonth.parse("2014-10")},
            {"201410", YearMonth.parse("2014-10")},
        };
    }
    //@formatter:on
    @Test(dataProvider = "string2ym")
    public void testString2JodaYearMonth(String s, YearMonth ym) throws Exception {
        String2JodaYearMonth convertor = new String2JodaYearMonth();
        Assert.assertEquals(convertor.convert(s), ym);
    }

    @Test(dataProvider = "string2ym")
    public void testString2Java8YearMonth(String s, YearMonth ym) throws Exception {
        String2Java8YearMonth convertor = new String2Java8YearMonth();
        Assert.assertEquals(convertor.convert(s), java.time.YearMonth.of(ym.getYear(), ym.getMonthOfYear()));
    }
}
