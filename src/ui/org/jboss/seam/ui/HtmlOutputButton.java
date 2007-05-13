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
package org.jboss.seam.ui;

import javax.el.ValueExpression;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 * see Javadoc of <a href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/api/index.html">JSF Specification</a>
 *
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class HtmlOutputButton
        extends UIOutput
{
    //------------------ GENERATED CODE BEGIN (do not modify!) --------------------

    public static final String COMPONENT_TYPE = "javax.faces.HtmlCommandButton";
    private static final String DEFAULT_RENDERER_TYPE = null;
    private static final boolean DEFAULT_DISABLED = false;
    private static final boolean DEFAULT_READONLY = false;
    private static final String DEFAULT_TYPE = "button";

    private String _accesskey = null;
    private String _alt = null;
    private String _dir = null;
    private Boolean _disabled = null;
    private String _image = null;
    private String _lang = null;
    private String _onblur = null;
    private String _onchange = null;
    private String _onclick = null;
    private String _ondblclick = null;
    private String _onfocus = null;
    private String _onkeydown = null;
    private String _onkeypress = null;
    private String _onkeyup = null;
    private String _onmousedown = null;
    private String _onmousemove = null;
    private String _onmouseout = null;
    private String _onmouseover = null;
    private String _onmouseup = null;
    private String _onselect = null;
    private Boolean _readonly = null;
    private String _style = null;
    private String _styleClass = null;
    private String _tabindex = null;
    private String _title = null;
    private String _type = null;

    public HtmlOutputButton()
    {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }


    public void setAccesskey(String accesskey)
    {
        _accesskey = accesskey;
    }

    public String getAccesskey()
    {
        if (_accesskey != null) return _accesskey;
        ValueExpression vb = getValueExpression("accesskey");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setAlt(String alt)
    {
        _alt = alt;
    }

    public String getAlt()
    {
        if (_alt != null) return _alt;
        ValueExpression vb = getValueExpression("alt");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setDir(String dir)
    {
        _dir = dir;
    }

    public String getDir()
    {
        if (_dir != null) return _dir;
        ValueExpression vb = getValueExpression("dir");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setDisabled(boolean disabled)
    {
        _disabled = Boolean.valueOf(disabled);
    }

    public boolean isDisabled()
    {
        if (_disabled != null) return _disabled.booleanValue();
        ValueExpression vb = getValueExpression("disabled");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext().getELContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_DISABLED;
    }

    public void setImage(String image)
    {
        _image = image;
    }

    public String getImage()
    {
        if (_image != null) return _image;
        ValueExpression vb = getValueExpression("image");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setLang(String lang)
    {
        _lang = lang;
    }

    public String getLang()
    {
        if (_lang != null) return _lang;
        ValueExpression vb = getValueExpression("lang");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnblur(String onblur)
    {
        _onblur = onblur;
    }

    public String getOnblur()
    {
        if (_onblur != null) return _onblur;
        ValueExpression vb = getValueExpression("onblur");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnchange(String onchange)
    {
        _onchange = onchange;
    }

    public String getOnchange()
    {
        if (_onchange != null) return _onchange;
        ValueExpression vb = getValueExpression("onchange");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnclick(String onclick)
    {
        _onclick = onclick;
    }

    public String getOnclick()
    {
        if (_onclick != null) return _onclick;
        ValueExpression vb = getValueExpression("onclick");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOndblclick(String ondblclick)
    {
        _ondblclick = ondblclick;
    }

    public String getOndblclick()
    {
        if (_ondblclick != null) return _ondblclick;
        ValueExpression vb = getValueExpression("ondblclick");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnfocus(String onfocus)
    {
        _onfocus = onfocus;
    }

    public String getOnfocus()
    {
        if (_onfocus != null) return _onfocus;
        ValueExpression vb = getValueExpression("onfocus");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnkeydown(String onkeydown)
    {
        _onkeydown = onkeydown;
    }

    public String getOnkeydown()
    {
        if (_onkeydown != null) return _onkeydown;
        ValueExpression vb = getValueExpression("onkeydown");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnkeypress(String onkeypress)
    {
        _onkeypress = onkeypress;
    }

    public String getOnkeypress()
    {
        if (_onkeypress != null) return _onkeypress;
        ValueExpression vb = getValueExpression("onkeypress");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnkeyup(String onkeyup)
    {
        _onkeyup = onkeyup;
    }

    public String getOnkeyup()
    {
        if (_onkeyup != null) return _onkeyup;
        ValueExpression vb = getValueExpression("onkeyup");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnmousedown(String onmousedown)
    {
        _onmousedown = onmousedown;
    }

    public String getOnmousedown()
    {
        if (_onmousedown != null) return _onmousedown;
        ValueExpression vb = getValueExpression("onmousedown");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnmousemove(String onmousemove)
    {
        _onmousemove = onmousemove;
    }

    public String getOnmousemove()
    {
        if (_onmousemove != null) return _onmousemove;
        ValueExpression vb = getValueExpression("onmousemove");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnmouseout(String onmouseout)
    {
        _onmouseout = onmouseout;
    }

    public String getOnmouseout()
    {
        if (_onmouseout != null) return _onmouseout;
        ValueExpression vb = getValueExpression("onmouseout");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnmouseover(String onmouseover)
    {
        _onmouseover = onmouseover;
    }

    public String getOnmouseover()
    {
        if (_onmouseover != null) return _onmouseover;
        ValueExpression vb = getValueExpression("onmouseover");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnmouseup(String onmouseup)
    {
        _onmouseup = onmouseup;
    }

    public String getOnmouseup()
    {
        if (_onmouseup != null) return _onmouseup;
        ValueExpression vb = getValueExpression("onmouseup");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setOnselect(String onselect)
    {
        _onselect = onselect;
    }

    public String getOnselect()
    {
        if (_onselect != null) return _onselect;
        ValueExpression vb = getValueExpression("onselect");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setReadonly(boolean readonly)
    {
        _readonly = Boolean.valueOf(readonly);
    }

    public boolean isReadonly()
    {
        if (_readonly != null) return _readonly.booleanValue();
        ValueExpression vb = getValueExpression("readonly");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext().getELContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_READONLY;
    }

    public void setStyle(String style)
    {
        _style = style;
    }

    public String getStyle()
    {
        if (_style != null) return _style;
        ValueExpression vb = getValueExpression("style");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setStyleClass(String styleClass)
    {
        _styleClass = styleClass;
    }

    public String getStyleClass()
    {
        if (_styleClass != null) return _styleClass;
        ValueExpression vb = getValueExpression("styleClass");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setTabindex(String tabindex)
    {
        _tabindex = tabindex;
    }

    public String getTabindex()
    {
        if (_tabindex != null) return _tabindex;
        ValueExpression vb = getValueExpression("tabindex");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setTitle(String title)
    {
        _title = title;
    }

    public String getTitle()
    {
        if (_title != null) return _title;
        ValueExpression vb = getValueExpression("title");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : null;
    }

    public void setType(String type)
    {
        _type = type;
    }

    public String getType()
    {
        if (_type != null) return _type;
        ValueExpression vb = getValueExpression("type");
        return vb != null ? JSF.getStringValue(getFacesContext(), vb) : DEFAULT_TYPE;
    }

    @Override
    public Object saveState(FacesContext context)
    {
        Object values[] = new Object[27];
        values[0] = super.saveState(context);
        values[1] = _accesskey;
        values[2] = _alt;
        values[3] = _dir;
        values[4] = _disabled;
        values[5] = _image;
        values[6] = _lang;
        values[7] = _onblur;
        values[8] = _onchange;
        values[9] = _onclick;
        values[10] = _ondblclick;
        values[11] = _onfocus;
        values[12] = _onkeydown;
        values[13] = _onkeypress;
        values[14] = _onkeyup;
        values[15] = _onmousedown;
        values[16] = _onmousemove;
        values[17] = _onmouseout;
        values[18] = _onmouseover;
        values[19] = _onmouseup;
        values[20] = _onselect;
        values[21] = _readonly;
        values[22] = _style;
        values[23] = _styleClass;
        values[24] = _tabindex;
        values[25] = _title;
        values[26] = _type;
        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state)
    {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _accesskey = (String)values[1];
        _alt = (String)values[2];
        _dir = (String)values[3];
        _disabled = (Boolean)values[4];
        _image = (String)values[5];
        _lang = (String)values[6];
        _onblur = (String)values[7];
        _onchange = (String)values[8];
        _onclick = (String)values[9];
        _ondblclick = (String)values[10];
        _onfocus = (String)values[11];
        _onkeydown = (String)values[12];
        _onkeypress = (String)values[13];
        _onkeyup = (String)values[14];
        _onmousedown = (String)values[15];
        _onmousemove = (String)values[16];
        _onmouseout = (String)values[17];
        _onmouseover = (String)values[18];
        _onmouseup = (String)values[19];
        _onselect = (String)values[20];
        _readonly = (Boolean)values[21];
        _style = (String)values[22];
        _styleClass = (String)values[23];
        _tabindex = (String)values[24];
        _title = (String)values[25];
        _type = (String)values[26];
    }
    //------------------ GENERATED CODE END ---------------------------------------
}
