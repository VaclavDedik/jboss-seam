/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.JSF;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public abstract class HtmlMessageTagBase
        extends HtmlComponentTagBase
{
    //private static final Log log = LogFactory.getLog(HtmlOutputFormatTag.class);

    // UIComponent attributes --> already implemented in UIComponentTagBase

    // user role attributes --> already implemented in UIComponentTagBase

    // HTML universal attributes --> already implemented in HtmlComponentTagBase

    // HTML event handler attributes --> already implemented in HtmlComponentTagBase

    // UIMessage attributes
    private String _for;
    private String _showSummary;
    private String _showDetail;

    // HtmlOutputMessage attributes
    private String _infoClass;
    private String _infoStyle;
    private String _warnClass;
    private String _warnStyle;
    private String _errorClass;
    private String _errorStyle;
    private String _fatalClass;
    private String _fatalStyle;
    private String _tooltip;

    public void release() {
        super.release();
        _for=null;
        _showSummary=null;
        _showDetail=null;
        _infoClass=null;
        _infoStyle=null;
        _warnClass=null;
        _warnStyle=null;
        _errorClass=null;
        _errorStyle=null;
        _fatalClass=null;
        _fatalStyle=null;
        _tooltip=null;
    }

    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);

        setStringProperty(component, JSF.FOR_ATTR, _for);
        setBooleanProperty(component, JSF.SHOW_SUMMARY_ATTR, _showSummary);
        setBooleanProperty(component, JSF.SHOW_DETAIL_ATTR, _showDetail);

        setStringProperty(component, JSF.INFO_CLASS_ATTR, _infoClass);
        setStringProperty(component, JSF.INFO_STYLE_ATTR, _infoStyle);
        setStringProperty(component, JSF.WARN_CLASS_ATTR, _warnClass);
        setStringProperty(component, JSF.WARN_STYLE_ATTR, _warnStyle);
        setStringProperty(component, JSF.ERROR_CLASS_ATTR, _errorClass);
        setStringProperty(component, JSF.ERROR_STYLE_ATTR, _errorStyle);
        setStringProperty(component, JSF.FATAL_CLASS_ATTR, _fatalClass);
        setStringProperty(component, JSF.FATAL_STYLE_ATTR, _fatalStyle);
        setBooleanProperty(component, JSF.TOOLTIP_ATTR, _tooltip);
    }

    public void setFor(String aFor)
    {
        _for = aFor;
    }

    public void setShowSummary(String showSummary)
    {
        _showSummary = showSummary;
    }

    public void setShowDetail(String showDetail)
    {
        _showDetail = showDetail;
    }

    public void setErrorClass(String errorClass)
    {
        _errorClass = errorClass;
    }

    public void setErrorStyle(String errorStyle)
    {
        _errorStyle = errorStyle;
    }

    public void setFatalClass(String fatalClass)
    {
        _fatalClass = fatalClass;
    }

    public void setFatalStyle(String fatalStyle)
    {
        _fatalStyle = fatalStyle;
    }

    public void setInfoClass(String infoClass)
    {
        _infoClass = infoClass;
    }

    public void setInfoStyle(String infoStyle)
    {
        _infoStyle = infoStyle;
    }

    public void setWarnClass(String warnClass)
    {
        _warnClass = warnClass;
    }

    public void setWarnStyle(String warnStyle)
    {
        _warnStyle = warnStyle;
    }

    public void setTooltip(String tooltip)
    {
        _tooltip = tooltip;
    }
}
