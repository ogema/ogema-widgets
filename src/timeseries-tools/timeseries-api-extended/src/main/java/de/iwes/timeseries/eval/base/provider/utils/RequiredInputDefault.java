/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.timeseries.eval.base.provider.utils;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class RequiredInputDefault implements RequiredInputData {
	private final String id;
	private final String label;
	private final String description;
	private final int cardinalityMin;
	private final int cardinalityMax;
	private final Class<? extends SingleValueResource> requestedInputType;
	
	public RequiredInputDefault(String id, String label, String description, int cardinalityMin, int cardinalityMax,
			Class<? extends SingleValueResource> requestedInputType) {
		super();
		this.id = id;
		this.label = label;
		this.description = description;
		this.cardinalityMin = cardinalityMin;
		this.cardinalityMax = cardinalityMax;
		this.requestedInputType = requestedInputType;
	}
	public RequiredInputDefault(String id, String label, String description,
			Class<? extends SingleValueResource> requestedInputType) {
		this(id, label, description, 1, Integer.MAX_VALUE, requestedInputType);
	}
	public RequiredInputDefault(String label, String description,
			Class<? extends SingleValueResource> requestedInputType) {
		this(ResourceUtils.getValidResourceName(label), label, description, requestedInputType);
	}
	public RequiredInputDefault(String label, String description,
			Class<? extends SingleValueResource> requestedInputType, int singleCardinalityOption) {
		this(ResourceUtils.getValidResourceName(label), label, description,
				singleCardinalityOption, singleCardinalityOption, requestedInputType);
	}
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public String description(OgemaLocale locale) {
		return description;
	}

	@Override
	public int cardinalityMin() {
		return cardinalityMin;
	}

	@Override
	public int cardinalityMax() {
		return cardinalityMax;
	}

	@Override
	public Class<? extends SingleValueResource> requestedInputType() {
		return requestedInputType;
	}

}
