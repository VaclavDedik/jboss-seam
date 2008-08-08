package org.jboss.seam.excel.jxl.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.Template;
import org.jboss.seam.excel.ui.UIBackground;
import org.jboss.seam.excel.ui.UIBorder;
import org.jboss.seam.excel.ui.UICellTemplate;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIFont;

/**
 * A helper class for parsing excel-specific xls* style attributes and
 * converting them to seam excel format
 * 
 * @author Nicklas Karlsson (nickarls@gmail.com)
 */
public class StyleParser
{
   // General String constants
   private static final String EXCEL_STYLE_PREFIX = "xls";
   private static final String STYLE_TEMPLATE_SEPARATOR = "\\.";
   protected static final String STYLE_ATTRIBUTE = "style";
   private static final String STYLE_SEPARATOR = ";";
   private static final String STYLE_VALUE_SEPARATOR = ":";
   private static final String COLUMN_WIDTH_SEPARATOR = ",";
   protected static final String LOCAL_TEMPLATE_STYLE = "xlsTemplates";
   protected static final String TEMPLATE_GLOBAL = "global";

   // Font attributes
   private static final String FONT_NAME = "xlsFontName";
   private static final String FONT_SIZE = "xlsFontSize";
   private static final String FONT_COLOR = "xlsFontColor";
   private static final String FONT_BOLD = "xlsFontBold";
   private static final String FONT_ITALIC = "xlsFontItalic";
   private static final String FONT_SCRIPT_STYLE = "xlsFontScriptStyle";
   private static final String FONT_STRUCKOUT = "xlsFontStruckOut";
   private static final String FONT_UNDERLINE_STYLE = "xlsFontUnderlineStyle";

   // Background attributes
   private static final String BACKGROUND_COLOR = "xlsBackgroundColor";
   private static final String BACKGROUND_PATTERN = "xlsBackgroundPattern";

   // Border attributes
   private static final String BORDER_COLOR = "xlsBorderColor";
   private static final String BORDER_COLOR_LEFT = "xlsBorderColorLeft";
   private static final String BORDER_COLOR_TOP = "xlsBorderColorTop";
   private static final String BORDER_COLOR_RIGHT = "xlsBorderColorRight";
   private static final String BORDER_COLOR_BOTTOM = "xlsBorderColorBottom";
   private static final String BORDER_LINE_STYLE = "xlsBorderLineStyle";
   private static final String BORDER_LINE_STYLE_LEFT = "xlsBorderLineStyleLeft";
   private static final String BORDER_LINE_STYLE_TOP = "xlsBorderLineStyleTop";
   private static final String BORDER_LINE_STYLE_RIGHT = "xlsBorderLineStyleRight";
   private static final String BORDER_LINE_STYLE_BOTTOM = "xlsBorderLineStyleBottom";

   private static final String BORDER_COLOR_PREFIX = "xlsBorderColor";
   private static final String BORDER_LINE_STYLE_PREFIX = "xlsBorderLineStyle";
   private static final String BORDER_LEFT_POSTFIX = "Left";
   private static final String BORDER_TOP_POSTFIX = "Top";
   private static final String BORDER_RIGHT_POSTFIX = "Right";
   private static final String BORDER_BOTTOM_POSTFIX = "Bottom";
   private static final String BORDER_ALL_POSTFIX = null;

   // Cell attributes
   private static final String ALIGNMENT = "xlsAlignment";

   // Column attributes
   private static final String COLUMN_WIDTH = "xlsColumnWidths";

   /**
    * Gets the style-string from a UIComponent
    * 
    * @param uiComponent The component to examine
    * @return The string or null if attribute wasn't present
    */
   public static String getComponentStyle(UIComponent uiComponent)
   {
      try
      {
         return (String) PropertyUtils.getSimpleProperty(uiComponent, STYLE_ATTRIBUTE);
      }
      catch (Exception e)
      {
         throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not read style attribute #0 from component #1", STYLE_ATTRIBUTE, uiComponent.getId() ));
      }
   }

   /**
    * Parses a style string and returns a map keyed with template name. The
    * contents is a map with key->value pairs from the style string. The
    * template name is determined from the key.temaplate:value form
    * 
    * @param styleString The style string to parse
    * @return The Map of style attribute maps
    */
   public static Map<String, Map<String, String>> getTemplateMap(String styleString)
   {
      Map<String, Map<String, String>> templateMap = new HashMap<String, Map<String, String>>();
      if (styleString == null)
      {
         return templateMap;
      }

      // Split up the style in components
      String[] styleStringParts = styleString.split(STYLE_SEPARATOR);
      for (String styleStringPart : styleStringParts)
      {
         // Split up the style part in key -> value pairs
         String[] styleParts = styleStringPart.split(STYLE_VALUE_SEPARATOR);
         if (styleParts.length != 2)
         {
            throw new ExcelWorkbookException(Interpolator.instance().interpolate("Unbalanced style parsing #0", styleStringPart));
         }
         String styleName = styleParts[0].trim();
         // Only check for xls-styles
         if (!styleName.startsWith(EXCEL_STYLE_PREFIX))
         {
            continue;
         }
         // Check if style name is global or named
         String[] templateParts = styleName.split(STYLE_TEMPLATE_SEPARATOR);
         if (templateParts.length == 1)
         {
            addStyleToTemplate(templateMap, TEMPLATE_GLOBAL, templateParts[0], styleParts[1]);
         }
         else if (templateParts.length == 2)
         {
            addStyleToTemplate(templateMap, templateParts[1], templateParts[0], styleParts[1]);
         }
         else
         {
            throw new ExcelWorkbookException(Interpolator.instance().interpolate("Unbalanced style parsing #0", styleName));
         }
      }

      return templateMap;
   }

   /**
    * Adds a style to a map
    * 
    * @param templateMap The template map
    * @param template The name of the template
    * @param key The key to insert
    * @param value The value to insert
    */
   private static void addStyleToTemplate(Map<String, Map<String, String>> templateMap, String template, String key, String value)
   {
      Map<String, String> innerMap = templateMap.get(template.trim());
      if (innerMap == null)
      {
         innerMap = new HashMap<String, String>();
      }
      innerMap.put(key.trim(), value.trim());
      templateMap.put(template.trim(), innerMap);
   }

   /**
    * Gets column settings from a style string
    * 
    * @param styleString The string to parse
    * @return The column settings
    */
   public static UIColumn getColumnSettings(String styleString)
   {
      UIColumn uiColumn = new UIColumn();

      Map<String, String> globalTemplate = getTemplateMap(styleString).get(TEMPLATE_GLOBAL);
      if (globalTemplate == null)
      {
         return uiColumn;
      }
      if (globalTemplate.get(COLUMN_WIDTH) != null)
      {
         uiColumn.setWidth(Integer.parseInt(globalTemplate.get(COLUMN_WIDTH)));
      }

      return uiColumn;
   }

   /**
    * Get a CellTemplate Template list from a string
    * 
    * @param templateMap Map with templates
    * @return The template list
    */
   public static List<Template> getTemplates(Map<String, Map<String, String>> templateMap)
   {
      List<Template> templates = new ArrayList<Template>();

      if (templateMap == null)
      {
         return templates;
      }

      // Flat the map out into named templates
      for (Map.Entry<String, Map<String, String>> template : templateMap.entrySet())
      {
         String templateName = template.getKey();
         Map<String, String> templateData = template.getValue();

         UICellTemplate cellTemplate = new UICellTemplate();
         cellTemplate.setName(templateName);
         cellTemplate.setAlignment(templateData.get(ALIGNMENT));
         if (hasFontStyle(templateData))
         {
            cellTemplate.getChildren().add(getFontStyle(templateData));
         }
         if (hasBackgroundStyle(templateData))
         {
            cellTemplate.getChildren().add(getBackgroundStyle(templateData));
         }
         if (hasBorders(templateData))
         {
            List<UIComponent> borders = getBorders(templateData);
            for (UIComponent border : borders)
            {
               cellTemplate.getChildren().add(border);
            }
         }

         templates.add(cellTemplate);
      }

      return templates;
   }

   /**
    * Gets a background component from a template map
    * 
    * @param templateData The map to look in
    * @return The background component
    */
   private static UIComponent getBackgroundStyle(Map<String, String> templateData)
   {
      UIBackground background = new UIBackground();

      background.setColor(templateData.get(BACKGROUND_COLOR));
      background.setPattern(templateData.get(BACKGROUND_PATTERN));

      return background;
   }

   /**
    * Checks if a template contains border data
    * 
    * @param templateData The template map to check
    * @return true if present, otherwise false
    */
   private static boolean hasBorders(Map<String, String> templateData)
   {
      return templateData.containsKey(BORDER_LINE_STYLE) || templateData.containsKey(BORDER_LINE_STYLE_BOTTOM) || templateData.containsKey(BORDER_LINE_STYLE_LEFT) || templateData.containsKey(BORDER_LINE_STYLE_RIGHT) || templateData.containsKey(BORDER_LINE_STYLE_TOP) || templateData.containsKey(BORDER_COLOR) || templateData.containsKey(BORDER_COLOR_BOTTOM) || templateData.containsKey(BORDER_COLOR_LEFT) || templateData.containsKey(BORDER_COLOR_RIGHT) || templateData.containsKey(BORDER_COLOR_TOP);
   }

   /**
    * Checks if a template contains background data
    * 
    * @param templateData The template map to check
    * @return true if present, otherwise false
    */
   private static boolean hasBackgroundStyle(Map<String, String> templateData)
   {
      return templateData.containsKey(BACKGROUND_COLOR) || templateData.containsKey(BACKGROUND_PATTERN);
   }

   private static void parseBorders(Map<String, String> borderMap, String borderType, List<UIComponent> borders)
   {
      if (borderMap.isEmpty())
      {
         return;
      }

      UIBorder border = new UIBorder();

      if (BORDER_ALL_POSTFIX == borderType)
      {
         border.setBorder("all");
      }
      else
      {
         border.setBorder(borderType.toLowerCase());
      }

      if (borderType == null)
      {
         borderType = "";
      }

      if (borderMap.containsKey(BORDER_COLOR + borderType))
      {
         border.setColor(borderMap.get(BORDER_COLOR + borderType));
      }

      if (borderMap.containsKey(BORDER_LINE_STYLE + borderType))
      {
         border.setLineStyle(borderMap.get(BORDER_LINE_STYLE + borderType));
      }
      else
      {
         border.setLineStyle("thin");
      }

      borders.add(border);
   }

   /**
    * Gets a list of border components from the template map
    * 
    * @param templateData The map to inspect
    * @return a list of UIBorder instances
    */
   private static List<UIComponent> getBorders(Map<String, String> templateData)
   {
      List<UIComponent> borders = new ArrayList<UIComponent>();

      Map<String, String> all = new HashMap<String, String>();
      Map<String, String> top = new HashMap<String, String>();
      Map<String, String> left = new HashMap<String, String>();
      Map<String, String> right = new HashMap<String, String>();
      Map<String, String> bottom = new HashMap<String, String>();

      for (Map.Entry<String, String> entry : templateData.entrySet())
      {
         if (!(entry.getKey().startsWith(BORDER_COLOR_PREFIX) || entry.getKey().startsWith(BORDER_LINE_STYLE_PREFIX)))
         {
            continue;
         }
         if (entry.getKey().endsWith(BORDER_TOP_POSTFIX))
         {
            top.put(entry.getKey(), entry.getValue());
         }
         else if (entry.getKey().endsWith(BORDER_LEFT_POSTFIX))
         {
            left.put(entry.getKey(), entry.getValue());
         }
         else if (entry.getKey().endsWith(BORDER_RIGHT_POSTFIX))
         {
            right.put(entry.getKey(), entry.getValue());
         }
         else if (entry.getKey().endsWith(BORDER_BOTTOM_POSTFIX))
         {
            right.put(entry.getKey(), entry.getValue());
         }
         else
         {
            all.put(entry.getKey(), entry.getValue());
         }
      }

      parseBorders(all, BORDER_ALL_POSTFIX, borders);
      parseBorders(left, BORDER_LEFT_POSTFIX, borders);
      parseBorders(right, BORDER_RIGHT_POSTFIX, borders);
      parseBorders(bottom, BORDER_BOTTOM_POSTFIX, borders);
      parseBorders(top, BORDER_TOP_POSTFIX, borders);

      return borders;
   }

   /**
    * Gets font description from template map
    * 
    * @param templateData The template map to use
    * @return The font description
    */
   private static UIComponent getFontStyle(Map<String, String> templateData)
   {
      UIFont font = new UIFont();

      font.setName(templateData.get(FONT_NAME));
      if (templateData.containsKey(FONT_SIZE))
      {
         font.setPointSize(Integer.parseInt(templateData.get(FONT_SIZE)));
      }
      font.setColor(templateData.get(FONT_COLOR));
      if (templateData.containsKey(FONT_BOLD))
      {
         font.setBold(Boolean.parseBoolean(templateData.get(FONT_BOLD)));
      }
      if (templateData.containsKey(FONT_ITALIC))
      {
         font.setItalic(Boolean.parseBoolean(templateData.get(FONT_ITALIC)));
      }
      font.setScriptStyle(templateData.get(FONT_SCRIPT_STYLE));
      if (templateData.containsKey(FONT_STRUCKOUT))
      {
         font.setStruckOut(Boolean.parseBoolean(templateData.get(FONT_STRUCKOUT)));
      }
      font.setUnderlineStyle(templateData.get(FONT_UNDERLINE_STYLE));

      return font;
   }

   /**
    * Checks if a template map contains font data
    * 
    * @param templateData The template map to inspect
    * @return true if present, false otherwise
    */
   private static boolean hasFontStyle(Map<String, String> templateData)
   {
      return templateData.containsKey(FONT_NAME) || templateData.containsKey(FONT_SIZE) || templateData.containsKey(FONT_COLOR) || templateData.containsKey(FONT_BOLD) || templateData.containsKey(FONT_ITALIC) || templateData.containsKey(FONT_SCRIPT_STYLE) || templateData.containsKey(FONT_STRUCKOUT) || templateData.containsKey(FONT_UNDERLINE_STYLE);
   }

   protected static List<Integer> parseColumnWidths(Map<String, String> globalTemplate)
   {
      List<Integer> columnWidths = new ArrayList<Integer>();

      if (globalTemplate == null)
      {
         return columnWidths;
      }

      if (!globalTemplate.containsKey(COLUMN_WIDTH))
      {
         return columnWidths;
      }

      String columnWidthString = globalTemplate.get(COLUMN_WIDTH);
      String columnWidthParts[] = columnWidthString.split(COLUMN_WIDTH_SEPARATOR);
      for (String columnWidthPart : columnWidthParts)
      {
         try
         {
            columnWidths.add(Integer.parseInt(columnWidthPart));
         }
         catch (NumberFormatException e)
         {
            columnWidths.add(null);
         }
      }
      return columnWidths;
   }

}
