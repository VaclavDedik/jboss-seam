package org.jboss.seam.example.pdf;

import org.jboss.seam.annotations.*;
import org.jboss.seam.*;

@Name("chart")
@Scope(ScopeType.SESSION)
public class DynamicChart{
    boolean is3d = false;
    boolean legend = true;
    
    String title = "Dynamic Chart";
    String categoryAxisLabel = "Default Category Label"; 
    String valueAxisLabel = "Default Value Label";
    
    String orientation = "vertical";
    String plotBackgroundPaint;
    String plotOutlinePaint;
    String borderPaint;
    String borderBackgroundPaint;
    
    
    float height = 300;
    float width  = 400;    
    
    boolean borderVisible = true;
    
    public boolean getIs3d() {
        return is3d;
    }

    public void setIs3d(boolean is3d) {
        this.is3d = is3d;
    }

    public String getCategoryAxisLabel() {
        return categoryAxisLabel;
    }

    public void setCategoryAxisLabel(String categoryAxisLabel) {
        this.categoryAxisLabel = categoryAxisLabel;
    }

    public String getValueAxisLabel() {
        return valueAxisLabel;
    }

    public void setValueAxisLabel(String valueAxisLabel) {
        this.valueAxisLabel = valueAxisLabel;
    }

    public boolean isBorderVisible() {
        return borderVisible;
    }

    public void setBorderVisible(boolean borderVisible) {
        this.borderVisible = borderVisible;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isLegend() {
        return legend;
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getBorderBackgroundPaint() {
        return borderBackgroundPaint;
    }

    public void setBorderBackgroundPaint(String borderBackgroundPaint) {
        this.borderBackgroundPaint = borderBackgroundPaint;
    }

    public String getBorderPaint() {
        return borderPaint;
    }

    public void setBorderPaint(String borderPaint) {
        this.borderPaint = borderPaint;
    }

    public String getPlotBackgroundPaint() {
        return plotBackgroundPaint;
    }

    public void setPlotBackgroundPaint(String plotBackgroundPaint) {
        this.plotBackgroundPaint = plotBackgroundPaint;
    }

    public String getPlotOutlinePaint() {
        return plotOutlinePaint;
    }

    public void setPlotOutlinePaint(String plotOutlinePaint) {
        this.plotOutlinePaint = plotOutlinePaint;
    }


}
