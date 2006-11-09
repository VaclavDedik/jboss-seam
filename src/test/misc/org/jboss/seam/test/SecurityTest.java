package org.jboss.seam.test;

import javax.persistence.Id;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.acl.JPAIdentityGenerator;
import org.testng.annotations.Test;

public class SecurityTest
{
  @Name("mock")
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

  @Test
  public void testJPAIdentityGenerator()
  {
    JPAIdentityGenerator gen = new JPAIdentityGenerator();
    assert("mock:1234".equals(gen.generateIdentity(new MockSecureEntityMethodId(1234))));
    assert("mock:1234".equals(gen.generateIdentity(new MockSecureEntityFieldId(1234))));
    assert(null == gen.generateIdentity(new MockSecureEntityMethodId(null)));
  }
}
