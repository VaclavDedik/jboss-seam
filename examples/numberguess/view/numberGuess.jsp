<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
<head>
<title>Guess a number...</title>
</head>
<body>
<h1>Guess a number...</h1>
<f:view>
	<h:form>
	    <h:messages globalOnly="true" />
	    <h:outputText value="Higher!" rendered="#{numberGuess.randomNumber>numberGuess.currentGuess}" />
	    <h:outputText value="Lower!" rendered="#{numberGuess.randomNumber<numberGuess.currentGuess}" />
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
