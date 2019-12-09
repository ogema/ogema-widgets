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
package org.ogema.generictype;

import org.ogema.core.model.Resource;

import de.iwes.timeseries.eval.api.configuration.Configuration;

/** An object is a complex data structure, but the structure considered invariant
 * in time
 */
@Deprecated //not used
public interface GenericDataTypeInstanceObject extends GenericDataTypeInstance {
	/** Resource-based implementations should provide this*/
	Resource getResource();
	Object getObject();
	/** This can be used as input to an evaluation*/
	Configuration<?> evalConfiguration();
}