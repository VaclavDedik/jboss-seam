package org.jboss.seam.excel.ui;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.excel.Command;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.Template;
import org.jboss.seam.excel.WorksheetItem;

/**
 * Common superclass for the UI components. Contains helper methods for merging
 * etc.
 * 
 * @author Nicklas Karlsson (nickarls@gmail.com)
 * @author Daniel Roth (danielc.roth@gmail.com)
 * @version 0.2
 */
public abstract class ExcelComponent extends UIComponentBase
{
   public final static String HEADER_FACET = "header";

   public ExcelComponent()
   {
      super();
   }

   /**
    * Helper class that returns all children of a certain type (implements
    * interface)
    * 
    * @param <T> The type to check for
    * @param children The list of children to search
    * @param childType The child type
    * @return The list of matching items
    */
   @SuppressWarnings("unchecked")
   public static <T> List<T> getChildrenOfType(List<UIComponent> children, Class<T> childType)
   {
      List<T> matches = new ArrayList<T>();
      for (UIComponent child : children)
      {
         if (childType.isAssignableFrom(child.getClass()))
         {
            matches.add((T) child);
         }
      }
      return matches;
   }

   /**
    * Returns all commands from a child list
    * 
    * @param children The list to search
    * @return The commands
    */
   protected static List<Command> getCommands(List<UIComponent> children)
   {
      return getChildrenOfType(children, Command.class);
   }

   /**
    * Returns all templates from a child list
    * 
    * @param children The list to search
    * @return The templates
    */
   protected static List<Template> getTemplates(List<UIComponent> children)
   {
      return getChildrenOfType(children, Template.class);
   }

   /**
    * Returns all worksheet items (cells, images, hyperlinks) from a child list
    * 
    * @param children The list to search
    * @return The items
    */
   protected static List<WorksheetItem> getItems(List<UIComponent> children)
   {
      return getChildrenOfType(children, WorksheetItem.class);
   }

   /**
    * Helper method for fetching value through binding
    * 
    * @param name The field to bind to
    * @param defaultValue The default value to fall back to
    * @return The field value
    */
   protected Object valueOf(String name, Object defaultValue)
   {
      Object value = defaultValue;
      if (getValueExpression(name) != null)
      {
         value = getValueExpression(name).getValue(FacesContext.getCurrentInstance().getELContext());
      }
      return value;
   }

   /**
    * Fetches the parent workbook from a component
    * 
    * @param component The component to examine
    * @return The workbook
    */
   protected ExcelWorkbook getWorkbook(UIComponent component)
   {
      if (component == null)
         return null;
      if (component instanceof UIWorkbook)
      {
         UIWorkbook uiWorkBook = (UIWorkbook) component;
         return uiWorkBook.getExcelWorkbook();
      }
      else
      {
         return getWorkbook(component.getParent());
      }
   }

   @SuppressWarnings("unchecked")
   protected UIComponent getParentByClass(UIComponent root, Class searchClass)
   {
      if (root == null)
      {
         return null;
      }
      if (root.getClass() == searchClass)
      {
         return root;
      }
      return getParentByClass(root.getParent(), searchClass);
   }

}
