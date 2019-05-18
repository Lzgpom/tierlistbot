<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <html>
      <body>
        <xsl:apply-templates select="./groups"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="groups">
    <xsl:for-each select="group">
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="group">
    <h1 align="center">
      <xsl:value-of select="@name"/>
    </h1>
    <table border="1" align="center">
      <tr>
        <th></th>
        <th>Name</th>
        <th>Average</th>
        <th>Variance</th>
      </tr>

      <xsl:for-each select="person">
        <xsl:sort select="average" data-type="number" order="ascending"/>
        <tr>
          <th>
            <xsl:value-of select="position()"/>
          </th>
          <th>
            <xsl:value-of select="@name"/>
          </th>
          <th>
            <xsl:value-of select="average"/>
          </th>
          <th>
            <xsl:value-of select="variance"/>
          </th>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>