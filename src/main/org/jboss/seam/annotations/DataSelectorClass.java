package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.databinding.DataSelector;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface DataSelectorClass
{
   Class<? extends DataSelector> value();
}
