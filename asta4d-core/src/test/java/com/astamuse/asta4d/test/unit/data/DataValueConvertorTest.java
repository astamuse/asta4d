/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.test.unit.data;

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
import com.astamuse.asta4d.data.convertor.String2JodaDateTime;
import com.astamuse.asta4d.data.convertor.String2JodaLocalDate;
import com.astamuse.asta4d.data.convertor.String2JodaLocalDateTime;
import com.astamuse.asta4d.data.convertor.String2JodaLocalTime;
import com.astamuse.asta4d.data.convertor.String2JodaYearMonth;

@Test
public class DataValueConvertorTest {

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
}
