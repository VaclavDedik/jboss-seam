if (!CAL_DAYS_SHORT)
  var CAL_DAYS_SHORT = "Su,Mo,Tu,We,Th,Fr,Sa";
if (!CAL_DAYS_MEDIUM)
  var CAL_DAYS_MEDIUM = "Sun,Mon,Tue,Wed,Thu,Fri,Sat";
if (!CAL_DAYS_LONG)
  var CAL_DAYS_LONG = "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday";
if (!CAL_MONTHS_MEDIUM)
  var CAL_MONTHS_MEDIUM = "Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec";
if (!CAL_MONTHS_LONG)
  var CAL_MONTHS_LONG = "January,February,March,April,May,June,July,August,September,October,November,December";
if (!CAL_DATE_FORMAT)
  var CAL_DATE_FORMAT = "mm/dd/yyyy";
if (!CAL_FIRST_DAY_OF_WEEK)
  var CAL_FIRST_DAY_OF_WEEK = 0;

Array.prototype.contains = function (element) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == element) 
      return true;
  }
  return false;
};

Array.prototype.indexNoCase = function (element) {
  for (var i = 0; i < this.length; i++) {
    if (typeof(this[i]) == "string" && this[i].toLowerCase() == element.toLowerCase()) 
      return i;
  }
  return -1;
};

String.prototype.lpad = function(padChar, len) {
	var val = this;
	while (val.length < len) 
	  val = padChar + val;
	return val;
};

function __Browser() 
{
  this.isIE    = false;
  this.isNS    = false;
  this.version = null;

  var ua, s, i;

  ua = navigator.userAgent;

  s = "MSIE";
  if ((i = ua.indexOf(s)) >= 0) 
  {
    this.isIE = true;
    this.version = parseFloat(ua.substr(i + s.length));
  }

  s = "Netscape6/";
  if ((i = ua.indexOf(s)) >= 0) 
  {
    this.isNS = true;
    this.version = parseFloat(ua.substr(i + s.length));
  }

  s = "Gecko";
  if ((i = ua.indexOf(s)) >= 0) 
  {
    this.isNS = true;
    this.version = 6.1;
  }
}

var __browser = new __Browser();

function getObject(objectId)
{
  if (document.getElementById && document.getElementById(objectId))
    return document.getElementById(objectId);
  else if (document.all && document.all(objectId))
    return document.all(objectId);
  else if (document.layers && document.layers[objectId])
    return document.layers[objectId];
  else
    return false;
}

function getStyleObject(objectId) 
{
  if(document.getElementById && document.getElementById(objectId)) 
	  return document.getElementById(objectId).style;
  else if (document.all && document.all(objectId))
  	return document.all(objectId).style;
  else if (document.layers && document.layers[objectId])
    return document.layers[objectId];
  else
	  return false;
}

function changeObjectVisibility(objectId, newVisibility) 
{
  var styleObject = getStyleObject(objectId);
  if(styleObject) 
  {
	  styleObject.visibility = newVisibility;
	  return true;
  } 
  else
    return false;
}

function __getControlDim(ctl)
{
  var width = ctl.offsetWidth;
  var height = ctl.offsetHeight;
	for (var lx = 0, ly = 0; ctl != null; lx += ctl.offsetLeft, ly += ctl.offsetTop, ctl = ctl.offsetParent);
	return {x:lx, y:ly, width:width, height:height};
}

function __CalendarFactory()
{
  var DAYS_IN_WEEK = 7;
  var MONTHS_IN_YEAR = 12;
  var POPUP_DIV = "__popupDiv";
  var calendarList = new Array();

  this.poppedCalendar = null;
  this.mouseIsDown = false;
  this.documentmousedown = false;

  this.daysShort = CAL_DAYS_SHORT.split(',');
  this.daysMedium = CAL_DAYS_MEDIUM.split(',');
  this.daysLong = CAL_DAYS_LONG.split(',');

  this.monthsMedium = CAL_MONTHS_MEDIUM.split(',');
  this.monthsLong = CAL_MONTHS_LONG.split(',');

  this.displayFormat = CAL_DATE_FORMAT.toLowerCase();
  this.entryFormats = new Array();
  this.entrySeparators = new Array();    
     
  __CalendarFactory.prototype.setDisplayFormat = function(displayFormat) { this.displayFormat = displayFormat.toLowerCase(); };
  __CalendarFactory.prototype.setEntryFormats = function(entryFormats) { this.entryFormats = entryFormats.split(','); };
  __CalendarFactory.prototype.setEntrySeparators = function(entrySeparators)  { this.entrySeparators = entrySeparators.split(','); };

  __CalendarFactory.prototype.createCalendar = function (name)
  {
    var calendarEntry = calendarList.length;
    calendarList[calendarEntry] = new __Calendar(calendarEntry + 1, name);
    return calendarList[calendarEntry];
  };

  __CalendarFactory.prototype.getCalendar = function (calendarNumber)
  {
    return calendarList[calendarNumber - 1];
  };

  __CalendarFactory.prototype.getCalendarByName = function (name)
  {
    for (var i = 0; i < calendarList.length; i++)
    {
      if (calendarList[i].name == name)
        return calendarList[i];
    }
    return null;
  };

  __CalendarFactory.prototype.popupCalendar = function (name, locX, locY)
  {
    this.poppedCalendar = this.getCalendarByName(name);
    this.getCalendarByName(name).popup(locX, locY);
  };

  __CalendarFactory.prototype.depopupCalendar = function ()
  {

    if (this.poppedCalendar)
    {
      if (this.mouseIsDown)
        return;

      var divName = this.poppedCalendar.getCalendarId() + "_months";
      if (getStyleObject(divName).visibility == "visible")
        changeObjectVisibility(divName, "hidden");

      divName = this.poppedCalendar.getCalendarId() + "_years";
      if (getStyleObject(divName).visibility == "visible")
        changeObjectVisibility(divName, "hidden");

      changeObjectVisibility(POPUP_DIV, "hidden");

      document.onmousedown = this.documentmousedown;
    }
  };

   __CalendarFactory.prototype.monthDays = function(year, month)
  {
    var days = (new Array(31,28,31,30,31,30,31,31,30,31,30,31))[month];
    return (month == 1) ? ((((year % 4 == 0) && !(year % 100 == 0)) || (year % 400 == 0)) ? (days + 1) : days) : days;
  };

   __CalendarFactory.prototype.monthDaysByDate = function(aDate)
  {
    var monthDate = new Date(aDate);
    return this.monthDays(monthDate.getFullYear(), monthDate.getMonth());
  };

  __CalendarFactory.prototype.parseDate = function (viewCtl, valueCtlName)
  {
  	if (!viewCtl) return;

    var date = this.validateDate(viewCtl.value);

    if (date)
      getObject(valueCtlName).value = date.year + "-" + date.month + "-" + date.day;
    else
    	getObject(valueCtlName).value = "";

    this.setViewDate(valueCtlName);
  };
  
  __CalendarFactory.prototype.convertInternalDate = function(internalString)
  {
    var dateElements = internalString.split("-");
    return new Date(dateElements[0], (dateElements[1] * 1) - 1, dateElements[2]);       
  };

  __CalendarFactory.prototype.getJulianDate = function (dateString) {
  	var julianDate = 0;
  	if (!dateString) return 0;

    var date = this.validateDate(dateString);

    if (date) {
    	julianDate = date.year * 1000;
    	for (var monthIdx = 0; monthIdx < date.month - 1; monthIdx ++)
    	  julianDate += this.monthDays(date.year, date.month - 1);
    	julianDate += date.day;
    }
    return julianDate;
  };

  __CalendarFactory.prototype.setViewDate = function(valueCtlName)
  {
  	var viewCtl = getObject("__" + valueCtlName + "_view");
  	var dateVal = getObject(valueCtlName).value;
  	if (dateVal.length == 0)
  	  viewCtl.value = "";
  	else
  	{
  		var dateBits = dateVal.split("-");
  		viewCtl.value = this.formatDate(parseInt(dateBits[0]), parseInt(dateBits[1]), parseInt(dateBits[2]));
  	}
  };

  __CalendarFactory.prototype.formatDate = function(year, month, day)
  {
  	var newDate = this.displayFormat;
  	newDate = newDate.replace("yyyy", ("" + year).lpad("0", 4));
  	newDate = newDate.replace("yy", ("" + year).lpad("0", 2));
  	newDate = newDate.replace("dd", ("" + day).lpad("0",2));
  	newDate = newDate.replace("d", "" + day);
  	
  	if (newDate.indexOf("mmmm") >= 0)
    	newDate = newDate.replace("mmmm", this.monthsLong[month - 1]);
	  else if (newDate.indexOf("mmm") >= 0)
    	newDate = newDate.replace("mmm", this.monthsMedium[month - 1]);
	  else if (newDate.indexOf("mm") >= 0)
    	newDate = newDate.replace("mm", ("" + month).lpad("0", 2));
	  else if (newDate.indexOf("m") >= 0)    	
    	newDate = newDate.replace("m", "" + month);    	
  	return newDate;
  };

  function getEntryElements(value, separators)
  {
    value = new String(value);
    for (var i = 0; i < value.length; i++)
    {
    	if (separators.contains(value.charAt(i)))
    	  return value.split(value.charAt(i));
    }
    return value.split(" ");
  };

  __CalendarFactory.prototype.validateDate = function(value)
  {
    var entryElements = getEntryElements(value, this.entrySeparators);
    for (var formatIdx = 0; formatIdx < this.entryFormats.length; formatIdx++)
    {
    	var entryFormat = this.entryFormats[formatIdx].split("/");
    	var validatedDate;

    	if (entryFormat.length == 1)
    	{
    		var tokenElts = new Array("d", "m", "y");
    		var valueArray = new Array();
    		var tokenArray = new Array();
    		var tokenStart = 0;
    		var tokenEnd = 0;

    		for (var eltIdx = 0; eltIdx < tokenElts.length; eltIdx++)
    		{
      		tokenStart = entryFormat[0].indexOf(tokenElts[eltIdx]);
      		if (tokenStart >= 0)
      		{
      			tokenEnd = entryFormat[0].lastIndexOf(tokenElts[eltIdx]);
      		  valueArray[valueArray.length] = entryElements[0].substring(tokenStart, tokenEnd + 1);
      		  tokenArray[tokenArray.length] = entryFormat[0].substring(tokenStart, tokenEnd + 1);
      		}
      	}

      	validatedDate = this.validateDateFormat(valueArray, tokenArray);
    	}
      else if (entryElements.length == entryFormat.length)
     	{
        validatedDate = this.validateDateFormat(entryElements, entryFormat);
     	}

     	if (validatedDate && validatedDate.isValid)
     	  return validatedDate;
    }
    return null;
  };

  __CalendarFactory.prototype.validateDateFormat = function(values, tokens)
  {
  	var isMatch = true;
  	var dayVal = 0;
  	var monthVal = 0;
  	var yearVal = 0;

 	  for (var elemIdx = 0; (elemIdx < tokens.length) && isMatch; elemIdx++)
 	  {
 	  	var token = tokens[elemIdx];
 	  	var entryVal = values[elemIdx];

	    if (token == "d" || token == "dd")
	    {
	    	dayVal = parseInt(entryVal);
	    	isMatch = (dayVal != NaN) && (dayVal >= 1);
	    }
  	  else if (token == "m" || token == "mm")
  	  {
	    	monthVal = parseInt(entryVal);
	    	isMatch = (monthVal != NaN) && (monthVal >= 1) && (monthVal <= MONTHS_IN_YEAR);
  	  }
  	  else if (token == "mmm")
  	  {
  	  	monthVal = this.monthsMedium.indexNoCase(entryVal) + 1;
  	  	isMatch = (monthVal != 0);
  	  }
  	  else if (token == "mmmm")
  	  {
  	  	monthVal = this.monthsLong.indexNoCase(entryVal) + 1;
  	  	isMatch = (monthVal != 0);
  	  }
  	  else if (token == "yy")
  	  {
	    	yearVal = parseInt(entryVal);
	    	isMatch = (yearVal != NaN) && (yearVal >= 0) && (yearVal <= 99);
	    	var currentYear = new Date().getYear() % 100;
	    	var currentCentury = Math.floor(new Date().getFullYear() / 100);
	    	if (isMatch)
	    	{
	    		if (currentYear <= 50 && yearVal > (currentYear + 50))
	    		  currentCentury--;
	    		else if (currentYear > 50 && yearVal < (currentYear - 50))
	    		  currentCentury++;

	    		yearVal = (currentCentury * 100) + yearVal;
	    	}
  	  }
  	  else if (token == "yyyy")
  	  {
	    	yearVal = parseInt(entryVal);
	    	isMatch = (yearVal != NaN) && (yearVal >= 0) && (yearVal <= 9999) && (entryVal.length == 4);
  	  }
  	}

    // Default the year to this year if none specified.
    if (yearVal == 0)
      yearVal = new Date().getFullYear();

 	  // Check day, month, year validity.  Month and day are mandatory.
	  if (isMatch)
	  	isMatch = (dayVal > 0) && (monthVal > 0) && (dayVal <= this.monthDays(yearVal, monthVal - 1));

	  return { isValid:isMatch, day:dayVal, month:monthVal, year:yearVal };
  };
}

function __Calendar(calendarNumber, name)
{
  var DAYS_IN_WEEK = 7;
  var MONTHS_IN_YEAR = 12;
  var DATE_FORMAT = "dd mmmm yyyy";
  var POPUP_DIV = "__popupDiv";

  this.calendarNumber = calendarNumber;
  this.styleClass = "seam-date";
  this.name = name;
  this.isPopup = false;
  this.mouseIsDown = false;

  var defaultDate = new Date();
  this.today = new Date(defaultDate.getFullYear(), defaultDate.getMonth(), defaultDate.getDate());
  this.selectedMonth = this.today.getMonth() + 1;
  this.selectedYear = this.today.getFullYear();
  this.startDOW = CAL_FIRST_DAY_OF_WEEK;
  this.daysOff = ["1","7"];
  this.staticCalendar = false;  

  this.dayNames = __calendarFactory.daysLong;
  this.dayHeaders = __calendarFactory.daysShort;
  this.monthNames = __calendarFactory.monthsLong;
  this.checkEventDate = null;
  
  __Calendar.prototype.setStatic = function(value) { this.staticCalendar = value; };  
  
  __Calendar.prototype.setCheckEventDateCallback = function(value) { this.checkEventDate = value; };

  __Calendar.prototype.repaint = function ()
  {
    if (this.isPopup)
    {
      getObject(POPUP_DIV).innerHTML = this.getHTML();
    }
    else
    {
      this.createContainer();
      this.calendarElement.innerHTML = this.getHTML();
    }
  };

  __Calendar.prototype.popup = function (locX, locY)
  {
    this.isPopup = true;
    __calendarFactory.documentmousedown = document.onmousedown;

    var popupDiv = getObject(POPUP_DIV);
    if (!popupDiv)
    {
      popupDiv = document.createElement('div');
      popupDiv.id = POPUP_DIV;
      popupDiv.style.position = "absolute";
      window.document.body.appendChild(popupDiv)
    }

    popupDiv.className = this.styleClass;
    getStyleObject(POPUP_DIV).left = locX + "px";
    getStyleObject(POPUP_DIV).top = locY + "px";

    popupDiv.innerHTML = this.getHTML();
    changeObjectVisibility(POPUP_DIV, "visible");
    document.onmousedown = this.documentmousedown;
    popupDiv.onmousedown = this.mousedown;
    popupDiv.onmouseup = this.mouseup;
  };

  __Calendar.prototype.mousedown = function()
  {
    __calendarFactory.mouseIsDown = true;
  };

  __Calendar.prototype.mouseup = function()
  {
    __calendarFactory.mouseIsDown = false;
  };

  __Calendar.prototype.documentmousedown = function()
  {
    __calendarFactory.depopupCalendar();
  };

  __Calendar.prototype.getHTML = function ()
  {
    var html = "";

    html += "<table width=\"100%\" cellpadding=0 cellspacing=0 border=0 style=\"" + this.styleClass + "\">";
    html += this.buildDayHeaders();
    html += this.buildDays();
    html += this.buildFooter();
    html += "</table>";

    return html;
  };

  __Calendar.prototype.setToday = function(today) { this.today = new Date(today.getFullYear(), today.getMonth(), today.getDate()); };
  __Calendar.prototype.setStyleClass = function(styleClass) { this.styleClass = styleClass; };
  __Calendar.prototype.setMonthNames = function(monthNames) { this.monthNames = monthNames; };
  __Calendar.prototype.setDayNames = function(dayNames) { this.dayNames = dayNames; };
  __Calendar.prototype.setDayHeaders = function(dayHeaders) { this.dayHeaders = dayHeaders; };
  __Calendar.prototype.setStartDOW = function(startDOW) { this.startDOW = startDOW; };
  __Calendar.prototype.setSelectedMonth = function(month) { this.selectedMonth = month; };
  __Calendar.prototype.setSelectedYear = function(year) { this.selectedYear = year; };
  __Calendar.prototype.setHighlightStart = function(highlightStart) { this.highlightStart = new Date(Math.floor(highlightStart)); };
  __Calendar.prototype.setHighlightEnd = function(highlightEnd) { this.highlightEnd = new Date(Math.floor(highlightEnd)); };
  __Calendar.prototype.setOnClickDate = function(onClickDateEvent) { this.onClickDate = onClickDateEvent; };
  __Calendar.prototype.setDaysOff = function(daysOff) { this.daysOff = daysOff.split(","); };

  __Calendar.prototype.buildDayHeaders = function()
  {
    var html = "<tr class=\"" + this.styleClass + "-header\">";
    html += "<td colspan=\"" + DAYS_IN_WEEK + "\"><table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr>";
    if (!this.staticCalendar)
      html += "<td class=\"" + this.styleClass + "-header-prevMonth\" onmouseover=\"this.style.cursor='pointer';\" onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").incMonth(-1);\"></td>";

    html += "<td class=\"" + this.styleClass + "-header\" style=\"text-align:right;\">";

    if (!this.staticCalendar)
      html += "<span onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").popupMonths();\" style=\"cursor:pointer;\">";
    
    html += this.monthNames[this.selectedMonth - 1];

    if (!this.staticCalendar)
    {
      html += "<div id=\"" + this.getCalendarId() + "_months" + "\" class=\"" + this.styleClass + "-monthNames\" style=\"position:absolute;display:block;visibility:hidden\">";
      for (var month = 0; month < this.monthNames.length; month++)
      html += "<a class=\"" + this.styleClass + "-monthNameLink\" onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").gotoMonth(" + (month + 1) + ");\">" + this.monthNames[month] + "</a>";
      html += "</div>";
      html += "</span>";
    }
      
    html += "</td>";

    html += "<td class=\"" + this.styleClass + "-header\" style=\"text-align:left;\">";
    
    if (!this.staticCalendar)
    {
      html += "<div id=\"" + this.getCalendarId() + "_years" + "\" class=\"" + this.styleClass + "-years\" style=\"position:absolute;display:block;visibility:hidden\"></div>"; 
      html += "<span onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").popupYears();\" style=\"cursor:pointer;\">" + this.selectedYear + "</span></td>";
    }
    else
      html += this.selectedYear + "</td>";

    if (!this.staticCalendar)
      html += "<td class=\"" + this.styleClass + "-header-nextMonth\" onmouseover=\"this.style.cursor='pointer';\" onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").incMonth(1);\"></td>";
      
    html += "</tr></table>";
    html += "</td></tr>";

    html += "<tr class=\"" + this.styleClass + "-headerDays\">";
    for (var day = this.startDOW; day < (DAYS_IN_WEEK + this.startDOW); day++)
      html += "<td align=\"center\">" + this.dayHeaders[day % DAYS_IN_WEEK]  + "</td>";

    html += "</tr>";

    return html;
  };

  __Calendar.prototype.buildDays = function()
  {
    var thisMonthDate = new Date(this.selectedYear, this.selectedMonth - 1, 1);
    var firstDOW = thisMonthDate.getDay();
    var previousMonthDays = __calendarFactory.monthDaysByDate(previousMonth(thisMonthDate));
    var daysInMonth = __calendarFactory.monthDays(this.selectedYear, this.selectedMonth - 1);
    var dayIdx = 0;
    var dow = this.startDOW;

    var html = "";

    var i = ((firstDOW - this.startDOW + 7) % 7) - 1;
    while (i >= 0)
      html += this.buildDay(previousMonthDays - i--, false, dayIdx++, dow++ % DAYS_IN_WEEK, -1);

    var j = 1;
    while (j <= daysInMonth)
      html += this.buildDay(j++, true, dayIdx++, dow++ % DAYS_IN_WEEK, 0);

    var k = (dayIdx % DAYS_IN_WEEK);
    var dayOfs = k;
    while ((k > 0) && (k < DAYS_IN_WEEK))
      html += this.buildDay(k++ - dayOfs + 1, false, dayIdx++, dow++ % DAYS_IN_WEEK, 1);

    return html;
  };

  __Calendar.prototype.buildDay = function (day, inMonth, dayIdx, dow, monthOffset)
  {
    var html = "";
    var inRange = false;

    var dayDate = new Date(this.selectedYear, this.selectedMonth - 1 + monthOffset, day);
    var eventDate = (this.checkEventDate && this.checkEventDate(dayDate));
    var dayOff = (this.daysOff) && (this.daysOff.contains((dow * 1) + 1)); 
    var thisDate = Math.floor(dayDate);

    if ((dayIdx % DAYS_IN_WEEK) == 0)
      html += "<tr>";

    html += "<td align=\"center\"";

    if (eventDate)
    {
      if (dayOff)
        html += " class=\"" + this.styleClass + "-eventDay-off\" "; 
      else
        html += " class=\"" + this.styleClass + "-eventDay\" "; 
    }
    else if (thisDate == Math.floor(this.today))
    {
      html += " class=\"" + this.styleClass + "-today\" ";
    }
    else if ((thisDate >= this.highlightStart) && (thisDate <= this.highlightEnd))
    {
      html += " class=\"" + this.styleClass + "-highlightDay\" ";
      inRange = true;
    }
    else if (dayOff)
    {
      if (inMonth)
        html += " class=\"" + this.styleClass + "-dayOff-inMonth\" ";
      else
        html += " class=\"" + this.styleClass + "-dayOff-outMonth\" ";
    }
    else if (!inMonth)
      html += " class=\"" + this.styleClass + "-outMonth\" ";
    else
    {
      html += " class=\"" + this.styleClass + "-inMonth\" ";
    }

    if (!inRange && (this.onClickDate) && (!this.checkEventDate || eventDate))
    {
    	html += " onclick=\"" + this.onClickDate + "('" + this.name + "'," + dayDate.getFullYear() + "," + dayDate.getMonth() + "," + day + ")";
    	if (this.isPopup)
    	  html += ";__calendarFactory.depopupCalendar()";
    	html += "\"";
    }

    html += " >" + day + "</td>";

    if ((dayIdx % DAYS_IN_WEEK) == (DAYS_IN_WEEK - 1))
      html += "</tr>";

    return html;
  };

  __Calendar.prototype.buildFooter = function()
  {
    if (!this.staticCalendar)
    {
      var html = "<tr class=\"" + this.styleClass + "-footer\"> ";
      html += "<td onmouseover=\"this.style.cursor='pointer';\" onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").gotoToday();\" colspan=\"" + DAYS_IN_WEEK + "\">" + this.formatDate(this.today) + "</td>";     
      html += "</tr>";
      return html;
    }
    else
      return "";
  };

  __Calendar.prototype.formatDate = function(aDate)
  {
    return aDate.getDate() + " " + this.monthNames[aDate.getMonth()] + " " + aDate.getFullYear();
  };

  __Calendar.prototype.getCalendarId = function()
  {
    return "__cal_" + this.calendarNumber;
  };

  __Calendar.prototype.createContainer = function()
  {
    if (!this.calendarElement)   
    {
      var calId = this.getCalendarId();
      var html = "";
      html += "<div ";
      html += "  class=\"" + this.styleClass + "\" ";
      html += "  id=\"" + calId + "\" ";
      html += "></div>";
  
      document.write(html);
      this.calendarElement = getObject(calId);
    }
  };

  function previousMonth(thisMonth)
  {
    var monthDate = new Date(thisMonth);
    var year = monthDate.getFullYear();
    var month = monthDate.getMonth();

    return new Date((month == 0) ? year - 1 : year, (month == 0) ? 11 : month - 1, 1);
  };

  function nextMonth(thisMonth)
  {
    var monthDate = new Date(thisMonth);
    var year = monthDate.getFullYear();
    var month = monthDate.getMonth();
    return new Date((month == 11) ? year + 1 : year, (month == 11) ? 0 : month + 1, 1);
  };

  __Calendar.prototype.incMonth = function(months)
  {
    var newDate = new Date(this.selectedYear, this.selectedMonth - 1, 1);
    var month = 0;

    while (month != months)
    {
      newDate = (months > 0) ? nextMonth(newDate) : previousMonth(newDate);
      month += ((months > 0) ? 1 : -1);
    }

    this.selectedMonth = newDate.getMonth() + 1;
    this.selectedYear = newDate.getFullYear();

    this.repaint();
  };

  __Calendar.prototype.incYear = function(years)
  {
    this.selectedYear += years;
    this.repaint();
  };

  __Calendar.prototype.gotoDate = function(aDate)
  {
    this.selectedMonth = aDate.getMonth() + 1;
    this.selectedYear = aDate.getFullYear();
    this.repaint();
  };

  __Calendar.prototype.gotoToday = function()
  {
    this.selectedMonth = this.today.getMonth() + 1;
    this.selectedYear = this.today.getFullYear();
    this.repaint();
  };

  __Calendar.prototype.gotoMonth = function(aMonth)
  {
    this.selectedMonth = aMonth;
    this.repaint();
  };

  __Calendar.prototype.gotoYear = function(aYear)
  {
    this.selectedYear = aYear;
    this.repaint();
  };

  __Calendar.prototype.popupMonths = function()
  {
    var divName = this.getCalendarId() + "_months";
    if (getStyleObject(divName).visibility == "visible")
      changeObjectVisibility(divName, "hidden");
    else
    {
      changeObjectVisibility(this.getCalendarId() + "_years", "hidden");
      changeObjectVisibility(divName, "visible");
    }
  };

  __Calendar.prototype.popupYears = function()
  {
    var divName = this.getCalendarId() + "_years";
    if (getStyleObject(divName).visibility == "visible")
      changeObjectVisibility(divName, "hidden");
    else
    {
      var html = "";
      for (year = this.selectedYear - 10; year <= this.selectedYear + 10; year++)
        html += "<a class=\"" + this.styleClass + "-yearLink\" onclick=\"javascript:__calendarFactory.getCalendar(" + this.calendarNumber + ").gotoYear(" + (year) + ");\">" + year + "</a>";
      getObject(divName).innerHTML = html;
      changeObjectVisibility(this.getCalendarId() + "_months", "hidden");
      changeObjectVisibility(divName, "visible");
    }
  };
}

var __calendarFactory = new __CalendarFactory();

function __clickCalendar(calName, year, month, day) {
  getObject(calName).value = __calendarFactory.formatDate(year, month + 1, day);
}

function __selectDate(calName, viewCtlName) {
  var cal = __calendarFactory.getCalendarByName(calName);

  if (!cal)
    cal = __calendarFactory.createCalendar(viewCtlName);
  cal.setOnClickDate("__clickCalendar");
  
  var ctl = getObject(viewCtlName);
  var ctlPos = __getControlDim(ctl);
  __calendarFactory.popupCalendar(calName, ctlPos.x, ctlPos.y + ctlPos.height);
}

function __clearDate(ctlName) {
  getObject(ctlName).value = "";
  __calendarFactory.setViewDate(ctlName);
}  