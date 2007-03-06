package org.jboss.seam.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Resources;

/**
 * Image manipulation and interrogation
 * 
 * @author pmuir
 * 
 */
@Name("org.jboss.seam.core.image")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.BUILT_IN)
@Intercept(InterceptionType.NEVER)
public class Image
{

   public enum Type
   {
      IMAGE_PNG("image/png", ".png", "PNG"), IMAGE_JPEG("image/jpeg", ".jpg", "JPEG", "image/jpg"), IMAGE_GIF(
               "image/gif", ".gif", "GIF");

      private String mimeType;

      private String extension;

      private String imageFormatName;

      private List<String> alternativeMimeTypes;

      Type(String mimeType, String extension, String imageFormatName,
               String... alternativeMimeTypes)
      {
         this.mimeType = mimeType;
         this.extension = extension;
         this.alternativeMimeTypes = Arrays.asList(alternativeMimeTypes);
         this.imageFormatName = imageFormatName;
      }

      public String getMimeType()
      {
         return mimeType;
      }

      public String getExtension()
      {
         return extension;
      }

      public List<String> getAlternativeMimeTypes()
      {
         return alternativeMimeTypes;
      }

      protected String getImageFormatName()
      {
         return imageFormatName;
      }

      public static Type getTypeByMimeType(String mimeType)
      {
         for (Type type : values())
         {
            if (type.getMimeType().equals(mimeType) || type.alternativeMimeTypes.contains(mimeType))
            {
               return type;
            }
         }
         return null;
      }

      public static Type getTypeByFormatName(String formatName)
      {
         for (Type type : values())
         {
            if (type.getImageFormatName().equalsIgnoreCase(formatName))
            {
               return type;
            }
         }
         return null;
      }
   }

   public static final int PNG_IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
   
   public static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

   private static final Type DEFAULT_CONTENT_TYPE = Type.IMAGE_PNG;

   private Object input;

   private byte[] output;

   private boolean dirty;

   private Type contentType = DEFAULT_CONTENT_TYPE;

   private BufferedImage bufferedImage;

   public Image()
   {
   }

   /**
    * Set the image. This can be one of String (loaded from the classpath), a
    * URL, a File, an InputStream or a byte[]
    * 
    * @param value
    * @throws IOException
    */
   public Image setInput(Object value) throws IOException
   {
      this.input = value;
      readImage();
      return this;
   }

   /**
    * Get the image as a byte[], any conversions having been applied. Returns null if the
    * image could not be read
    */
   public byte[] getImage() throws IOException
   {
      if (dirty)
      {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         ImageIO.write(bufferedImage, getContentType().getImageFormatName(), outputStream);
         output = outputStream.toByteArray();
      }
      return output;
   }

   /**
    * The content type of the output image, by default DEFAULT_CONTENT_TYPE
    */
   public Type getContentType()
   {
      return contentType;
   }

   public void setContentType(Type contentType)
   {
      this.contentType = contentType;
   }
   
   public BufferedImage getBufferedImage()
   {
      return bufferedImage;
   }
   
   public void setBufferedImage(BufferedImage bufferedImage)
   {
      this.bufferedImage = bufferedImage;
      dirty = true;
   }

   /**
    * The aspect ratio of the image
    */
   public double getRatio() throws IOException
   {
      // Do the operation with double precision
      Double ratio = (double) bufferedImage.getWidth() / (double) bufferedImage.getHeight();
      return ratio;
   }

   /**
    * Check whether the image is of a given ratio to within a given precision
    */
   public boolean isRatio(double ratio, double precision) throws IOException
   {
      double error = ratio * precision;
      return (ratio - error) < getRatio() && getRatio() <= (ratio + error);
   }

   /**
    * The width of the image
    */
   public int getWidth() throws IOException
   {
      return bufferedImage.getWidth();
   }

   /**
    * The height of the image
    */
   public int getHeight() throws IOException
   {
      return bufferedImage.getHeight();
   }

   /**
    * Alter the ratio of the output image <b>without</b> altering the ratio of
    * the input by adding transparent strips. If the image is already of the
    * correct ratio (to within the given precision) nothing happens
    */
   public Image adjustRatio(double desiredRatio, double precision) throws InterruptedException,
            IOException
   {
      if (!isRatio(desiredRatio, precision))
      {
         if (getRatio() > desiredRatio)
         {
            // The image is too wide - add a transparent strip across the
            // top/bottom to make the image squarer
            double desiredHeight = getRatio() * getHeight() / desiredRatio;
            double stripHeight = (desiredHeight - getHeight()) / 2;
            BufferedImage newImage = new BufferedImage(getWidth(),
                     (int) (getHeight() + stripHeight * 2), getImageType());
            Graphics2D graphics2D = createGraphics(newImage);
            graphics2D.drawImage(bufferedImage, 0, (int) stripHeight, null);
            bufferedImage = newImage;
         }
         else if (getRatio() < desiredRatio)
         {
            // The image is too wide - add a transparent strip across the
            // top/bottom to make the image squarer
            double desiredWidth = getRatio() * getWidth() / desiredRatio;
            double stripWidth = (desiredWidth - getWidth()) / 2;
            BufferedImage newImage = new BufferedImage((int) (getWidth() + stripWidth * 2),
                     getHeight(), getImageType());
            Graphics2D graphics2D = createGraphics(newImage);
            graphics2D.drawImage(bufferedImage, (int) stripWidth, 0, null);
            bufferedImage = newImage;
         }
         dirty = true;
      }
      return this;
   }
   
   /**
    * Blur the output image using a convolution
    */
   public Image blur(int radius) throws IOException {
      BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), getImageType());
      int blurWidth = ((radius - 1) * 2 + 1); 
      int pixels = blurWidth * blurWidth; 
      float weight = 1.0f/ pixels;
      float[] elements = new float[pixels];

      for (int i = 0; i < pixels; i++) {
            elements[i] = weight;
      }

      Kernel kernel = new Kernel(blurWidth, blurWidth, elements);
      ConvolveOp simpleBlur = new ConvolveOp(kernel);

      simpleBlur.filter(bufferedImage, newImage);
      bufferedImage = newImage;
      dirty = true;
      return this;
   }

   /**
    * Scale the image to the given width
    */
   public Image scaleToWidth(int width) throws IOException
   {
      // Always scale, never stretch. We don't care if the requested scaled
      // ratio is different from the current
      int height = width * getHeight() / getWidth();
      BufferedImage newImage = new BufferedImage(width, height, getImageType());
      Graphics2D graphics2D = createGraphics(newImage);
      graphics2D.drawImage(bufferedImage, 0, 0, width, height, null);
      bufferedImage = newImage;
      dirty = true;
      return this;
   }

   /**
    * Scale the image to the given height
    */
   public Image scaleToHeight(int height) throws IOException
   {
      // Always scale, never stretch. We don't care if the requested scaled
      // ratio is different from the current
      int width = height * getWidth() / getHeight();
      BufferedImage newImage = new BufferedImage(width, height, getImageType());
      Graphics2D graphics2D = createGraphics(newImage);
      graphics2D.drawImage(bufferedImage, 0, 0, width, height, null);
      bufferedImage = newImage;
      dirty = true;
      return this;
   }
   
   /**
    * Scale the image by the given factor
    */
   public Image scale(double factor) throws IOException 
   {
      int width = (int) (getWidth() * factor);
      int height = (int) (getHeight() * factor);
      BufferedImage newImage = new BufferedImage(width, height, getImageType());
      Graphics2D graphics2D = createGraphics(newImage);
      graphics2D.drawImage(bufferedImage, 0, 0, width, height, null);
      bufferedImage = newImage;
      dirty = true;
      return this;
   }
   
   /**
    * Resize the image to the given width and height, changing the ratio
    * if necessary
    */
   public Image resize(int width, int height) 
   {
      BufferedImage newImage = new BufferedImage(width, height, getImageType());
      Graphics2D graphics2D = createGraphics(newImage);
      graphics2D.drawImage(bufferedImage, 0, 0, width, height, null);
      bufferedImage = newImage;
      dirty = true;
      return this;
   }

   private void readImage() throws IOException
   {
      if (input instanceof URL)
      {
         readImage(((URL) input).openStream());
      }
      else if (input instanceof File)
      {
         readImage(((File) input).toURL().openStream());
      }
      else if (input instanceof String)
      {
         readImage(Resources.getResourceAsStream((String) input));
      }
      else if (input instanceof InputStream)
      {
         readImage((InputStream) input);
      }
      else if (input != null && input.getClass().isArray())
      {
         if (input.getClass().getComponentType().isAssignableFrom(Byte.TYPE))
         {
            byte[] b = (byte[]) input;
            readImage(new ByteArrayInputStream(b));
         }
      }
   }

   /**
    * Create Canvas, set some defaults (bg colour, rendering hints)
    * 
    * @param image
    */
   private Graphics2D createGraphics(BufferedImage image)
   {
      Graphics2D graphics2D = image.createGraphics();
      graphics2D.setBackground(new Color(255, 255, 255));
      graphics2D.clearRect(0, 0, image.getWidth(), image.getHeight());
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
               RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      return graphics2D;
   }

   public static Image instance()
   {
      if (!Contexts.isConversationContextActive())
      {
         throw new IllegalStateException("No active conversation scope");
      }
      return (Image) Component.getInstance(Image.class, true);
   }

   private void readImage(InputStream inputStream) throws IOException
   {
      ImageInputStream stream = ImageIO.createImageInputStream(inputStream);
      if (stream == null)
      {
         throw new IllegalArgumentException("stream == null!");
      }

      Iterator iter = ImageIO.getImageReaders(stream);
      if (!iter.hasNext())
      {
         return;
      }

      ImageReader reader = (ImageReader) iter.next();
      ImageReadParam param = reader.getDefaultReadParam();
      reader.setInput(stream, true, true);
      String type = reader.getFormatName();
      setContentType(Type.getTypeByFormatName(type));
      bufferedImage = reader.read(0, param);
      stream.close();
      reader.dispose();
      inputStream.reset();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      byte[] buffer = new byte[256];
      while (true)
      {
         int bytesRead = inputStream.read(buffer);
         if (bytesRead == -1) break;
         os.write(buffer, 0, bytesRead);
      }
      output = os.toByteArray();
      inputStream.close();
   }
   
   private int getImageType() {
      if (Type.IMAGE_PNG.equals(getContentType()))
      {
         return PNG_IMAGE_TYPE;
      }
      else
      {
         return DEFAULT_IMAGE_TYPE;
      }
   }
}
