<localEntry key="request_transform.xslt">
    <xsl:stylesheet version="2.0"
                    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                    xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                    xmlns:m0="http://services.samples"
                    exclude-result-prefixes="m0 fn">
        <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

        <xsl:template match="@* | node()">
            <xsl:copy>
                <xsl:apply-templates select="@* | node()"/>
            </xsl:copy>
        </xsl:template>

        <xsl:template match="m0:CheckPriceRequest">

            <m:getQuote xmlns:m="http://services.samples">
                <m:request>
                    <m:symbol>
                        <xsl:value-of select="m0:Code"/>
                    </m:symbol>
                </m:request>
            </m:getQuote>

        </xsl:template>
    </xsl:stylesheet>
</localEntry>