/*
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.Conventions;
import org.springframework.util.Assert;

/**
 * Implementation of {@link Model} based on a {@link ConcurrentHashMap} for use
 * in concurrent scenarios. Exposed to handler methods by Spring Web Reactive
 * typically via a declaration of the {@link Model} interface. There is typically
 * no need to create it within user code. If necessary a controller method can
 * return a regular {@code java.util.Map}, or more likely a
 * {@code java.util.ConcurrentMap}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
@SuppressWarnings("serial")
public class ConcurrentModel extends ConcurrentHashMap<String, Object> implements Model {

	/**
	 * Construct a new, empty {@code ConcurrentModel}.
	 */
	public ConcurrentModel() {
	}

	/**
	 * Construct a new {@code ModelMap} containing the supplied attribute
	 * under the supplied name.
	 * @see #addAttribute(String, Object)
	 */
	public ConcurrentModel(String attributeName, Object attributeValue) {
		addAttribute(attributeName, attributeValue);
	}

	/**
	 * Construct a new {@code ModelMap} containing the supplied attribute.
	 * Uses attribute name generation to generate the key for the supplied model
	 * object.
	 * @see #addAttribute(Object)
	 */
	public ConcurrentModel(Object attributeValue) {
		addAttribute(attributeValue);
	}


	/**
	 * Add the supplied attribute under the supplied name.
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @param attributeValue the model attribute value (can be {@code null})
	 */
	public ConcurrentModel addAttribute(String attributeName, Object attributeValue) {
		Assert.notNull(attributeName, "Model attribute name must not be null");
		put(attributeName, attributeValue);
		return this;
	}

	/**
	 * Add the supplied attribute to this {@code Map} using a
	 * {@link org.springframework.core.Conventions#getVariableName generated name}.
	 * <p><emphasis>Note: Empty {@link Collection Collections} are not added to
	 * the model when using this method because we cannot correctly determine
	 * the true convention name. View code should check for {@code null} rather
	 * than for empty collections as is already done by JSTL tags.</emphasis>
	 * @param attributeValue the model attribute value (never {@code null})
	 */
	public ConcurrentModel addAttribute(Object attributeValue) {
		Assert.notNull(attributeValue, "Model object must not be null");
		if (attributeValue instanceof Collection && ((Collection<?>) attributeValue).isEmpty()) {
			return this;
		}
		return addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
	}

	/**
	 * Copy all attributes in the supplied {@code Collection} into this
	 * {@code Map}, using attribute name generation for each element.
	 * @see #addAttribute(Object)
	 */
	public ConcurrentModel addAllAttributes(Collection<?> attributeValues) {
		if (attributeValues != null) {
			for (Object attributeValue : attributeValues) {
				addAttribute(attributeValue);
			}
		}
		return this;
	}

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map}.
	 * @see #addAttribute(String, Object)
	 */
	public ConcurrentModel addAllAttributes(Map<String, ?> attributes) {
		if (attributes != null) {
			putAll(attributes);
		}
		return this;
	}

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map},
	 * with existing objects of the same name taking precedence (i.e. not getting
	 * replaced).
	 */
	public ConcurrentModel mergeAttributes(Map<String, ?> attributes) {
		if (attributes != null) {
			for (Map.Entry<String, ?> entry : attributes.entrySet()) {
				String key = entry.getKey();
				if (!containsKey(key)) {
					put(key, entry.getValue());
				}
			}
		}
		return this;
	}

	/**
	 * Does this model contain an attribute of the given name?
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @return whether this model contains a corresponding attribute
	 */
	public boolean containsAttribute(String attributeName) {
		return containsKey(attributeName);
	}

	@Override
	public Map<String, Object> asMap() {
		return this;
	}

}
