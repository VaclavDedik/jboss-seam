package org.jboss.seam.example.seamdiscs.test;

import javax.faces.model.DataModel;

import org.jboss.seam.example.seamdiscs.model.Disc;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DisplayDiscTest extends DBUnitSeamTest
{
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }

    @Test
    public void testDisplayDiscs() throws Exception
    {
        new FacesRequest("/discs.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object discs = getValue("#{discs.dataModel}");
                assert discs instanceof DataModel;
                DataModel dataModel = (DataModel) discs;
                
                // Check for the correct number of results
                assert dataModel.getRowCount() == 6;
                
                // Check for correct ordering
                assertDisc(dataModel, 0, "Blood on the Tracks", "Bob Dylan");
                assertDisc(dataModel, 1, "Chavez Ravine", "Ry Cooder");
                assertDisc(dataModel, 2, "Dark Side of the Moon", "Pink Floyd");
                assertDisc(dataModel, 3, "Liege and Lief", "Fairport Convention");
                assertDisc(dataModel, 4, "The Freewheelin' Bob Dylan", "Bob Dylan");
                assertDisc(dataModel, 5, "The Wall", "Pink Floyd");
            }
            
        }.run();
    }
    
    @Test
    public void testDisplayDisc() throws Exception
    {
        // TODO Test navigation, but need a MockNavigationHandler
        new NonFacesRequest("/discs.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("actionOutcome", "disc");
                setParameter("discId", "3");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 3).equals(getValue("#{discHome.id}"));
                Object object = null;
                object = getValue("#{disc}");
                assert object instanceof Disc;
                Disc disc1 = (Disc) object;
                assert "The Wall".equals(disc1.getName());
            }
        }.run();
    }
    
    
    
    private void assertDisc(DataModel dataModel, int row, String discName, String artistName)
    {
        dataModel.setRowIndex(row);
        Object rowData = dataModel.getRowData();
        assert rowData instanceof Disc;
        Disc disc = (Disc) rowData;
        assert discName.equals(disc.getName());
        assert artistName.equals(disc.getArtist().getName());
    }
    
}
