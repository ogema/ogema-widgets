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
package org.ogema.model.user;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.prototypes.PhysicalElement;

/** 
 * put an instance of this resource for each WLAN into a ResourceList
 * Beta
 */
public interface UserPresenceDetection extends PhysicalElement {

	/**Person for which presence is indicated here*/
	public NaturalPerson user();
	/** If set to false the person presence is only detected manually*/
	public BooleanResource trackPresence();

	@NonPersistent
	public BooleanResource presenceDetected();
}
