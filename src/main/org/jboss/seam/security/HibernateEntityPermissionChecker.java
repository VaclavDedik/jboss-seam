package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;


@Name("org.jboss.seam.security.entityPermissionChecker")
@Scope(STATELESS)
@Install(precedence = FRAMEWORK, classDependencies={"org.hibernate.Session", "javax.persistence.EntityManager"})
@BypassInterceptors
public class HibernateEntityPermissionChecker extends EntityPermissionChecker
{
   
}
