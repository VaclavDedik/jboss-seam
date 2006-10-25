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

import org.jboss.seam.ui.JSF;
import org.jboss.seam.ui.HTML;

import javax.faces.component.UIComponent;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @author Martin Marinschek
 * @version $Revision$ $Date$
 */
public abstract class HtmlCommandLinkTagBase
    extends HtmlComponentTagBase
{
    //private static final Log log = LogFactory.getLog(HtmlCommandLinkTag.class);

    // UIComponent attributes --> already implemented in UIComponentTagBase

    // user role attributes --> already implemented in UIComponentTagBase

    // HTML universal attributes --> already implemented in HtmlComponentTagBase

    // HTML event handler attributes --> already implemented in HtmlComponentTagBase

    // HTML anchor attributes relevant for command link
    private String _accesskey;
    private String _charset;
    private String _coords;
    private String _hreflang;
    private String _rel;
    private String _rev;
    private String _shape;
    private String _tabindex;
    private String _type;
    private String _target;
    //HtmlCommandLink Attributes
    //FIXME: is mentioned in JSF API, but is no official anchor-attribute of HTML 4.0... what to do?
    private String _onblur;
    //FIXME: is mentioned in JSF API, but is no official anchor-attribute of HTML 4.0... what to do?
    private String _onfocus;

    // UICommand attributes
    private String _action;
    private String _immediate;
    private String _actionListener;

    @Override
    public void release() {
        super.release();
        _accesskey=null;
        _charset=null;
        _coords=null;
        _hreflang=null;
        _rel=null;
        _rev=null;
        _shape=null;
        _tabindex=null;
        _type=null;
        _target=null;
        _onblur=null;
        _onfocus=null;
        _action=null;
        _immediate=null;
        _actionListener=null;
    }

    @Override
    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);

        setStringProperty(component, HTML.ACCESSKEY_ATTR, _accesskey);
        setStringProperty(component, HTML.CHARSET_ATTR, _charset);
        setStringProperty(component, HTML.COORDS_ATTR, _coords);
        setStringProperty(component, HTML.HREFLANG_ATTR, _hreflang);
        setStringProperty(component, HTML.REL_ATTR, _rel);
        setStringProperty(component, HTML.REV_ATTR, _rev);
        setStringProperty(component, HTML.SHAPE_ATTR, _shape);
        setStringProperty(component, HTML.TABINDEX_ATTR, _tabindex);
        setStringProperty(component, HTML.TYPE_ATTR, _type);
        setStringProperty(component, HTML.ONBLUR_ATTR, _onblur);
        setStringProperty(component, HTML.ONFOCUS_ATTR, _onfocus);
        setStringProperty(component, HTML.TARGET_ATTR, _target);
        setActionProperty(component, _action);
        setActionListenerProperty(component, _actionListener);
        setBooleanProperty(component, JSF.IMMEDIATE_ATTR, _immediate);
   }

    public void setAccesskey(String accesskey)
    {
        _accesskey = accesskey;
    }

    public void setCharset(String charset)
    {
        _charset = charset;
    }

    public void setCoords(String coords)
    {
        _coords = coords;
    }

    public void setHreflang(String hreflang)
    {
        _hreflang = hreflang;
    }

    public void setOnblur(String onblur)
    {
        _onblur = onblur;
    }

    public void setOnfocus(String onfocus)
    {
        _onfocus = onfocus;
    }

    public void setRel(String rel)
    {
        _rel = rel;
    }

    public void setRev(String rev)
    {
        _rev = rev;
    }

    public void setShape(String shape)
    {
        _shape = shape;
    }

    public void setTabindex(String tabindex)
    {
        _tabindex = tabindex;
    }

    public void setType(String type)
    {
        _type = type;
    }

    public void setAction(String action)
    {
        _action = action;
    }

    public void setImmediate(String immediate)
    {
        _immediate = immediate;
    }

    public void setActionListener(String actionListener)
    {
        _actionListener = actionListener;
    }


    public void setTarget(String target)
    {
        this._target = target;
    }
}
