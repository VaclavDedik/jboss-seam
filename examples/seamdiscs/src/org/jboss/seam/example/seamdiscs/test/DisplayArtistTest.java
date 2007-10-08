package org.jboss.seam.example.seamdiscs.test;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.jboss.seam.example.seamdiscs.model.Artist;
import org.jboss.seam.example.seamdiscs.model.Band;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DisplayArtistTest extends DBUnitSeamTest
{
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }

    @Test
    public void testDisplayArtists() throws Exception
    {
        new FacesRequest("/artists.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object artists = getValue("#{artists.dataModel}");
                assert artists instanceof DataModel;
                DataModel artistsDataModel = (DataModel) artists;
                
                // Check for the correct number of results
                assert artistsDataModel.getRowCount() == 6;
                
                // Check for correct ordering
                assertArtist(artistsDataModel, 0, "Ry Cooder");
                assertArtist(artistsDataModel, 1, "Richard Thompson");
                assertArtist(artistsDataModel, 2, "Pink Floyd");
                assertArtist(artistsDataModel, 3, "Led Zepplin");
                assertArtist(artistsDataModel, 4, "Fairport Convention");
                assertArtist(artistsDataModel, 5, "Bob Dylan");
            }
            
        }.run();
    }
    
    @Test
    public void testSeamCollectionModel() throws Exception
    {
        new FacesRequest("/artists.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object artists = getValue("#{artists.dataModel}");
                assert artists instanceof CollectionModel;
                CollectionModel collectionModel = (CollectionModel) artists;
                
                // Reorder the list               
                List<SortCriterion> criteria = new ArrayList<SortCriterion>();
                criteria.add(new SortCriterion("artist.name", true));
                collectionModel.setSortCriteria(criteria);
                
                // Check for correct ordering
                assertArtist(collectionModel, 5, "Ry Cooder");
                assertArtist(collectionModel, 4, "Richard Thompson");
                assertArtist(collectionModel, 3, "Pink Floyd");
                assertArtist(collectionModel, 2, "Led Zepplin");
                assertArtist(collectionModel, 1, "Fairport Convention");
                assertArtist(collectionModel, 0, "Bob Dylan");
            }
            
        }.run();
    }
    
    @Test
    public void testDisplayArtist() throws Exception
    {
        // TODO Test navigation, but need a MockNavigationHandler
        new NonFacesRequest("/artists.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("actionOutcome", "artist");
                setParameter("artistId", "1");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 1).equals(getValue("#{artistHome.id}"));
                Object object = null;
                object = getValue("#{artist}");
                assert object instanceof Band;
                Band artist1 = (Band) object;
                assert "Pink Floyd".equals(artist1.getName());
                assert artist1.getBandMembers().size() == 3;
            }
        }.run();
    }
    
    
    
    private void assertArtist(DataModel dataModel, int row, String name)
    {
        dataModel.setRowIndex(row);
        Object rowData = dataModel.getRowData();
        assert rowData instanceof Artist;
        Artist artist = (Artist) rowData;
        assert name.equals(artist.getName());
    }
    
}
