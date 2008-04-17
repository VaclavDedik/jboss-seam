package org.jboss.seam.init;

import org.jboss.seam.annotations.Namespace;

class NamespaceDescriptor
{
	private String namespace;
	private String packageName;
	private String componentPrefix;

	NamespaceDescriptor(Namespace namespaceAnnotation, Package pkg)
	{
		this.namespace       = namespaceAnnotation.value();
		this.componentPrefix = namespaceAnnotation.prefix();
		this.packageName     = pkg.getName();
	}
	
	NamespaceDescriptor(String namespace, String packageName) {
		this.namespace       = namespace;
		this.packageName     = packageName;
		this.componentPrefix = "";
	}

	public String getNamespace() {
		return namespace;
	}
	
	public String getComponentPrefix() {
		return componentPrefix;
	}

	public String getPackageName() {
		return packageName;
	}

	@Override
	public String toString()
	{
		return "NamespaceDescriptor(" + namespace + ')';
	}
}