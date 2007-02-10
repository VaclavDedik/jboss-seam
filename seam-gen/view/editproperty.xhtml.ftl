<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#assign propertyIsId = property.equals(pojo.identifierProperty)>
<#if !propertyIsId || property.value.identifierGeneratorStrategy == "assigned">
<#if pojo.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#assign column = componentProperty.columnIterator.next()>
<#assign propertyType = componentProperty.value.typeName>

                    <tr class="prop">
                        <td class="name">${componentProperty.name}</td>
                        <td class="value">
                            <s:decorate>
<#if propertyType == "date">
	        		           <h:inputText id="${componentProperty.name}" 
	        		                 maxlength="10"
	        		                      size="10"
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
		        	                     value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
		        	               <s:convertDateTime type="date" dateStyle="short" pattern="MM/dd/yyyy"/>
		        	           </h:inputText>
		        	           <s:selectDate for="${property.name}">
		        	               <h:graphicImage url="img/dtpick.gif" style="margin-left:5px"/>
		        	           </s:selectDate>
<#elseif propertyType == "time">
		        	           <h:inputText id="${componentProperty.name}" 
	        		                      size="5"
<#if !column.nullable>
                                      required="true"
</#if>
		        	                     value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
		        	               <s:convertDateTime type="time"/>
		        	           </h:inputText>
<#elseif propertyType == "timestamp">
		        	           <h:inputText id="${componentProperty.name}" 
	        		                      size="16"
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
			                       <s:convertDateTime type="both" dateStyle="short"/>
			                   </h:inputText>
<#elseif propertyType == "big_decimal">
			                   <h:inputText id="${componentProperty.name}" 
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"
			                              size="${column.precision+7}"/>
<#elseif propertyType == "big_integer">
			                   <h:inputText id="${componentProperty.name}" 
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"
			                              size="${column.precision+6}"/>
<#elseif propertyType == "boolean" || propertyType == "yes_no" || propertyType == "true_false">
			                   <h:selectBooleanCheckbox id="${componentProperty.name}"
<#if !column.nullable>
                                                  required="true"
</#if>
<#if propertyIsId>
                                                  disabled="${'#'}{${homeName}.managed}"
</#if>
		        	                                 value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"/>
<#elseif propertyType == "string">
<#if column.length gt 160>
<#if column.length gt 800>
<#assign rows = 10>
<#else>
<#assign rows = (column.length/80)?int>
</#if>
	        		           <h:inputTextarea id="${componentProperty.name}"
	        		                           cols="80"
	        		                           rows="${rows}"
<#if propertyIsId>
                                         disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                          required="true"
</#if>
		        	                         value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"/>
<#else>
<#if column.length gt 100>
<#assign size = 100>
<#else>
<#assign size = column.length>
</#if>
                               <h:inputText id="${componentProperty.name}" 
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
                                          size="${size}"
                                     maxlength="${column.length}"
                                         value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"/>
</#if>
<#else>
		        	           <h:inputText id="${componentProperty.name}"
<#if !column.nullable>
                                      required="true"
</#if>
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"/>
</#if>
                            </s:decorate>
                        </td>
                    </tr>
</#foreach>
<#else>
<#assign column = property.columnIterator.next()>
<#assign propertyType = property.value.typeName>

                    <tr class="prop">
                        <td class="name">${property.name}</td>
                        <td class="value">
                            <s:decorate>
<#if propertyType == "date">
	        		           <h:inputText id="${property.name}" 
	        		                 maxlength="10"
	        		                      size="10"
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
		        	                     value="${'#'}{${homeName}.instance.${property.name}}">
		        	               <s:convertDateTime type="date" dateStyle="short" pattern="MM/dd/yyyy"/>
		        	           </h:inputText>
		        	           <s:selectDate for="${property.name}">
		        	               <h:graphicImage url="img/dtpick.gif" style="margin-left:5px"/>
		        	           </s:selectDate>
<#elseif propertyType == "time">
		        	           <h:inputText id="${property.name}" 
	        		                      size="5"
<#if !column.nullable>
                                      required="true"
</#if>
		        	                     value="${'#'}{${homeName}.instance.${property.name}}">
		        	               <s:convertDateTime type="time"/>
		        	           </h:inputText>
<#elseif propertyType == "timestamp">
		        	           <h:inputText id="${property.name}" 
	        		                      size="16"
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}">
			                       <s:convertDateTime type="both" dateStyle="short"/>
			                   </h:inputText>
<#elseif propertyType == "big_decimal">
			                   <h:inputText id="${property.name}" 
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}"
			                              size="${column.precision+7}"/>
<#elseif propertyType == "big_integer">
			                   <h:inputText id="${property.name}" 
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}"
			                              size="${column.precision+6}"/>
<#elseif propertyType == "boolean" || propertyType == "yes_no" || propertyType == "true_false">
			                   <h:selectBooleanCheckbox id="${property.name}"
<#if !column.nullable>
                                                  required="true"
</#if>
<#if propertyIsId>
                                                  disabled="${'#'}{${homeName}.managed}"
</#if>
		        	                                 value="${'#'}{${homeName}.instance.${property.name}}"/>
<#elseif propertyType == "string">
<#if column.length gt 160>
<#if column.length gt 800>
<#assign rows = 10>
<#else>
<#assign rows = (column.length/80)?int>
</#if>
	        		           <h:inputTextarea id="${property.name}"
	        		                           cols="80"
	        		                           rows="${rows}"
<#if propertyIsId>
                                         disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                          required="true"
</#if>
		        	                         value="${'#'}{${homeName}.instance.${property.name}}"/>
<#else>
<#if column.length gt 100>
<#assign size = 100>
<#else>
<#assign size = column.length>
</#if>
                               <h:inputText id="${property.name}" 
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
                                          size="${size}"
                                     maxlength="${column.length}"
                                         value="${'#'}{${homeName}.instance.${property.name}}"/>
</#if>
<#else>
		        	           <h:inputText id="${property.name}"
<#if !column.nullable>
                                      required="true"
</#if>
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}"/>
</#if>
                            </s:decorate>
                        </td>
                    </tr>
</#if>
</#if>
</#if>
