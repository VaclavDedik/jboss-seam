package org.jboss.seam.example.excel.test;

import java.io.File;
import java.io.ByteArrayInputStream;

import jxl.Sheet;
import jxl.Workbook;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.document.ByteArrayDocumentData;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.mock.JUnitSeamTest;
import org.junit.runner.RunWith;
import org.junit.Test;

/**
 * @author Daniel Roth
 * 
 *         Really small test. Renders an jxl excel file from jsf tags and
 *         verifies the content.
 * 
 */
@RunWith(Arquillian.class)
public class RenderTest extends JUnitSeamTest {

    @Deployment(name="RenderTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment()
    {
        EnterpriseArchive er = ShrinkWrap.create(ZipImporter.class, "seam-excel.ear").importFrom(new File("../excel-ear/target/seam-excel.ear"))
            .as(EnterpriseArchive.class);
        WebArchive web = er.getAsType(WebArchive.class, "excel-web.war");
        web.addAsWebInfResource(new StringAsset("org.jboss.seam.mock.MockFacesContextFactory"), "classes/META-INF/services/javax.faces.context.FacesContextFactory");
        web.addClasses(RenderTest.class);

        return er;
    }

    @Test
    public void testSimple() throws Exception {

        new FacesRequest() {

            @Override
            protected void updateModelValues() throws Exception {
            }

            @Override
            protected void invokeApplication() throws Exception {

                Renderer.instance().render("/simple.xhtml");

                DocumentData data = (DocumentData) Contexts.getEventContext().get("testExport");
                Workbook workbook = Workbook.getWorkbook(new ByteArrayInputStream(((ByteArrayDocumentData)data).getData()));
                Sheet sheet = workbook.getSheet("Developers");
                
                assert sheet != null;

                assert "Daniel Roth".equals(sheet.getCell(0, 0).getContents());
                assert "Nicklas Karlsson".equals(sheet.getCell(0, 1).getContents());

            }
        }.run();
    }

    public static void main(String[] args) {
        RenderTest t = new RenderTest();
        try {
            t.testSimple();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
