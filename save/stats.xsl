<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="exsl math">
    <xsl:template match="/">
        <xsl:element name="groups">
            <xsl:apply-templates select="./centre"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="centre">
        <xsl:for-each select="groups/group">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="group">
        <xsl:element name="group">
            <xsl:attribute name="name">
                <xsl:value-of select="name"/>
            </xsl:attribute>

            <xsl:for-each select="./people/person">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>


    <xsl:template match="person">
        <xsl:element name="person">
            <xsl:attribute name="name">
                <xsl:value-of select="name"/>
            </xsl:attribute>
            <xsl:variable name="person_name" select="name"/>
            <xsl:variable name="group_name" select="../../name"/>
            <xsl:variable name="sum"
                          select="sum(/centre/tierlists/tierlist[group/name = $group_name]/voters/voter/scores/score[person/name = $person_name]/score)"/>
            <xsl:variable name="count"
                          select="count(/centre/tierlists/tierlist[group/name = $group_name]/voters/voter/scores/score[person/name = $person_name]/score)"/>

            <xsl:element name="average">
                <xsl:value-of select="format-number($sum div $count, '#.##')"/>
            </xsl:element>

            <xsl:element name="variance">
                <xsl:variable name="squares">
                    <xsl:for-each select="/centre/tierlists/tierlist[group/name = $group_name]/voters/voter/scores/score[person/name = $person_name]/score">
                        <sq>
                            <xsl:value-of select="math:power(number(.), 2)"/>
                        </sq>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:variable name="sum-squares" select="sum(exsl:node-set($squares)/sq)"/>
                <xsl:variable name="variance"
                              select="$sum-squares div ($count - 1) - math:power($sum, 2) div ($count * ($count - 1))"/>
                <xsl:value-of select="format-number(math:sqrt($variance), '#.##')"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>