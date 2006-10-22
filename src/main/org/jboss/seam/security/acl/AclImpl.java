package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Default Acl implementation.
 *
 * @author Shane Bryzak
 */
public class AclImpl implements Acl
{
  /** @todo Investigate whether we should support Groups in the future */

  private class AclPair
  {
    private AclEntry negative;
    private AclEntry positive;

    public boolean hasNegative()
    {
      return negative != null;
    }

    public boolean hasPositive()
    {
      return positive != null;
    }

    public AclEntry getNegative()
    {
      return negative;
    }

    public void setNegative(AclEntry value)
    {
      negative = value;
      checkDuplicates();
    }

    public AclEntry getPositive()
    {
      return positive;
    }

    public void setPositive(AclEntry value)
    {
      positive = value;
      checkDuplicates();
    }

    /**
     * Duplicates are removed as per Acl.getPermissions() spec.
     */
    private void checkDuplicates()
    {
      if (negative != null && positive != null)
      {
        Set<Permission> dupes = new HashSet<Permission>();

        Enumeration<Permission> e = negative.permissions();
        while (e.hasMoreElements())
        {
          Permission neg = e.nextElement();

          Enumeration<Permission> e2 = positive.permissions();
          while (e2.hasMoreElements())
          {
            Permission pos = e2.nextElement();
            if (neg.equals(pos))
            {
              dupes.add(pos);
              break;
            }
          }
        }

        for (Permission dupe : dupes)
        {
          negative.removePermission(dupe);
          positive.removePermission(dupe);
        }
      }
    }
  }

  /**
   * The Owners of this Acl
   */
  private Set<Principal> owners = new HashSet<Principal>();

  /**
   * The name of this Acl
   */
  private String name;

  /**
   * Each Principal in this Acl can have at most one positive and one negative entry
   */
  private Map<Principal,AclPair> entries = new HashMap<Principal,AclPair>();

  /**
   * Construct a new Acl owned by the specified Principal.
   *
   * @param owner Principal
   */
  public AclImpl(Principal owner)
  {
    owners.add(owner);
  }

  public boolean addEntry(Principal caller, AclEntry entry)
      throws NotOwnerException
  {
    if (!isOwner(caller))
      throw new NotOwnerException();

    if (!entries.containsKey(caller))
    {
      synchronized(entries)
      {
        if (!entries.containsKey(caller))
          entries.put(caller, new AclPair());
      }
    }

    AclPair pair = entries.get(caller);

    if (entry.isNegative())
    {
      if (pair.hasNegative())
        return false;
      else
        pair.setNegative(entry);
    }
    else
    {
      if (pair.hasPositive())
        return false;
      else
        pair.setPositive(entry);
    }

    return true;
  }

  public boolean checkPermission(Principal principal, Permission permission)
  {
    AclPair pair = entries.get(principal);
    if (pair == null || !pair.hasPositive())
      return false;

    return pair.getPositive().checkPermission(permission);
  }

  public Enumeration<AclEntry> entries()
  {
    List<AclEntry> allEntries = new ArrayList();
    for (Principal p : entries.keySet())
    {
      AclPair pair = entries.get(p);
      if (pair.hasPositive())
        allEntries.add(pair.getPositive());
      if (pair.hasNegative())
        allEntries.add(pair.getNegative());
    }

    final Iterator<AclEntry> iter = allEntries.iterator();

    return new Enumeration() {
      public boolean hasMoreElements() {
        return iter.hasNext();
      }
      public Object nextElement() {
        return iter.next();
      }
    };
  }

  public Enumeration getPermissions(Principal user)
  {
    if (!entries.containsKey(user))
    {
      return new Enumeration() {
        public boolean hasMoreElements() { return false; }
        public Object nextElement() { throw new NoSuchElementException(); }
      };
    }
    else
      return entries.get(user).getPositive().permissions();
  }

  public boolean removeEntry(Principal caller, AclEntry entry)
      throws NotOwnerException
  {
    if (!isOwner(caller))
      throw new NotOwnerException();

    if (!entries.containsKey(caller))
      return false;

    AclPair pair = entries.get(caller);

    if (entry.isNegative() && pair.getNegative().equals(entry))
    {
      pair.setNegative(null);
      return true;
    }
    else if (!entry.isNegative() && pair.getPositive().equals(entry))
    {
      pair.setPositive(null);
      return true;
    }

    return false;
  }

  public void setName(Principal caller, String name)
      throws NotOwnerException
  {
    if (!isOwner(caller))
      throw new NotOwnerException();

    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public boolean addOwner(Principal caller, Principal owner)
      throws NotOwnerException
  {
    if (!isOwner(caller))
      throw new NotOwnerException();

    return owners.add(owner);
  }

  public boolean deleteOwner(Principal caller, Principal owner)
      throws NotOwnerException, LastOwnerException
  {
    if (!isOwner(caller))
      throw new NotOwnerException();

    if (owners.contains(owner) && owners.size() == 1)
      throw new LastOwnerException();

    return owners.remove(owner);
  }

  public boolean isOwner(Principal owner)
  {
    return owners.contains(owner);
  }
}
