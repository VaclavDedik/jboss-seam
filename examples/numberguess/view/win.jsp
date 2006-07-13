<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns="http://www.w3.org/1999/xhtml"
          version="1.2">
<jsp:directive.page contentType="text/html;charset=utf-8"/>
<html>
<head>
<title>You won!</title>
</head>
<body>
<h1>You won!</h1>
<f:view>
    Yes, the answer was <h:outputText value="#{numberGuess.currentGuess}" />.
    It took you <h:outputText value="#{numberGuess.guessCount}" /> guesses.
    Would you like to <a href="numberGuess.seam">play again</a>?
  </f:view>
</body>
</html>
</jsp:root>
