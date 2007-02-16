package org.jboss.seam.example.spring;

public class TestBean
{
    String name;

    public TestBean() {}
    public TestBean(String name) {
        setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
