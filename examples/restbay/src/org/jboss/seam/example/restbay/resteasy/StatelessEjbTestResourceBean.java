package org.jboss.seam.example.restbay.resteasy;

import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Stateless
public class StatelessEjbTestResourceBean extends TestResource implements StatelessEjbTestResource
{

   @javax.annotation.Resource // EJB injection!
   javax.ejb.SessionContext ejbSessionContext;


   public String echoUri(@Context UriInfo uriInfo)
   {
      assert ejbSessionContext != null; // Ensure this is executed in the EJB container
      setUriInfo(uriInfo);
      return super.echoUri();
   }

   @Override
   public String echoQueryParam(String bar)
   {
      return super.echoQueryParam(bar);
   }

   @Override
   public String echoHeaderParam(String bar)
   {
      return super.echoHeaderParam(bar);
   }

   @Override
   public String echoCookieParam(String bar)
   {
      return super.echoCookieParam(bar);
   }

   @Override
   public String echoTwoParams(String one, String two)
   {
      return super.echoTwoParams(one, two);
   }

   @Override
   public String echoEncoded(String val)
   {
      return super.echoEncoded(val);
   }

   @Override
   public String echoFormParams(MultivaluedMap<String, String> formMap)
   {
      return super.echoFormParams(formMap);
   }

   @Override
   public String echoFormParams2(String[] foo)
   {
      return super.echoFormParams2(foo);
   }

   @Override
   public String echoFormParams3(TestForm form)
   {
      return super.echoFormParams3(form);
   }

   @Override
   public SubResource getBar(String baz)
   {
      return super.getBar(baz);
   }

   @Override
   public long convertPathParam(GregorianCalendar isoDate)
   {
      return super.convertPathParam(isoDate);
   }

   @Override
   public String throwException()
   {
      return super.throwException();
   }

   public List<String[]> getCommaSeparated(@Context HttpHeaders headers)
   {
      setHeaders(headers);
      return super.getCommaSeparated();
   }

}