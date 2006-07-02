package org.jboss.seam.util;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
      //put(Date.class, new DateTimeConverter());
      //put(Short.class, new ShortConverter());
      //put(Byte.class, new ByteConverter());
      //put(BigInteger.class, new BigIntegerConverter());
      //put(BigDecimal.class, new BigDecimalConverter());
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
      public Z toObject(String string, Type type); 
   }
   
   public static class BooleanConverter implements Converter<Boolean>
   {
      public Boolean toObject(String string, Type type)
      {
         return Boolean.valueOf(string);
      }
   }
   
   public static class IntegerConverter implements Converter<Integer>
   {
      public Integer toObject(String string, Type type)
      {
         return Integer.valueOf(string);
      }
   }
   
   public static class LongConverter implements Converter<Long>
   {
      public Long toObject(String string, Type type)
      {
         return Long.valueOf(string);
      }
   }
   
   public static class FloatConverter implements Converter<Float>
   {
      public Float toObject(String string, Type type)
      {
         return Float.valueOf(string);
      }
   }
   
   public static class DoubleConverter implements Converter<Double>
   {
      public Double toObject(String string, Type type)
      {
         return Double.valueOf(string);
      }
   }
   
   public static class CharacterConverter implements Converter<Character>
   {
      public Character toObject(String string, Type type)
      {
         return string.charAt(0);
      }
   }
   
   public static class StringConverter implements Converter<String>
   {
      public String toObject(String string, Type type)
      {
         return string;
      }
   }
   
   public static class StringArrayConverter implements Converter<String[]>
   {
      public String[] toObject(String string, Type type)
      {
         return Strings.split(string, ", ()\r\n\f\t");
      }
   }
   
   public static class ArrayConverter implements Converter
   {
      public Object toObject(String string, Type type)
      {
         String[] strings = getConverter(String[].class).toObject(string, String[].class);
         Class elementType = ( (Class) type ).getComponentType();
         Object objects = Array.newInstance( elementType, strings.length );
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length; i++)
         {
            Array.set( objects, i, elementConverter.toObject(strings[i], elementType) );
         }
         return objects;
      }
   }
   
   public static class SetConverter implements Converter<Set>
   {
      public Set toObject(String string, Type type)
      {
         String[] strings = getConverter(String[].class).toObject(string, String[].class);
         Class elementType = Reflections.getCollectionElementType(type);
         Set set = new HashSet(strings.length);
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length; i++)
         {
            set.add( elementConverter.toObject(strings[i], elementType) );
         }
         return set;
      }
   }
   
   public static class ListConverter implements Converter<List>
   {
      public List toObject(String string, Type type)
      {
         String[] strings = getConverter(String[].class).toObject(string, String[].class);
         Class elementType = Reflections.getCollectionElementType(type);
         List list = new ArrayList(strings.length);
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length; i++)
         {
            list.add( elementConverter.toObject(strings[i], elementType) );
         }
         return list;
      }
   }
   
   public static class MapConverter implements Converter<Map>
   {
      public Map toObject(String string, Type type)
      {
         String[] strings = getConverter(String[].class).toObject(string, String[].class);
         Class elementType = Reflections.getCollectionElementType(type);
         Map map = new HashMap(strings.length/2);
         Converter elementConverter = converters.get(elementType);
         for (int i=0; i<strings.length;)
         {
            map.put( strings[i++], elementConverter.toObject(strings[i++], elementType) );
         }
         return map;
      }
   }
   
}
