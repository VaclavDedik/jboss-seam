package org.jboss.seam.security.openid;

import java.io.Serializable;

import org.openid4java.*;
import org.openid4java.consumer.*;
import org.openid4java.discovery.*;
import org.openid4java.message.*;
import org.openid4java.message.ax.*;

import java.util.List;
import java.io.IOException;

import javax.servlet.http.*;

import org.jboss.seam.annotations.*;
import org.jboss.seam.*;
import org.jboss.seam.faces.*;
import org.jboss.seam.core.*;
import org.jboss.seam.security.*;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@Name("openid")
@Install(precedence=Install.BUILT_IN, classDependencies="org.openid4java.consumer.ConsumerManager")
@Scope(ScopeType.SESSION)
public class OpenId implements Serializable
{
    String id;
    String validatedId;

    ConsumerManager manager;
    DiscoveryInformation discovered;

    @Create
    public void init()
        throws ConsumerException
    {
        manager = new ConsumerManager();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public String returnToUrl() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String returnToUrl = "http://" + request.getServerName() + ":" + request.getServerPort() +  
            context.getApplication().getViewHandler().getActionURL(context, "/openid.xhtml");
        return returnToUrl;
    }
    public void login() 
        throws IOException
    {
        validatedId = null;

        String returnToUrl = returnToUrl();

        System.out.println("return to " + returnToUrl);
        String url = authRequest(id, returnToUrl);
        System.out.println("auth to --> " + url);

        Redirect redirect = Redirect.instance();
        redirect.captureCurrentView();
        
        FacesManager.instance().redirectToExternalURL(url);
    }


    // --- placing the authentication request ---
    @SuppressWarnings("unchecked")
    protected String authRequest(String userSuppliedString, String returnToUrl)
        throws IOException
    {
        try {
            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(userSuppliedString);
            
            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            discovered = manager.associate(discoveries);
            
            //// store the discovery information in the user's session
            // httpReq.getSession().setAttribute("openid-disc", discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            // Attribute Exchange example: fetching the 'email' attribute
            FetchRequest fetch = FetchRequest.createFetchRequest();
            fetch.addAttribute("email",
                               "http://schema.openid.net/contact/email",   // type URI
                               true);                                      // required
            
            // attach the extension to the authentication request
            authReq.addExtension(fetch);

            return authReq.getDestinationUrl(true);
            // httpResp.sendRedirect(authReq.getDestinationUrl(true));
        } catch (OpenIDException e)  {
            e.printStackTrace();
        }
        
        return null;
    }

    public void verify() 
    {       
        ExternalContext    context = javax.faces.context.FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        
        validatedId = verifyResponse(request);
    }


    public boolean loginImmediately() {
        System.out.println("* LOGIN IMMEDIATELY! " + validatedId);
        if (validatedId !=null) {
            Identity.instance().acceptExternallyAuthenticatedPrincipal((new OpenIdPrincipal(validatedId)));
            return true;
        } 

        return false;
    }

    public boolean isValid() {
        System.out.println("is valid?" + validatedId);
        return validatedId != null;
    }

    public String validatedId() {
        return validatedId;
    }

    @SuppressWarnings("unchecked")
    public String verifyResponse(HttpServletRequest httpReq)
    {
        try {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response =
                new ParameterList(httpReq.getParameterMap());
            

            System.out.println("DISCOVERED IS " + discovered);

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = httpReq.getRequestURL();
            String queryString = httpReq.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(httpReq.getQueryString());
            
            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(
                                                             receivingURL.toString(),
                                                             response, discovered);
            
            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess =
                    (AuthSuccess) verification.getAuthResponse();
                
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp = (FetchResponse) authSuccess
                        .getExtension(AxMessage.OPENID_NS_AX);
                    
                    List emails = fetchResp.getAttributeValues("email");
                    String email = (String) emails.get(0);
                    System.out.println("XXX email is " + email);
                }
                
                return verified.getIdentifier();
            }
        } catch (OpenIDException e) {
            // present error to the user
        }
        
        return null;
    }

}
