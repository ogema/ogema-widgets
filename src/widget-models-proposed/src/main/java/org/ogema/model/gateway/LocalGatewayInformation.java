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
package org.ogema.model.gateway;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;

/** 
 * Information on the OGEMA Gateway
 * Use {@link PhysicalElement#name()} for a human readable name 
 */
public interface LocalGatewayInformation extends PhysicalElement {
	
	/**
	 * Id of the OGEMA gateway / instance. The id should be unique within all OGEMA gateways in a project
	 * and within all gateways connected to common cloud services.
	 */
	StringResource id();
}