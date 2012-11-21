package com.astamuse.asta4d.web.annotation;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.web.WebApplicationContext;

@ContextData(scope = WebApplicationContext.SCOPE_COOKIE)
public @interface CookieData {

}
