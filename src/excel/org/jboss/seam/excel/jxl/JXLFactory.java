package org.jboss.seam.excel.jxl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import jxl.HeaderFooter;
import jxl.biff.DisplayFormat;
import jxl.biff.FontRecord;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.Pattern;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableFont;
import jxl.write.WriteException;

import org.jboss.seam.core.Interpolator;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.css.CellStyle;
import org.jboss.seam.excel.ui.UIHeaderFooter;
import org.jboss.seam.excel.ui.UIHeaderFooterCommand;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

public class JXLFactory
{
   private static final String DATEFORMATS_CLASSNAME = "jxl.write.DateFormats";
   private static final String NUMBERFORMATS_CLASSNAME = "jxl.write.NumberFormats";
   private static final String ALIGNMENT_CLASS_NAME = "jxl.format.Alignment";
   private static final String ORIENTATION_CLASS_NAME = "jxl.format.Orientation";
   private static final String VERTICAL_ALIGNMENT_CLASS_NAME = "jxl.format.VerticalAlignment";
   private static final String COLOR_CLASS_NAME = "jxl.format.Colour";
   private static final String BORDER_CLASS_NAME = "jxl.format.Border";
   private static final String BORDER_LINE_STYLE_CLASS_NAME = "jxl.format.BorderLineStyle";
   private static final String PATTERN_CLASS_NAME = "jxl.format.Pattern";
   private static final String PAGE_ORIENTATION_CLASS_NAME = "jxl.format.PageOrientation";
   private static final String PAPER_SIZE_CLASS_NAME = "jxl.format.PaperSize";
   private static final String HEADER_FOOTER_COMMAND_CLASS_NAME = "org.jboss.seam.excel.UIHeaderFooterCommand";
   private static final String SCRIPT_STYLE_CLASS_NAME = "jxl.format.ScriptStyle";
   private static final String UNDERLINE_STYLE_CLASS_NAME = "jxl.format.UnderlineStyle";
   
   private static final Log log = Logging.getLog(JXLFactory.class);

   public static boolean isBorderLineStyle(String text) {
      return getValidContants(BORDER_LINE_STYLE_CLASS_NAME).contains(text.toLowerCase());
   }
   
   public static boolean isPattern(String text) {
      return getValidContants(PATTERN_CLASS_NAME).contains(text.toLowerCase());
   }
   
   public static boolean isColor(String text) {
      return getValidContants(COLOR_CLASS_NAME).contains(text.toLowerCase());
   }

   public static boolean isAlignment(String text) {
      return getValidContants(ALIGNMENT_CLASS_NAME).contains(text.toLowerCase());
   }

   public static boolean isOrientation(String text) {
      return getValidContants(ORIENTATION_CLASS_NAME).contains(text.toLowerCase());
   }

   public static boolean isVerticalAlignment(String text) {
      return getValidContants(VERTICAL_ALIGNMENT_CLASS_NAME).contains(text.toLowerCase());
   }

   public static boolean isUnderlineStyle(String text) {
      return getValidContants(UNDERLINE_STYLE_CLASS_NAME).contains(text.toLowerCase());
   }

   public static boolean isScriptStyle(String text) {
      return getValidContants(SCRIPT_STYLE_CLASS_NAME).contains(text.toLowerCase());
   }

   private static List<String> getValidContants(String className) {
      List<String> constants = new ArrayList<String>();
      
      if (log.isTraceEnabled())
      {
         log.trace("Getting valid constants from #0", className);
      }
      Class clazz = null;
      try
      {
         clazz = Class.forName(className);
      }
      catch (ClassNotFoundException e)
      {
         throw new ExcelWorkbookException("Could not find class while getting valid constants", e);
      }
      // Loop through the fields
      for (Field field : clazz.getFields())
      {
         int modifiers = field.getModifiers();
         // Append to list if it's public and static (as most our constants are)
         if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers))
         {
            constants.add(field.getName().toLowerCase());
         }
      }
      return constants;
   }
   
   public static String getValidConstantsSuggestion(String className)
   {
      List<String> constants = getValidContants(className);
      StringBuffer buffer = new StringBuffer();
      int i = 0;
      // Loop through the fields
      for (String field : constants)
      {
         buffer.append(i++ == 0 ? field : ", " + field);
      }
      return Interpolator.instance().interpolate("[#0]", buffer.toString());
   }   
   
   private static Object getConstant(String className, String fieldName) throws NoSuchFieldException
   {
      if (log.isTraceEnabled())
      {
         log.trace("Looking for constant #0 in class #1", fieldName, className);
      }
      try
      {
         return Class.forName(className).getField(fieldName).get(null);
      }
      catch (NoSuchFieldException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not read field #0 from class #1", fieldName, className), e);
      }
   }   
   
   public static Alignment createAlignment(String alignment)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating alignment for #0", alignment);
      }
      try
      {
         return alignment == null ? Alignment.LEFT : (Alignment) getConstant(ALIGNMENT_CLASS_NAME, alignment.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Alignment {0} not supported, try {1}", alignment, getValidConstantsSuggestion(ALIGNMENT_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }
   
   /**
    * Creates a JExcelAPI representation of an script style
    * 
    * @param mask The requested script style
    * @return The script style representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/ScriptStyle.html">ScriptStyle</a>
    * @since 0.1
    */
   private static ScriptStyle createScriptStyle(String scriptStyle)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating script style for #0", scriptStyle);
      }
      try
      {
         return scriptStyle == null ? ScriptStyle.NORMAL_SCRIPT : (ScriptStyle) getConstant(SCRIPT_STYLE_CLASS_NAME, scriptStyle.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Script style {0} not supported, try {1}", scriptStyle, getValidConstantsSuggestion(SCRIPT_STYLE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }   
   
   /**
    * Creates a JExcelAPI representation of an underline style
    * 
    * @param mask The requested underline style
    * @return The underline style representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/UnderlineStyle.html">UnderlineStyle</a>
    * @since 0.1
    */
   private static UnderlineStyle createUnderlineStyle(String underlineStyle)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating underline style for #0", underlineStyle);
      }
      try
      {
         return underlineStyle == null ? UnderlineStyle.NO_UNDERLINE : (UnderlineStyle) getConstant(UNDERLINE_STYLE_CLASS_NAME, underlineStyle.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Underline style {0} not supported, try {1}", underlineStyle, getValidConstantsSuggestion(UNDERLINE_STYLE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }   
 
   public static FontRecord createFont(CellStyle.Font fontspecs) throws WriteException
   {
      WritableFont font = null;
      if (fontspecs.family != null) {
         font = new WritableFont(WritableFont.createFont(fontspecs.family));
      }
      else
      {
         font = new WritableFont(WritableFont.ARIAL);
      }
      if (fontspecs.pointSize != null) {
         font.setPointSize(fontspecs.pointSize);
      }
      if (fontspecs.color != null) {
         font.setColour(createColor(fontspecs.color));
      }
      if (fontspecs.bold != null) {
         font.setBoldStyle(fontspecs.bold ? WritableFont.BOLD : WritableFont.NO_BOLD);
      }
      if (fontspecs.italic != null) {
         font.setItalic(fontspecs.italic);
      }
      if (fontspecs.struckOut != null) {
         font.setStruckout(fontspecs.struckOut);
      }
      if (fontspecs.scriptStyle != null) {
         font.setScriptStyle(createScriptStyle(fontspecs.scriptStyle));
      }
      if (fontspecs.underlineStyle != null) {
         font.setUnderlineStyle(createUnderlineStyle(fontspecs.underlineStyle));
      }
      return font;
   }

   public static DisplayFormat createNumberFormat(String mask)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating number format for mask #0", mask);
      }
      try
      {
         return (DisplayFormat) getConstant(NUMBERFORMATS_CLASSNAME, mask);
      }
      catch (NoSuchFieldException e)
      {
         // Look! An empty catch block! But this one is documented. We are using
         // this to see if there is a constant
         // defines for this in the class
         return null;
      }
   }

   public static DisplayFormat createDateFormat(String mask)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating date format for mask #0", mask);
      }
      try
      {
         return (DisplayFormat) getConstant(DATEFORMATS_CLASSNAME, mask.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         // Look! An empty catch block! But this one is documented. We are using
         // this to see if there is a constant
         // defines for this in the class
         return null;
      }
   }

   public static Colour createColor(String color)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating color for #0", color);
      }
      // Workaround for the feature that black is... well not always black in
      // Excel (ref: Andy Khan on yahoo groups)
      if (color.equalsIgnoreCase("black"))
      {
         color = "palette_black";
      }
      try
      {
         return color == null ? Colour.AUTOMATIC : (Colour) getConstant(COLOR_CLASS_NAME, color.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Color {0} not supported, try {1}", color, getValidConstantsSuggestion(COLOR_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   public static Orientation createOrientation(String orientation)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating orientation for #0", orientation);
      }
      try
      {
         return orientation == null ? Orientation.HORIZONTAL : (Orientation) getConstant(ORIENTATION_CLASS_NAME, orientation.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Orientation {0} not supported, try {1}", orientation, getValidConstantsSuggestion(ORIENTATION_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   public static VerticalAlignment createVerticalAlignment(String verticalAlignment)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating verical alignment for #0", verticalAlignment);
      }
      try
      {
         return verticalAlignment == null ? VerticalAlignment.BOTTOM : (VerticalAlignment) getConstant(VERTICAL_ALIGNMENT_CLASS_NAME, verticalAlignment.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Verical alignment {0} not supported, try {1}", verticalAlignment, getValidConstantsSuggestion(VERTICAL_ALIGNMENT_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   public static Border createBorder(String border)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating border for #0", border);
      }
      try
      {
         return border == null ? Border.ALL : (Border) getConstant(BORDER_CLASS_NAME, border.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Border {0} not supported, try {1}", border, getValidConstantsSuggestion(BORDER_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   public static BorderLineStyle createLineStyle(String string)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating border line style for #0", string);
      }
      try
      {
         return string == null ? BorderLineStyle.NONE : (BorderLineStyle) getConstant(BORDER_LINE_STYLE_CLASS_NAME, string.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Border line style {0} not supported, try {1}", string, getValidConstantsSuggestion(BORDER_LINE_STYLE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   public static Pattern createPattern(String pattern)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating pattern for #0", pattern);
      }
      try
      {
         return pattern == null ? Pattern.SOLID : (Pattern) getConstant(PATTERN_CLASS_NAME, pattern.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Pattern {0} not supported, try {1}", pattern, getValidConstantsSuggestion(PATTERN_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }
   
   /**
    * Creates a JExcelAPI representation of a page orientation
    * 
    * @param orientation The type of orientation to create
    * @return The page orientation representation
    */
   public static PageOrientation createPageOrientation(String orientation)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating page orientation for #0", orientation);
      }
      try
      {
         return orientation == null ? PageOrientation.LANDSCAPE : (PageOrientation) getConstant(PAGE_ORIENTATION_CLASS_NAME, orientation.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Page orientation {0} not supported, try {1}", orientation, getValidConstantsSuggestion(PAGE_ORIENTATION_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }   

   /**
    * Creates a JExcelAPI representation of a paper size
    * 
    * @param paperSize The type of paper size to create
    * @return The paper size representation
    */
   public static PaperSize createPaperSize(String paperSize)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating paper size for #0", paperSize);
      }
      try
      {
         return paperSize == null ? PaperSize.A4 : (PaperSize) getConstant(PAPER_SIZE_CLASS_NAME, paperSize.toUpperCase());
      }
      catch (NoSuchFieldException e)
      {
         String message = Interpolator.instance().interpolate("Page size {0} not supported, try {1}", paperSize, getValidConstantsSuggestion(PAPER_SIZE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }
   
   /**
    * Creates a JExcelAPI header or footer representation. Processes the left,
    * center and right facets using a helper method
    * 
    * @param uiHeaderFooter The UI header or footer to interpret
    * @param headerFooter The JExcelAPI header or footer representation to add
    *           to
    * @return The JExcelAPI header or footer representation
    */
   public static HeaderFooter createHeaderFooter(UIHeaderFooter uiHeaderFooter, HeaderFooter headerFooter)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Processing header/footer #0", uiHeaderFooter);
      }
      processHeaderFooterFacet(headerFooter.getLeft(), uiHeaderFooter.getFacet(UIHeaderFooter.LEFT_FACET));
      processHeaderFooterFacet(headerFooter.getCentre(), uiHeaderFooter.getFacet(UIHeaderFooter.CENTER_FACET));
      processHeaderFooterFacet(headerFooter.getRight(), uiHeaderFooter.getFacet(UIHeaderFooter.RIGHT_FACET));
      return headerFooter;
   }

   /**
    * Processes a header or footer facet. A header or footer facet in JExcelAPI
    * is split into three parts, left, center and right and the UI
    * representation has facets with the same namings. Gets the requested
    * facet from the UI component and calls helper methods for processing the
    * header commands in sequence
    * 
    * @param headerFooter The JExcelAPI header or footer facet to process
    * @param facetName The name of the facet to process (left, center, right)
    * @param uiHeaderFooter The UI representation to interpret
    */
   private static void processHeaderFooterFacet(HeaderFooter.Contents contents, UIComponent facet)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Processing facet #0 of header/footer #1", facet, contents);
      }
      // No facet found
      if (facet == null)
      {
         return;
      }
      for (UIComponent child : facet.getChildren())
      {
         if (child.getClass() == UIHeaderFooterCommand.class)
         {
            processHeaderFooterCommand(contents, (UIHeaderFooterCommand) child);
         }
      }
   }

   /**
    * Processes a header command and applies it to the JExcelAPI header contents
    * 
    * @param contents The contents to apply the command to (left, center, right)
    * @param command The command to interpret
    */
   private static void processHeaderFooterCommand(HeaderFooter.Contents contents, UIHeaderFooterCommand command)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Processing header/footer command #0", command);
      }
      switch (command.getCommand())
      {
      case append:
         contents.append((String) command.getParameter());
         break;
      case date:
         contents.appendDate();
         break;
      case page_number:
         contents.appendPageNumber();
         break;
      case time:
         contents.appendTime();
         break;
      case total_pages:
         contents.appendTotalPages();
         break;
      case workbook_name:
         contents.appendWorkbookName();
         break;
      case worksheet_name:
         contents.appendWorkSheetName();
         break;
      case font_name:
         contents.setFontName((String) command.getParameter());
         break;
      case font_size:
         contents.setFontSize((Integer) command.getParameter());
         break;
      case toggle_bold:
         contents.toggleBold();
         break;
      case toggle_italics:
         contents.toggleItalics();
         break;
      case toggle_double_underline:
         contents.toggleDoubleUnderline();
         break;
      case toggle_outline:
         contents.toggleOutline();
         break;
      case toggle_shadow:
         contents.toggleShadow();
         break;
      case toggle_strikethrough:
         contents.toggleStrikethrough();
         break;
      case toggle_subscript:
         contents.toggleSubScript();
         break;
      case toggle_superscript:
         contents.toggleSuperScript();
         break;
      default:
         String message = Interpolator.instance().interpolate("Header/Footer command {0} not supported, try {1}", command.getCommand(), getValidConstantsSuggestion(HEADER_FOOTER_COMMAND_CLASS_NAME));
         throw new ExcelWorkbookException(message);
      }
   }      
}
