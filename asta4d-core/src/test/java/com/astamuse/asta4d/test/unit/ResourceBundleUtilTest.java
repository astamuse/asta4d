package com.astamuse.asta4d.test.unit;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.format.NamedPlaceholderFormatter;
import com.astamuse.asta4d.format.NumberPlaceholderFormatter;
import com.astamuse.asta4d.format.SymbolPlaceholderFormatter;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.ResourceBundleHelperBase.ParamMapResourceBundleHelper;
import com.astamuse.asta4d.util.ResourceBundleHelperBase.ResourceBundleHelper;
import com.astamuse.asta4d.util.ResourceBundleUtil;

public class ResourceBundleUtilTest extends BaseTest {

    @Test
    public void useHelperDefault() throws Exception {
        setUp("symbol_placeholder_messages");
        ResourceBundleHelper helper = ResourceBundleUtil.getHelper();
        assertEquals(helper.getMessage("weatherreport1", "Tomorrow", "sunny"), "Tomorrow's weather is sunny.");
        assertEquals(
                helper.getMessage("weatherreport2", helper.getExternalParamValue("weatherreport2", "date"),
                        helper.getExternalParamValue("weatherreport2", "weather")), "Today's weather is cloudy.");
    }

    @Test
    public void useSymbolFormatter() throws Exception {
        setUp("symbol_placeholder_messages");
        ResourceBundleHelper helper = ResourceBundleUtil.getHelper().formatter(new SymbolPlaceholderFormatter());
        assertEquals(helper.getMessage("weatherreport1", "Tomorrow", "sunny"), "Tomorrow's weather is sunny.");
        assertEquals(
                helper.getMessage("weatherreport2", helper.getExternalParamValue("weatherreport2", "date"),
                        helper.getExternalParamValue("weatherreport2", "weather")), "Today's weather is cloudy.");
    }

    @Test
    public void useSymbolFormatterJaJp() throws Exception {
        setUp("symbol_placeholder_messages");
        ResourceBundleHelper helper = ResourceBundleUtil.getHelper().formatter(new SymbolPlaceholderFormatter()).locale(Locale.JAPAN);
        assertEquals(helper.getMessage("weatherreport1", "明日", "晴れ"), "明日の天気は晴れです。");
        assertEquals(
                helper.getMessage("weatherreport2", helper.getExternalParamValue("weatherreport2", "date"),
                        helper.getExternalParamValue("weatherreport2", "weather")), "今日の天気は曇りです。");
    }

    @Test
    public void useNumberFormatter() throws Exception {
        setUp("number_placeholder_messages");
        ResourceBundleHelper helper = ResourceBundleUtil.getHelper().formatter(new NumberPlaceholderFormatter());
        assertEquals(helper.getMessage("weatherreport1", "Tomorrow", "sunny"), "Tomorrow's weather is sunny.");
        assertEquals(
                helper.getMessage("weatherreport2", helper.getExternalParamValue("weatherreport2", "date"),
                        helper.getExternalParamValue("weatherreport2", "weather")), "Today's weather is cloudy.");
    }

    @Test
    public void useNumberFormatterJaJp() throws Exception {
        setUp("number_placeholder_messages");
        ResourceBundleHelper helper = ResourceBundleUtil.getHelper().formatter(new NumberPlaceholderFormatter()).locale(Locale.JAPAN);
        assertEquals(helper.getMessage("weatherreport1", "明日", "晴れ"), "明日の天気は晴れです。");
        assertEquals(
                helper.getMessage("weatherreport2", helper.getExternalParamValue("weatherreport2", "date"),
                        helper.getExternalParamValue("weatherreport2", "weather")), "今日の天気は曇りです。");
    }

    @Test
    public void useNamedFormatter() throws Exception {
        setUp("named_placeholder_messages");
        ParamMapResourceBundleHelper helper = ResourceBundleUtil.getParamMapHelper().formatter(new NamedPlaceholderFormatter());
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("date", "Tomorrow");
            params.put("weather", "sunny");
            assertEquals(helper.getMessage("weatherreport1", params), "Tomorrow's weather is sunny.");
        }
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("date", helper.getExternalParamValue("weatherreport2", "date"));
            params.put("weather", helper.getExternalParamValue("weatherreport2", "weather"));
            assertEquals(helper.getMessage("weatherreport2", params), "Today's weather is cloudy.");
        }
    }

    @Test
    public void useNamedFormatterJaJp() throws Exception {
        setUp("named_placeholder_messages");
        ParamMapResourceBundleHelper helper = ResourceBundleUtil.getParamMapHelper().formatter(new NamedPlaceholderFormatter())
                .locale(Locale.JAPAN);
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("date", "明日");
            params.put("weather", "晴れ");
            assertEquals(helper.getMessage("weatherreport1", params), "明日の天気は晴れです。");
        }
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("date", helper.getExternalParamValue("weatherreport2", "date"));
            params.put("weather", helper.getExternalParamValue("weatherreport2", "weather"));
            assertEquals(helper.getMessage("weatherreport2", params), "今日の天気は曇りです。");
        }
    }

    private void setUp(String fileName) {
        Context context = Context.getCurrentThreadContext();
        context.setCurrentLocale(Locale.US);
        context.getConfiguration().setResourceNames("com.astamuse.asta4d.test.render.messages." + fileName);
    }
}
