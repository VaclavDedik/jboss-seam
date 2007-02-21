package org.jboss.seam.example.spring;

import static org.jboss.seam.ScopeType.EVENT;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

@Scope(EVENT)
@Name("changePassword")
public class ChangePasswordAction
{

    @In @Out
    private User user;

    @In(create=true)
    private UserService userService;

    private String verify;

    private boolean changed;

    public void changePassword()
    {
        if (userService.changePassword(user.getUsername(), verify, user.getPassword())) {
            FacesMessages.instance().add("Password updated");
            changed = true;
        } else {
            FacesMessages.instance().add("verify", "Re-enter new password");
            verify=null;
        }
        user = userService.findUser(user.getUsername());
    }

    public boolean isChanged()
    {
        return changed;
    }

    public String getVerify()
    {
        return verify;
    }

    public void setVerify(String verify)
    {
        this.verify = verify;
    }
}
