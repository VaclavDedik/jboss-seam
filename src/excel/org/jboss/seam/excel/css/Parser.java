package org.jboss.seam.excel.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.ui.UILink;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

public class Parser
{
   private static final String STYLE_ATTRIBUTE = "style";
   private static final String STYLE_CLASS_ATTRIBUTE = "styleClass";
   private static final String STYLES_SEPARATOR = ";";
   private static final String STYLE_NAME_VALUE_SEPARATOR = ":";
   private static final String STYLE_SHORTHAND_SEPARATOR = " ";

   private Map<String, StyleMap> definedStyleClasses = new HashMap<String, StyleMap>();
   private Map<String, PropertyBuilder> propertyBuilders = new HashMap<String, PropertyBuilder>();
   private Map<UIComponent, StyleMap> cellStyleCache = new HashMap<UIComponent, StyleMap>();

   private Log log = Logging.getLog(Parser.class);

   public Parser()
   {
      initPropertyBuilders();
   }

   public Parser(List<UILink> stylesheets) throws MalformedURLException, IOException
   {
      initPropertyBuilders();
      loadStylesheets(stylesheets);
   }

   private void loadStylesheets(List<UILink> stylesheets) throws MalformedURLException, IOException
   {
      for (UILink stylesheet : stylesheets)
      {
         definedStyleClasses.putAll(parseStylesheet(stylesheet.getURL()));
      }
   }

   private void initPropertyBuilders()
   {
      propertyBuilders.put(CSSNames.FONT_FAMILY, new PropertyBuilders.FontFamily());
      propertyBuilders.put(CSSNames.FONT_SIZE, new PropertyBuilders.FontSize());
      propertyBuilders.put(CSSNames.FONT_COLOR, new PropertyBuilders.FontColor());
      propertyBuilders.put(CSSNames.FONT_ITALIC, new PropertyBuilders.FontItalic());
      propertyBuilders.put(CSSNames.FONT_SCRIPT_STYLE, new PropertyBuilders.FontScriptStyle());
      propertyBuilders.put(CSSNames.FONT_STRUCK_OUT, new PropertyBuilders.FontStruckOut());
      propertyBuilders.put(CSSNames.FONT_UNDERLINE_STYLE, new PropertyBuilders.FontUnderlineStyle());
      propertyBuilders.put(CSSNames.FONT_BOLD, new PropertyBuilders.FontBold());
      propertyBuilders.put(CSSNames.FONT, new PropertyBuilders.FontShorthand());
      propertyBuilders.put(CSSNames.BACKGROUND_PATTERN, new PropertyBuilders.BackgroundPattern());
      propertyBuilders.put(CSSNames.BACKGROUND_COLOR, new PropertyBuilders.BackgroundColor());
      propertyBuilders.put(CSSNames.BACKGROUND, new PropertyBuilders.BackgroundShorthand());
      propertyBuilders.put(CSSNames.BORDER_LEFT_COLOR, new PropertyBuilders.BorderLeftColor());
      propertyBuilders.put(CSSNames.BORDER_LEFT_LINE_STYLE, new PropertyBuilders.BorderLeftLineStyle());
      propertyBuilders.put(CSSNames.BORDER_LEFT, new PropertyBuilders.BorderLeftShorthand());
      propertyBuilders.put(CSSNames.BORDER_TOP_COLOR, new PropertyBuilders.BorderTopColor());
      propertyBuilders.put(CSSNames.BORDER_TOP_LINE_STYLE, new PropertyBuilders.BorderTopLineStyle());
      propertyBuilders.put(CSSNames.BORDER_TOP, new PropertyBuilders.BorderTopShorthand());
      propertyBuilders.put(CSSNames.BORDER_RIGHT_COLOR, new PropertyBuilders.BorderRightColor());
      propertyBuilders.put(CSSNames.BORDER_RIGHT_LINE_STYLE, new PropertyBuilders.BorderRightLineStyle());
      propertyBuilders.put(CSSNames.BORDER_RIGHT, new PropertyBuilders.BorderRightShorthand());
      propertyBuilders.put(CSSNames.BORDER_BOTTOM_COLOR, new PropertyBuilders.BorderBottomColor());
      propertyBuilders.put(CSSNames.BORDER_BOTTOM_LINE_STYLE, new PropertyBuilders.BorderBottomLineStyle());
      propertyBuilders.put(CSSNames.BORDER_BOTTOM, new PropertyBuilders.BorderBottomShorthand());
      propertyBuilders.put(CSSNames.BORDER, new PropertyBuilders.BorderShorthand());
      propertyBuilders.put(CSSNames.FORMAT_MASK, new PropertyBuilders.FormatMask());
      propertyBuilders.put(CSSNames.ALIGNMENT, new PropertyBuilders.Alignment());
      propertyBuilders.put(CSSNames.INDENTATION, new PropertyBuilders.Indentation());
      propertyBuilders.put(CSSNames.ORIENTATION, new PropertyBuilders.Orientation());
      propertyBuilders.put(CSSNames.LOCKED, new PropertyBuilders.Locked());
      propertyBuilders.put(CSSNames.SHRINK_TO_FIT, new PropertyBuilders.ShrinkToFit());
      propertyBuilders.put(CSSNames.WRAP, new PropertyBuilders.Wrap());
      propertyBuilders.put(CSSNames.VERICAL_ALIGNMENT, new PropertyBuilders.VericalAlignment());
      propertyBuilders.put(CSSNames.COLUMN_WIDTH, new PropertyBuilders.ColumnWidth());
      propertyBuilders.put(CSSNames.COLUMN_AUTO_SIZE, new PropertyBuilders.ColumnAutoSize());
      propertyBuilders.put(CSSNames.COLUMN_HIDDEN, new PropertyBuilders.ColumnHidden());
      propertyBuilders.put(CSSNames.COLUMN_WIDTHS, new PropertyBuilders.ColumnWidths());
   }

   private Map<String, StyleMap> parseStylesheet(String URL) throws MalformedURLException, IOException
   {
      Map<String, StyleMap> styleClasses = new HashMap<String, StyleMap>();
      BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(URL).openStream()));
      String line;
      while ((line = reader.readLine()) != null)
      {
         String[] spaceParts = line.split(" ");
         String name = spaceParts[0];
         if (name.startsWith("."))
         {
            name = name.substring(1);
         }
         int startbrace = line.indexOf("{");
         int stopbrace = line.indexOf("}");
         if (startbrace < 0 || stopbrace < 0)
         {
            String message = Interpolator.instance().interpolate("Could not find braces in #0", line);
            throw new ExcelWorkbookException(message);
         }
         String styleData = line.substring(startbrace + 1, stopbrace).trim();
         StyleMap styleMap = parseStyleString(styleData);
         styleClasses.put(name, styleMap);
      }
      reader.close();
      return styleClasses;
   }

   public static String getStyle(UIComponent component) {
      return getStyleProperty(component, STYLE_ATTRIBUTE);
   }
   
   public static String getStyleClass(UIComponent component) {
      return getStyleProperty(component, STYLE_CLASS_ATTRIBUTE);
   }
   
   private static String getStyleProperty(UIComponent component, String field)
   {
      try
      {
         return (String) PropertyUtils.getProperty(component, field);
      }
      catch (NoSuchMethodException e)
      {
         // No panic, no property
         return null;
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Could not read field #0 of bean #1", field, component.getId());
         throw new ExcelWorkbookException(message, e);
      }
   }

   private List<StyleMap> cascadeStyleMap(UIComponent component, List<StyleMap> styleMaps) {
      styleMaps.add(getStyleMap(component));
      if (component.getParent() != null) {
         cascadeStyleMap(component.getParent(), styleMaps);
      }
      return styleMaps;
   }
   
   public StyleMap getCascadedStyleMap(UIComponent component) {
      List<StyleMap> styleMaps = cascadeStyleMap(component, new ArrayList<StyleMap>());
      Collections.reverse(styleMaps);
      StyleMap cascadedStyleMap = new StyleMap();
      for (StyleMap styleMap : styleMaps) {
         cascadedStyleMap.putAll(styleMap);
      }
      return cascadedStyleMap;
   }
   
   private StyleMap getStyleMap(UIComponent component)
   {
      if (cellStyleCache.containsKey(component))
      {
         return cellStyleCache.get(component);
      }

      StyleMap styleMap = new StyleMap();

      String componentStyleClass = getStyleProperty(component, STYLE_CLASS_ATTRIBUTE);
      if (componentStyleClass != null)
      {
         String[] styleClasses = trimArray(componentStyleClass.split(STYLE_SHORTHAND_SEPARATOR));
         for (String styleClass : styleClasses)
         {
            if (!definedStyleClasses.containsKey(styleClass))
            {
               log.warn("Uknown style class #0", styleClass);
               continue;
            }
            styleMap.putAll(definedStyleClasses.get(styleClass));
         }
      }

      String componentStyle = getStyleProperty(component, STYLE_ATTRIBUTE);
      if (componentStyle != null)
      {
         styleMap.putAll(parseStyleString(componentStyle));
      }

      cellStyleCache.put(component, styleMap);
      return styleMap;
   }

   private StyleMap parseStyleString(String styleString)
   {
      StyleMap styleMap = new StyleMap();

      String[] styles = trimArray(styleString.split(STYLES_SEPARATOR));
      for (String style : styles)
      {
         String[] styleParts = style.split(STYLE_NAME_VALUE_SEPARATOR);
         if (styleParts.length != 2)
         {
            log.warn("Style component #0 should be of form <key>#1<value>", style, STYLE_NAME_VALUE_SEPARATOR);
            continue;
         }
         String styleName = styleParts[0].toLowerCase().trim();
         if (!propertyBuilders.containsKey(styleName))
         {
            log.warn("No property builder (unknown style) for property #0", styleName);
            continue;
         }
         PropertyBuilder propertyBuilder = propertyBuilders.get(styleName);
         String styleValue = styleParts[1];
         String[] styleValues = trimArray(styleValue.trim().split(STYLE_SHORTHAND_SEPARATOR));
         styleMap.putAll(propertyBuilder.parseProperty(styleName, styleValues));
      }

      return styleMap;
   }

   private String[] trimArray(String[] array)
   {
      List<String> validValues = new ArrayList<String>();
      for (int i = 0; i < array.length; i++)
      {
         if (!"".equals(array[i]) && !" ".equals(array[i]))
         {
            validValues.add(array[i].toLowerCase().trim());
         }
      }
      return validValues.toArray(new String[validValues.size()]);
   }

   public void setStylesheets(List<UILink> stylesheets) throws MalformedURLException, IOException
   {
      loadStylesheets(stylesheets);
   }
}
