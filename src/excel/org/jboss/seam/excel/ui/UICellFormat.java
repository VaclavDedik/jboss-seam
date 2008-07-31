package org.jboss.seam.excel.ui;


public abstract class UICellFormat extends ExcelComponent
{
   private String alignment;
   private String comment;
   private Integer commentWidth;
   private Integer commentHeight;
   private Integer indentation;
   private Boolean locked;
   private String mask;
   private String orientation;
   private Boolean shrinkToFit;
   private String verticalAlignment;
   private Boolean wrap;

   public String getAlignment()
   {
      return (String) valueOf("alignment", alignment);
   }

   public void setAlignment(String alignment)
   {
      this.alignment = alignment;
   }

   public String getComment()
   {
      return (String) valueOf("comment", comment);
   }

   public void setComment(String comment)
   {
      this.comment = comment;
   }

   public Integer getIndentation()
   {
      return (Integer) valueOf("indentation", indentation);
   }

   public void setIndentation(Integer indentation)
   {
      this.indentation = indentation;
   }

   public Boolean getLocked()
   {
      return (Boolean) valueOf("locked", locked);
   }

   public void setLocked(Boolean locked)
   {
      this.locked = locked;
   }

   public String getMask()
   {
      return (String) valueOf("mask", mask);
   }

   public void setMask(String mask)
   {
      this.mask = mask;
   }

   public String getOrientation()
   {
      return (String) valueOf("orientation", orientation);
   }

   public void setOrientation(String orientation)
   {
      this.orientation = orientation;
   }

   public Boolean getShrinkToFit()
   {
      return (Boolean) valueOf("shrinkToFit", shrinkToFit);
   }

   public void setShrinkToFit(Boolean shrinkToFit)
   {
      this.shrinkToFit = shrinkToFit;
   }

   public String getVerticalAlignment()
   {
      return (String) valueOf("verticalAlignment", verticalAlignment);
   }

   public void setVerticalAlignment(String verticalAlignment)
   {
      this.verticalAlignment = verticalAlignment;
   }

   public Boolean getWrap()
   {
      return (Boolean) valueOf("wrap", wrap);
   }

   public void setWrap(Boolean wrap)
   {
      this.wrap = wrap;
   }

   public Integer getCommentWidth()
   {
      return (Integer) valueOf("commentWidth", commentWidth);
   }

   public void setCommentWidth(Integer commentWidth)
   {
      this.commentWidth = commentWidth;
   }

   public Integer getCommentHeight()
   {
      return (Integer) valueOf("commentHeight", commentHeight);
   }

   public void setCommentHeight(Integer commentHeight)
   {
      this.commentHeight = commentHeight;
   }

}
