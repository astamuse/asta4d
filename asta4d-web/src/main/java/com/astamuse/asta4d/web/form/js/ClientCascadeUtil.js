function(){
  var util = {};
  
  util.buildCascadeInvokingParams = function(jqElem) {
    var params = [];
    var value;
    for (var i = 1; i <= 9; i++) {
      value = jqElem.attr("cascade-ref-info-" + i);
      if (value) {
        params.push(value);
      } else {
        break;
      }
    }
    return params;
  }
  
  util.rewriteArrayRefAttrs = [
    "id", 
    "name",
    "cascade-ref",
    "cascade-ref-target",
    "cascade-ref-info-1",
    "cascade-ref-info-2",
    "cascade-ref-info-3",
    "cascade-ref-info-4",
    "cascade-ref-info-5",
    "cascade-ref-info-6",
    "cascade-ref-info-7",
    "cascade-ref-info-8", 
    "cascade-ref-info-9", 
  ];
  
  util.replaceArrayIndexPlaceHolder = function(s, index, cascadeLayer) {
    var reg = new RegExp("(^|.*[^@])(@{" + cascadeLayer + "})([^@].*|$)", "g");
    var ret = s.replace(reg, "$1" + index + "$3");
    return ret;
  }
  
  util.rewriteAttrs = function(elem, index, cascadeLayer) {
    if (elem.length === 0) {
      return;
    }
    for (var i = 0; i < util.rewriteArrayRefAttrs.length; i++) {
      var attr = util.rewriteArrayRefAttrs[i];
      elem
          .each(function(idx, e) {
            var jq = $(e);
            var value = jq.attr(attr);
            if (value) {
              jq.attr(attr, util.replaceArrayIndexPlaceHolder(value, index,
                  cascadeLayer));
            }
          });
    }
  }
  
  var rewriteArrayRefAttrsSelector;
  
  util.rewriteArrayRefs = function(elem, index, cascadeLayer) {
    if(!rewriteArrayRefAttrsSelector){
      rewriteArrayRefAttrsSelector = util.rewriteArrayRefAttrs.map(
          function(attr) {
            return "[" + attr + "]";
          }).join(",");
    }
    util.rewriteAttrs(elem.filter(rewriteArrayRefAttrsSelector), index, cascadeLayer);
    util.rewriteAttrs(elem.find(rewriteArrayRefAttrsSelector), index, cascadeLayer);
  }
  
  util.addRow = function(appendTargetParentSelector,
      lengthFieldSelector, copyTemplateSelector, cascadeLayer) {
  
    if (arguments.length === 0) {
      var params = util.buildCascadeInvokingParams($(this));
      if (params.length === 0) {
        throw "addRow requires necessary parameters.";
      } else {
        util.addRow.apply(this, params);
        return;
      }
    }
    
    if(cascadeLayer === undefined){
      cascadeLayer = 1;
    }
  
    if (typeof cascadeLayer !== "number") {
      cascadeLayer = parseInt(cascadeLayer);
    }
  
    var lengthElem = $(lengthFieldSelector);
    var length = parseInt(lengthElem.val());
  
    var templateElem = $(copyTemplateSelector);
    templateElem = templateElem.clone();
    templateElem.css("display", "");
  
    util.rewriteArrayRefs(templateElem, length, cascadeLayer);
  
    $(appendTargetParentSelector).append(templateElem);
  
    lengthElem.val(length + 1);
  }
  
  util.removeRow=function(selector) {
    if (arguments.length === 0) {
      var params =util.buildCascadeInvokingParams($(this));
      if (params.length === 0) {
        throw "removeRow requires necessary parameters.";
      } else {
        util.removeRow.apply(this, params);
        return;
      }
    }
    $(selector).remove();
  }
  
  return util;
}