package org.jboss.seam.pdf.ui;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

public class UIChartData 
    extends ITextComponent 
{ 
    String key;
    String columnKey;
    String rowKey;
    Number value;
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }
    
    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public void setValue(Double value) {        
        this.value = value;
    }

    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        super.encodeEnd(context);
        
        key = (String) valueBinding(context, "key", key);
        rowKey = (String) valueBinding(context, "rowkey", rowKey);
        columnKey = (String) valueBinding(context, "columnKey", columnKey);
        value = (Number) valueBinding(context, "value", value);
        
        UIChart chart = (UIChart) findITextParent(getParent(), UIChart.class);
        if (chart != null) {            
            Dataset dataset = chart.getDataset();
            
            if (dataset instanceof DefaultPieDataset) {
                DefaultPieDataset piedata = (DefaultPieDataset) dataset;
                piedata.setValue(key, value);
            } else if (dataset instanceof DefaultCategoryDataset) {
                DefaultCategoryDataset data = (DefaultCategoryDataset) dataset;
                data.addValue(value, rowKey, columnKey);                
            } else {
                throw new RuntimeException("Cannot add data to dataset of type " + dataset.getClass());
            }
            
        }
    }
    
    
    @Override
    public void createITextObject(FacesContext context) {
     
    }

    @Override
    public Object getITextObject() {      
        return null;
    }

    @Override
    public void handleAdd(Object other) {
        
    }

    @Override
    public void removeITextObject() {
      
    }

}
