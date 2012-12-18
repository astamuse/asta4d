<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">
  <!-- import the main stylesheet, here pointing to fo/docbook.xsl -->
  <xsl:import href="urn:docbkx:stylesheet"/>
  <!-- highlight.xsl must be imported in order to enable highlighting support, highlightSource=1 parameter
   is not sufficient -->
  <xsl:import href="urn:docbkx:stylesheet/highlight.xsl"/>
  <xsl:param name="highlight.source" select="1"/>

    <xsl:param name="chapter.autolabel">1</xsl:param>
    <xsl:param name="section.autolabel" select="1"/>
    <xsl:param name="section.label.includes.component.label" select="1"/>  
</xsl:stylesheet>