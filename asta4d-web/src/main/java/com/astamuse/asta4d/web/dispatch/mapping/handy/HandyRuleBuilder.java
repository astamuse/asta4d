package com.astamuse.asta4d.web.dispatch.mapping.handy;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSet;

/**
 * From the entry methods at {@link UrlMappingRuleSet}, there are two possible flow of rule configuration:
 * 
 * <ul>
 * <li>{@link UrlMappingRuleSet#add(String, String)}: Once the target path is configured by entry method, the only operations can be
 * performed is to set rule attributes related values, such as id, rule attribute, extra path variables, rule matcher, priority, etc.
 * {@link #buildHandyRuleWithAttrOnly(UrlMappingRule)}} returns a sub class of {@link HandyRuleWithAttrOnly} to perform this restriction.
 * <li>{@link UrlMappingRuleSet#add(String)}: If the target is not set by entry method, the following steps are enabled to guide developers
 * to configure the current rule:
 * <ol>
 * <li>remap(via {@link HandyRuleWithRemap})-> attribution related operations(via {@link HandyRuleWithAttrOnly} )
 * <li>attribution related operations (via {@link HandyRuleWithAttrAndHandler})-> handler setting(via {@link HandyRuleWithHandler}) ->
 * forward/redirect setting(via {@link HandyRuleWithForward})
 * </ol>
 * </ul>
 * 
 * {@link HandyRuleWithAttrOnly} is dependent and others follow the following extending relationship:
 * <p>
 * 
 * {@link HandyRuleWithForward} <--(extend with handler settable)-- {@link HandyRuleWithHandler} <--(extend with attr op)--
 * {@link HandyRuleWithAttrAndHandler}) <--(extend with remap op)-- {@link HandyRuleWithRemap}
 * 
 * <p>
 * 
 * This builder interface affords the default build policy of how to create the above instances with the default handy rule implementations.
 * To extend the url rule configuration DSL, developers must supply their own set of above classes' sub classes, as well as the extended
 * implementation of {@link UrlMappingRuleSet}.
 * 
 * @author e-ryu
 *
 */
public interface HandyRuleBuilder {

    @SuppressWarnings("unchecked")
    default <A extends HandyRuleAfterAddSrc<?, ?, ?>> A buildHandyRuleAfterAddSrc(UrlMappingRule rule) {
        return (A) new HandyRuleAfterAddSrc<>(rule);
    }

    @SuppressWarnings("unchecked")
    default <B extends HandyRuleAfterAttr<?, ?>> B buildHandyRuleAfterAttr(UrlMappingRule rule) {
        return (B) new HandyRuleAfterAddSrc<>(rule);
    }

    @SuppressWarnings("unchecked")
    default <C extends HandyRuleAfterHandler<?>> C buildHandyRuleAfterHandler(UrlMappingRule rule) {
        return (C) new HandyRuleAfterHandler<>(rule);
    }

    @SuppressWarnings("unchecked")
    default <D extends HandyRuleAfterAddSrcAndTarget<?>> D buildHandyRuleAfterAddSrcAndTarget(UrlMappingRule rule) {
        return (D) new HandyRuleAfterAddSrcAndTarget<>(rule);
    }

}
