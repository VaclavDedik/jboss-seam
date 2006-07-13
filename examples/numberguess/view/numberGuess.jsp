<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns="http://www.w3.org/1999/xhtml"
          version="1.2">
<jsp:directive.page contentType="text/html;charset=utf-8"/>
<html>
<head>
<title>Guess a number...</title>
</head>
<body>
<h1>Guess a number...</h1>
<f:view>
	<h:form>
	    <h:messages globalOnly="true" />
	    <h:outputText value="Higher!" rendered="#{numberGuess.randomNumber gt numberGuess.currentGuess}" />
	    <h:outputText value="Lower!" rendered="#{numberGuess.randomNumber lt numberGuess.currentGuess}" />
		<br />
        I'm thinking of a number between <h:outputText value="#{numberGuess.smallest}" /> and 
        <h:outputText value="#{numberGuess.biggest}" />. You have 
        <h:outputText value="#{numberGuess.remainingGuesses}" /> guesses.
        <br />
        Your guess: 
        <h:inputText value="#{numberGuess.currentGuess}" id="guess" required="true">
            <f:validateLongRange
                maximum="#{numberGuess.biggest}" 
                minimum="#{numberGuess.smallest}"/>
        </h:inputText>
		<h:commandButton type="submit" value="Guess" action="guess" />
		<br/>
        <h:message for="guess" style="color: red"/>
	</h:form>
</f:view>
</body>
</html>
</jsp:root>
