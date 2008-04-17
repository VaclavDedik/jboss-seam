/*
 * JBoss, Home of Professional Open Source
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.seam.init;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.testng.Assert;

/**
 * <p>Converts an <a href="http://www.w3.org/TR/xml-names/">XML namespace</a> to a Java package name.</p>
 *
 * <p>The conversion algorithm is as follows:
 * <ul>
 *   <li>The XML namespace is parsed using <code><a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html">
 *     java.net.URI</a></code>. Only absolute URIs are supported (i.e., a scheme must be specified).</li>
 *   <li>URIs must be <i>hierarchical</i> (i.e., the scheme must be followed by <code>//</code>) with one exception:
 *     <ul><li>If the scheme is <code>seam:</code>, the URI is considered <i>opaque</i>, and is converted to a Java
 *       package using <a href="#seam_scheme">alternate rules</a>.</li></ul></li>
 *   <li>The authority component must be <i>server-based</i> (nearly all URI schemes currently in use are server-based).
 *   <li>The host portion of the namespace is converted as described by <a
 *     href="http://java.sun.com/docs/books/jls/third_edition/html/packages.html#7.7">Section 7.7</a> of the
 *     <i>Java Language Specification, 3rd Edition</i></li>. That is, "subdomains" are reversed from left-to-right to
 *       right-to-left order. The top-level domain becomes the root Java package. </li>
 *   <li>The path, as returned by <code><a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html#getPath()">
 *     URI.getPath()</a></code> is mapped to further Java packages such that each path element becomes another Java package
 *     appended in left-to-right order.</li>
 *   <li>A leading <code>www</code> subdomain, if specified, is ignored.</li>
 *   <li>Values returned by <code><a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html#getUserInfo()">
 *     URI.getUserInfo()</a></code>, <code><a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html#getPort()">
 *     URI.getPort()</a></code>, <code><a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html#getQuery()">
 *     URI.getQuery()</a></code>, and <code><a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html#getFragment()">
 *     URI.getFragment()</a></code> are ignored.</li>
 * </ul></p>
 * 
 * <h2><a id="seam_proto" name="#seam_scheme">The <code>seam:</code> Scheme</a></h2>
 *
 * <p>If the scheme is <code>seam:</code>, the URI is considered <i>opaque</i>, and is converted to a Java package using
 * these rules:
 * <ul>
 *   <li>The <i>scheme-specific-part</i> is parsed into components using the Java package delimiter, <code>period</code> (".")</li>
 *   <li>Each component is appended, in left-to-right order, to build a complete Java package.</li>
 * </ul>
 * </p>
 *
 * <p>Characters specified in the URI which are not valid characters for Java packages result in {@link #resolve(String)}
 * throwing <code>IllegalArgumentException</code>, with one exception: the hyphen ("-") character is converted to
 * the valid Java package characer, the underscore ("_").</p>
 *
 * <h3>Valid Examples</h3>
 * 
 *  <ul>
 *    <li>seam:com.company.department.product ==> com.company.department.product</li>
 *    <li>seam:org.acme-widgets.shipping.persistence ==> org.acme_widgets.shipping.persistence</li>
 *    <li>http://www.company.com/department/product ==> com.company.department.product</li>
 *    <li>https://my-company.com/department/product ==> com.my_company.department.product</li>
 *    <li>http://ericjung:password@www.company.com:8080/foo/bar/baz#anchor?param1=332&param2=334 ==> com.company.foo.bar.baz</li>
 * </ul>
 * <h3>Invalid Examples</h3>
 * <ul>    
 * 	<li>http://cats.import.com (<code>import</code> is a java keyword)</li>
 * 	<li>http://bar#foo#com</li>
 *    <li>seam:com!company!department</li>
 *    <li>com.company.department</li>
 *    <li>mailto:java-net@java.sun.com</li>
 *    <li>news:comp.lang.java</li>
 *    <li>urn:isbn:096139210x</li>    	
 * </ul>
 *
 * @author <a href="mailto:eric DOT jung AT yahoo DOT com">Eric H. Jung</a>
 */
public class NamespacePackageResolver {
	private static final String JAVA_SCHEME = "java";

	private static final LogProvider log = 
		Logging.getLogProvider(NamespacePackageResolver.class);


	/**
	 * <p>Converts an XML namespace, <code>ns</code>, to a Stringified package name according to the rules
	 * detailed in this class's javadoc.</p>
	 *
	 * <p>Characters specified in <code>ns</code> which are not valid characters
	 * for Java packages result <code>IllegalArgumentException</code> being thrown, with one exception. The
	 * hyphen ("-") character is converted to the valid Java package characer, the underscore ("_").</p>
	 *
	 * @param ns the xml namespace to convert
	 * 
	 * @returns a namespace descriptor
	 */
	public String resolve(final String ns) {
		try {
			return parseURI(new URI(ns));
		} catch (Exception e) {
			// the exact exception doesn't matter here.  The caller
			// can log if needed
			return null;
		}
	}

	private String parseURI(URI uri) {
		if (!uri.isAbsolute()) {
			throw new IllegalArgumentException(uri + " is not an absolute URI");
		}
		
		return uri.isOpaque() ? parseOpaqueURI(uri) : parseHierarchicalURI(uri);
	}

	
	/**
	 * java:package 
	 * seam:component
	 * seam:package:prefix
	 */
	private String parseOpaqueURI(URI uri) {
		if (uri.getScheme().equalsIgnoreCase(JAVA_SCHEME)) {
			return uri.getSchemeSpecificPart();
		}
		throw new IllegalArgumentException("Unrecognized scheme in " + uri);
	}

	private String parseHierarchicalURI(URI uri) {
		String scheme = uri.getScheme().toLowerCase();
		if (!scheme.equals("http") && !scheme.equals("https")) {
			throw new IllegalArgumentException("Hierarchical URLs must use http or https scheme " + uri);
		}
		
		StringBuffer buf = new StringBuffer();
		
		appendToPackageName(buf, hostnameToPackage(uri.getHost()));
		appendToPackageName(buf, pathToPackage(uri.getPath()));
		
		return buf.toString();
	}

	/**
	 * Convert path elements to package names in forward order
	 */
	String pathToPackage(String path) {
		StringBuffer buf = new StringBuffer();
		
		if (path != null) {
			String[] pathElements = path.split("/");
			for (int i = 1, len = pathElements.length; i < len; i++) {
				appendToPackageName(buf, pathElements[i]);
			}
		}
		
		return buf.toString();
	}
	
	String hostnameToPackage(String hostname) {
		StringBuffer result = new StringBuffer(); 
		
		String[] subdomains = hostname.split("\\.");

		//Iterate through the subdomains in reverse converting each to a package name. 
		for (int i = subdomains.length - 1; i >= 0; i--) {
			String subdomain = subdomains[i];
			if (i > 0 || !subdomain.equalsIgnoreCase("www")) {
				appendToPackageName(result, subdomain);
			}
		}
		
		return result.toString();
	}	

	private void appendToPackageName(StringBuffer buf, String subdomain) {
		if (subdomain.length()>0) {
			subdomain = makeSafeForJava(subdomain);
	
			if (buf.length() > 0) {
				buf.append('.');
			}
			
			buf.append(subdomain);
		}
	}

	/**
	 * Converts characters in <code>subdomain</code> which aren't java-friendly
	 * into java-friendly equivalents. Right now, we only support the conversion
	 * of hyphens ("-") to underscores ("_"). We could do other things like toLowerCase(),
	 * but there are instances of upper-case package names in widespread use even by the
	 * likes of IBM (e.g., <a href="http://publib.boulder.ibm.com/infocenter/db2luw/v8/index.jsp?topic=/com.ibm.db2.udb.dc.doc/dc/r_jdbcdrivers.htm">
	 * COM.ibm.db2 classnames</a>).
	 * 
	 * @param subdomain
	 * @return
	 */
	private String makeSafeForJava(String subdomain) {
		return subdomain.replace("-", "_");
	}

}
