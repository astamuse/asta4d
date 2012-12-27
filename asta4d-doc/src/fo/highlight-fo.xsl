<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                                xmlns:xslthl="http://xslthl.sf.net"
                                exclude-result-prefixes="xslthl"
                                version='1.0'>

        <xsl:template match='xslthl:keyword'>
                <fo:block font-weight="bold" color="#7F0055"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:comment'>
                <fo:block font-style="italic" color="#3F5F5F"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:oneline-comment'>
                <fo:block font-style="italic" color="#3F5F5F"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:multiline-comment'>
                <fo:block font-style="italic" color="#3F5FBF"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:tag'>
                <fo:block  color="#3F7F7F"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:attribute'>
                <fo:block olor="#7F007F"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:value'>
                <fo:block color="#2A00FF"><xsl:apply-templates/></fo:block>
        </xsl:template>

        <xsl:template match='xslthl:string'>
                <fo:block color="#2A00FF"><xsl:apply-templates/></fo:block>
        </xsl:template>

</xsl:stylesheet>