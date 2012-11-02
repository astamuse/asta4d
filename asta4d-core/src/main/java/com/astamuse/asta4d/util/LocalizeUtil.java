package com.astamuse.asta4d.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class LocalizeUtil {

    public static String[] getCandidatePaths(String path, Locale locale) {
        int dotIndex = path.lastIndexOf(".");
        String name = dotIndex > 0 ? path.substring(0, dotIndex) : path;
        String extension = dotIndex > 0 ? path.substring(dotIndex) : StringUtils.EMPTY;
        List<String> candidatePathList = new ArrayList<>();
        if (!StringUtils.isEmpty(locale.getCountry())) {
            candidatePathList.add(name + '_' + locale.toString() + extension);
        }
        if (!StringUtils.isEmpty(locale.getLanguage())) {
            candidatePathList.add(name + '_' + locale.getLanguage() + extension);
        }
        candidatePathList.add(path);
        return candidatePathList.toArray(new String[candidatePathList.size()]);
    }

    public static String createLocalizedKey(String str, Locale locale) {
        if (StringUtils.isEmpty(locale.toLanguageTag())) {
            return str;
        }
        return str + "::" + locale.toLanguageTag();
    }
}
