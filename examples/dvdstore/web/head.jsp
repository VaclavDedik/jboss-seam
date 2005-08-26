<c:url value="/images/header.gif"       var="head" />
<c:url value="/images/logos_footer.gif" var="foot" />
<c:url value="/images/curve.gif"        var="curve" />

<c:url value="/images/shopping.jpg"     var="image1" />
<c:url value="/images/carts.jpg"        var="image2" />
<c:url value="/images/couple.jpg"       var="image3" />

<table width="100%"  border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="33%">&nbsp;</td>
        <td width="752">
            <div align="center">
            <table width="752"  border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td valign="top"><img src="${head}" width="752" height="74" /></td>
                </tr>
                <c:if test="${full_header}">
                    <tr>
                        <td valign="top">
                            <table width="752" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td width="251"><img src="${image1}" width="251" height="154"/></td>
                                    <td width="251"><img src="${image2}" width="251" height="154"/></td>
                                    <td width="250"><img src="${image3}" width="250" height="154"/></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </c:if>
                <tr>
                    <td class="maincontent" valign="top">

    
