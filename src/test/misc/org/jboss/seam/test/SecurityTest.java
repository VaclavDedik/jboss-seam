package org.jboss.seam.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jboss.seam.security.Role;
import org.jboss.seam.security.SeamPermission;
import org.jboss.seam.security.config.SecurityConfigException;
import org.jboss.seam.security.config.SecurityConfiguration;
import org.testng.annotations.Test;

public class SecurityTest
{
  private static final String ROLES_CONFIG = "<roles><role name=\"admin\">" +
      "<members>superuser</members><permissions>" +
      "<permission name=\"user\" action=\"create\"/>" +
      "</permissions></role><role name=\"superuser\">" +
      "<members>user</members><permissions>" +
      "<permission name=\"account\" action=\"create\"/>" +
      "</permissions></role><role name=\"user\"><permissions>" +
      "<permission name=\"customer\" action=\"create\"/>" +
      "</permissions></role></roles>";

  class CustomSecurityConfiguration extends SecurityConfiguration {
    public void loadConfigFromStream(InputStream config)
      throws SecurityConfigException {
        super.loadConfigFromStream(config);
    }
  }

  @Test
  public void testRoleConfiguration()
      throws Exception
  {
    String securityConfig = String.format("<security-config>%s</security-config>",
                                          ROLES_CONFIG);

    CustomSecurityConfiguration config = new CustomSecurityConfiguration();
    config.loadConfigFromStream(new ByteArrayInputStream(securityConfig.getBytes()));

    assert config.getSecurityRoles().contains(new Role("admin"));
    assert config.getSecurityRoles().contains(new Role("superuser"));
    assert config.getSecurityRoles().contains(new Role("user"));

    for (Role r : config.getSecurityRoles())
    {
      if ("admin".equals(r.getName()))
      {
        assert r.hasPermission(new SeamPermission("user", "create"));
        assert r.isMember(new Role("superuser"));
        break;
      }
    }
  }

  /*@Name("mock")
  class MockSecureEntityMethodId {
    private Integer id;
    public MockSecureEntityMethodId(Integer id) { this.id = id; }
    @Id public Integer getId() { return id; }
  }

  @Name("mock")
  class MockSecureEntityFieldId {
    @Id private Integer id;
    public MockSecureEntityFieldId(Integer id) { this.id = id; }
  }

  class MockCompositeId implements Serializable {
    private int fieldA;
    private String fieldB;
    @Override
    public String toString() {
      return String.format("%s,%s", fieldA, fieldB);
    }
    public MockCompositeId(int fieldA, String fieldB) {
      this.fieldA = fieldA;
      this.fieldB = fieldB;
    }
  }

  @Name("mock")
  class MockSecureEntityCompositeId {
    @Id private MockCompositeId id;
    public MockSecureEntityCompositeId(MockCompositeId id) { this.id = id; }
  }

  @Test
  public void testJPAIdentityGenerator()
  {
    JPAIdentityGenerator gen = new JPAIdentityGenerator();
    assert("mock:1234".equals(gen.generateIdentity(new MockSecureEntityMethodId(1234))));
    assert("mock:1234".equals(gen.generateIdentity(new MockSecureEntityFieldId(1234))));
    assert(null == gen.generateIdentity(new MockSecureEntityMethodId(null)));
    assert("mock:1234,abc".equals(gen.generateIdentity(new MockSecureEntityCompositeId(
      new MockCompositeId(1234, "abc")))));
  }

  @Test
  public void testPersistentAcls()
  {
    Ejb3Configuration ac = new Ejb3Configuration();
    System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.LocalOnlyContextFactory");

    ac.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
    ac.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:aname");
    ac.setProperty("hibernate.connection.username", "sa");
    ac.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
    ac.setProperty("hibernate.hbm2ddl.auto", "create");
    //ac.setProperty("hibernate.show_sql", "true");
    ac.setProperty("hibernate.cache.use_second_level_cache", "false");

    ac.addAnnotatedClass(MockAclPermission.class);
    ac.addAnnotatedClass(MockAclObjectIdentity.class);
    ac.addAnnotatedClass(MockSecureEntity.class);

    EntityManagerFactory factory = ac.createEntityManagerFactory();

    EntityManager em = factory.createEntityManager();
    em.getTransaction().begin();

    // Create our mock entity
    MockSecureEntity ent = new MockSecureEntity();
    ent.setId(123);
    em.persist(ent);

    // Now create an identity for it
    MockAclObjectIdentity ident = new MockAclObjectIdentity();
    ident.setId(1);
    ident.setObjectIdentity(new JPAIdentityGenerator().generateIdentity(ent));
    em.persist(ident);

    // And now create some permissions
    //@todo This step should eventually be done using SeamSecurityManager.grantPermission()
    MockAclPermission perm = new MockAclPermission();
    perm.setId(1);
    perm.setIdentity(ident);
    perm.setRecipient("testUser");
    perm.setRecipientType(RecipientType.user);
    perm.setMask(0x01 | 0x02);  // read/delete permission only
    em.persist(perm);
    em.flush();
    em.getTransaction().commit();

    MockServletContext ctx = new MockServletContext();
    MockExternalContext eCtx = new MockExternalContext(ctx);

    new Initialization(ctx).init();

    Lifecycle.beginRequest(eCtx);

    // Create an Authentication object in session scope
    Contexts.getSessionContext().set("org.jboss.seam.security.authentication",
                                     new UsernamePasswordToken("testUser", "",
                                     new String[] {}));

    Component aclProviderComp = new Component(PersistentAclManager.class,
                                              "persistentAclProvider");
    PersistentAclManager aclProvider = (PersistentAclManager) aclProviderComp.newInstance();
    aclProvider.setPersistenceContextManager(factory);
    aclProvider.setAclQuery("select p.mask, p.recipient, p.recipientType from MockAclPermission p " +
        "where p.identity.objectIdentity = :identity");

    MockSecureEntity e2 = em.find(MockSecureEntity.class, 123);

    // This check should pass

    // --> will reinstate once PersistentAclProvider.convertToPermissions() works
    //SeamSecurityManager.instance().checkPermission(e2, "read");

    // This check should fail
    //try
    //{
      //SeamSecurityManager.instance().checkPermission(e2, "special");
      //assert(false);
    //}
    //catch (SecurityException ex) { }

    Lifecycle.endRequest();
  }*/
}
