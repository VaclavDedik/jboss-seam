package @actionPackage@;

import java.util.Set;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;


@Name("authenticator")
public class Authenticator
{
    @Logger Log log;
   
    public boolean authenticate(String username, String password, Set<String> roles)
    {
        log.info("authenticating #0", username);
        //write your authentication logic here,
        //return true if the authentication was
        //successful, false otherwise
        return true;
    }
}
