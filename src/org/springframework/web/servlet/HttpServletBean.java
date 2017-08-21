/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.springframework.web.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;

/**
 * Simple extension of javax.servlet.http.HttpServlet that treats its config
 * parameters as bean properties. A very handy superclass for any type of servlet.
 * Type conversion is automatic. It is also possible for subclasses to specify
 * required properties.
 * 对HttpServlet做一个简单的扩展，将它的配置参数当作bean的属性，对于任何类型的servlet来说都是非常方便的父类。
 * 类型转换是自动的;子类也可以指定所需的属性
 *
 * <p>This servlet leaves request handling to subclasses, inheriting the
 * default behaviour of HttpServlet.
 * 这个servlet将请求的处理留给子类，继承了HttpServlet的默认行为
 *
 * <p>This servlet superclass has no dependency on a Spring application context.
 * 这个servlet父类不依赖任何Spring相关的application context.
 *
 * @author Rod Johnson
 * @see #initServletBean
 */
public abstract class HttpServletBean extends HttpServlet {
	
	protected final Log logger = LogFactory.getLog(getClass());

	/** 
	 * List of required properties (Strings) that must be supplied as config parameters to this servlet.
	 * 所需的属性列表必需以配置参数的形式提供给本servlet
	 */
	private List requiredProperties = new ArrayList();

	/**
	 * Subclasses can invoke this method to specify that this property
	 * (which must match a JavaBean property they expose) is mandatory,
	 * and must be supplied as a config parameter.
	 * 子类必需调用这个方法指定requiredProperties,并且必需以配置参数的方式提供。
	 * 
	 * @param property name of the required property
	 */
	protected final void setRequiredProperty(String property) {
		this.requiredProperties.add(property);
	}
	
	/**
	 * Map config parameters onto bean properties of this servlet, and
	 * invoke subclass initialization.
	 * 将配置参数映射到此servlet的bean属性上，并调用子类初始化
	 * @throws ServletException if bean properties are invalid (or required
	 * properties are missing), or if subclass initialization fails.
	 */
	public final void init() throws ServletException {
		logger.info("Initializing servlet '" + getServletName() + "'");

		// set bean properties
		try {
			String[] reqPropArray = (String[]) this.requiredProperties.toArray(new String[this.requiredProperties.size()]);
			PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), reqPropArray);
			BeanWrapper bw = new BeanWrapperImpl(this);
			bw.setPropertyValues(pvs);
		}
		catch (BeansException ex) {
			logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
			throw ex;
		}

		// let subclasses do whatever initialization they like
		initServletBean();
		logger.info("Servlet '" + getServletName() + "' configured successfully");
	}
	
	/**
	 * Subclasses may override this to perform custom initialization.
	 * All bean properties of this servlet will have been set before this
	 * method is invoked. This default implementation does nothing.
	 * @throws ServletException if subclass initialization fails
	 */
	protected void initServletBean() throws ServletException {
	}
	
}
