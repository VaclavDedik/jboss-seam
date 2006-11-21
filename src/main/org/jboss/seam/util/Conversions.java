package org.jboss.seam.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Conversions
{
   
   private static Map<Class, Converter> converters = new HashMap<Class, Converter>() {{
      put(String.class, new StringConverter());
      put(Boolean.class, new BooleanConverter());
      put(boolean.class, new BooleanConverter());
      put(Integer.class, new IntegerConverter());
      put(int.class, new IntegerConverter());
      put(Long.class, new LongConverter());
      put(long.class, new LongConverter());
      put(Float.class, new FloatConverter());
      put(float.class, new FloatConverter());
      put(Double.class, new DoubleConverter());
      put(double.class, new DoubleConverter());
      put(Character.class, new CharacterConverter());
      put(char.class, new CharacterConverter());
      put(String[].class, new StringArrayConverter());
      put(Set.class, new SetConverter());
      put(List.class, new ListConverter());
      put(Map.class, new MapConverter());
      put(Properties.class, new PropertiesConverter());
      //put(Date.class, new DateTimeConverter());
      //put(Short.class, new ShortConverter());
      //put(Byte.class, new ByteConverter());
      //put(BigInteger.class, new BigIntegerConverter());
      //put(BigDecimal.class, new BigDecimalConverter());
      put(Class.class, new ClassConverter());
   }};
   
   public static <Y> void putConverter(Class<Y> type, Converter<Y> converter)
   {
      converters.put(type, converter);
   }
   
   public static <Y> Converter<Y> getConverter(Class<Y> clazz)
   {
      Converter<Y> converter = converters.get(clazz);
      if (converter==null)
      {
          throw new IllegalArgumentException("No converter for type: " + clazz.getName());
      }
      return converter;
   }
   
   public static interface Converter<Z>
   {
      public Z toObject(PropertyValue value, Type type); 
   }
   
   public static class BooleanConverter implements Converter<Boolean>
   {
      public Boolean toObject(PropertyValue value, Type type)
      {
         return Boolean.valueOf( value.getSingleValue() );
      }
   }
   
   public static class IntegerConverter implements Converter<Integer>
   {
      public Integer toObject(PropertyValue value, Type type)
      {
         return Integer.valueOf( value.getSingleValue() );
      }
   }
   
   public static class LongConverter implements Converter<Long>
   {
      public Long toObject(PropertyValue value, Type type)
      {
         return Long.valueOf( value.getSingleValue() );
      }
   }
   
   public static class FloatConverter implements Converter<Float>
   {
      public Float toObject(PropertyValue value, Type type)
      {
         return Float.valueOf( value.getSingleValue() );
      }
   }
   
   public static class DoubleConverter implements Converter<Double>
   {
      public Double toObject(PropertyValue value, Type type)
      {
         return Double.valueOf( value.getSingleValue() );
      }
   }
   
   public static class CharacterConverter implements Converter<Character>
   {
      public Character toObject(PropertyValue value, Type type)
      {
         return value.getSingleValue().charAt(0);
      }
   }
   
   public static class StringConverter implements Converter<String>
   {
      public String toObject(PropertyValue value, Type type)
      {
         return  value.getSingleValue() ;
      }
   }
   
   public static class StringArrayConverter implements Converter<String[]>
   {
      public String[] toObject(PropertyValue values, Type type)
      {
         return values.getMultiValues();
      }
   }
   
   public static class ArrayConverter implements Converter
   {
      public Object toObject(PropertyValue values, Type type)
      {
         String[] strings = values.getMultiValues();
         Class elementType = ( (Class) type ).getComponentType();
         Object objects = Array.newInstance( elementType, strings.length );
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length; i++)
         {
            Object element = elementConverter.toObject( new FlatPropertyValue(strings[i]), elementType );
            Array.set( objects, i, element );
         }
         return objects;
      }
   }
   
   public static class SetConverter implements Converter<Set>
   {
      public Set toObject(PropertyValue values, Type type)
      {
         String[] strings = values.getMultiValues();
         Class elementType = Reflections.getCollectionElementType(type);
         Set set = new HashSet(strings.length);
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length; i++)
         {
            Object element = elementConverter.toObject( new FlatPropertyValue(strings[i]), elementType );
            set.add(element);
         }
         return set;
      }
   }
   
   public static class ListConverter implements Converter<List>
   {
      public List toObject(PropertyValue values, Type type)
      {
         String[] strings = values.getMultiValues();
         Class elementType = Reflections.getCollectionElementType(type);
         List list = new ArrayList(strings.length);
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length; i++)
         {
            Object element = elementConverter.toObject( new FlatPropertyValue(strings[i]), elementType );
            list.add(element);
         }
         return list;
      }
   }
   
   public static class MapConverter implements Converter<Map>
   {
      public Map toObject(PropertyValue values, Type type)
      {
         Map<String, String> keyedValues = values.getKeyedValues();
         Class elementType = Reflections.getCollectionElementType(type);
         Map map = new HashMap( keyedValues.size() );
         Converter elementConverter = converters.get(elementType);
         for (Map.Entry<String, String> me: keyedValues.entrySet())
         {
            String key = me.getKey();
            Object element = elementConverter.toObject( new FlatPropertyValue( me.getValue() ), elementType );
            map.put(key, element);
         }
         return map;
      }
   }
   
   public static class PropertiesConverter implements Converter<Properties>
   {
      public Properties toObject(PropertyValue values, Type type)
      {
         Map<String, String> keyedValues = values.getKeyedValues();
         Properties map = new Properties();
         Converter elementConverter = converters.get(String.class);
         for ( Map.Entry<String, String> me: keyedValues.entrySet() )
         {
            String key = me.getKey();
            Object element = elementConverter.toObject( new FlatPropertyValue( me.getValue() ), String.class );
            map.put(key, element);
         }
         return map;
      }
   }
   
   public static class ClassConverter implements Converter<Class>
   {
      public Class toObject(PropertyValue value, Type type)
      {
         try
         {
            return Reflections.classForName( value.getSingleValue() );
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new IllegalArgumentException(cnfe);
         }
      }
   }
   
   public static interface PropertyValue extends Serializable
   {
      Map<String, String> getKeyedValues();
      String[] getMultiValues();
      String getSingleValue();
      boolean isExpression();
   }
   
   public static class FlatPropertyValue implements PropertyValue
   {
      
      private String string;

      public FlatPropertyValue(String string)
      {
         if (string==null)
         {
            throw new IllegalArgumentException();
         }
         this.string = string;
      }

      public String[] getMultiValues()
      {
         return Strings.split(string, ", \r\n\f\t");
      }

      public String getSingleValue()
      {
         return string;
      }
      
      public boolean isExpression()
      {
         return string.startsWith("#{");
      }

      public Map<String, String> getKeyedValues()
      {
         throw new UnsupportedOperationException("not a keyed property value");
      }
      
      @Override
      public String toString()
      {
         return string;
      }
      
   }
   
   public static class MultiPropertyValue implements PropertyValue
   {
      
      private String[] strings;

      public MultiPropertyValue(String[] strings)
      {
         if (strings==null) throw new IllegalArgumentException();
         this.strings = strings;
      }

      public String[] getMultiValues()
      {
         return strings;
      }

      public String getSingleValue()
      {
         throw new UnsupportedOperationException("not a flat property value");
      }
      
      public Map<String, String> getKeyedValues()
      {
         throw new UnsupportedOperationException("not a keyed property value");
      }

      public boolean isExpression()
      {
         return false;
      }
      
      @Override
      public String toString()
      {
         return Strings.toString( ", ", (Object[]) strings );
      }
      
   }
   
   public static class AssociativePropertyValue implements PropertyValue
   {
      
      private Map<String, String> keyedValues;

      public AssociativePropertyValue(Map<String, String> keyedValues)
      {
         if (keyedValues==null) throw new IllegalArgumentException();
         this.keyedValues = keyedValues;
      }

      public String[] getMultiValues()
      {
         throw new UnsupportedOperationException("not a multi-valued property value");
      }

      public String getSingleValue()
      {
         throw new UnsupportedOperationException("not a flat property value");
      }
      
      public Map<String, String> getKeyedValues()
      {
         return keyedValues;
      }
      
      public boolean isExpression()
      {
         return false;
      }
      
      @Override
      public String toString()
      {
         return keyedValues.toString();
      }
      
   }
   
}
