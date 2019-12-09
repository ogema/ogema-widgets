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
package de.iwes.timeseries.eval.garo.api.jaxb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.recordeddata.RecordedDataStorage;
import org.ogema.serialization.jaxb.IntegerResource;
import org.ogema.serialization.jaxb.Resource;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;
import org.smartrplace.analysis.backup.parser.api.MemoryGateway;
import org.smartrplace.logging.fendodb.CloseableDataRecorder;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;

public class GaRoSelectionItemJAXB extends GaRoSelectionItem {
	//only relevant for level GW_LEVEL
	private String gwId;
	private MemoryGateway gwData;
	private final GatewayBackupAnalysis gatewayParser;
	private CloseableDataRecorder logData;
	private List<String> recIds;
	
	protected Resource resource;
	
	//only relevant for level ROOM_LEVEL, TS_LEVEL
	public GaRoSelectionItemJAXB getGwSelectionItem() {
		return (GaRoSelectionItemJAXB) gwSelectionItem;
	}
	
	public GaRoSelectionItemJAXB(String gwId, GatewayBackupAnalysis gatewayParser) {
		super(GaRoMultiEvalDataProvider.GW_LEVEL, gwId);
		this.gatewayParser = gatewayParser;
		this.gwId = gwId;
	}
	public GaRoSelectionItemJAXB(String name, Resource room, GaRoSelectionItemJAXB superSelectionItem) {
		super(GaRoMultiEvalDataProvider.ROOM_LEVEL, name);
		this.gatewayParser = null;
		this.gwSelectionItem = superSelectionItem;
		//this.gwId = superSelectionItem.gwId;
		//this.roomId = room.getKey();
		this.resource = room;
	}
	public GaRoSelectionItemJAXB(String tsId, GaRoSelectionItemJAXB superSelectionItem) {
		super(GaRoMultiEvalDataProvider.TS_LEVEL, tsId);
		this.gatewayParser = null;
		this.gwId = superSelectionItem.gwId;
		this.gwSelectionItem = superSelectionItem.gwSelectionItem;
		this.roomSelectionItem = superSelectionItem;
		//this.tsId = tsId;
	}
	
	public MemoryGateway getGwData() {
		if(level == 0) {
			if(gwData == null) try {
				this.gwData = gatewayParser.getGateway(gwId);
			} catch (UncheckedIOException | IOException e) {
				throw new IllegalStateException(e);
			}
			return gwData;
		} else {
			if(getGwSelectionItem().gwData == null) try {
				getGwSelectionItem().gwData = getGwSelectionItem().gatewayParser.getGateway(getGwSelectionItem().gwId);
			} catch (UncheckedIOException | IOException e) {
				throw new IllegalStateException(e);
			}
			return getGwSelectionItem().gwData;
		}
	}
	public CloseableDataRecorder getLogRecorder() {
		if(level == 0) {
			if(logData == null) try {
				this.logData = (CloseableDataRecorder) getGwData().getLogdata().get();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return logData;
		} else {
			if(getGwSelectionItem().logData == null) try {
				getGwSelectionItem().logData = (CloseableDataRecorder) getGwSelectionItem().getGwData().getLogdata().get();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return getGwSelectionItem().logData;
		}
	}
	public List<String> getLogDataIds() {
		if(level == 0) {
			if(recIds == null) try {
				this.recIds = getLogRecorder().getAllRecordedDataStorageIDs();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return recIds;
		} else {
			if(getGwSelectionItem().recIds == null) try {
				getGwSelectionItem().recIds = getGwSelectionItem().getLogRecorder().getAllRecordedDataStorageIDs();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return getGwSelectionItem().recIds;
		}
	}
	
	@Override
	protected List<String> getDevicePaths(GaRoSelectionItem roomSelItem) {
		List<Resource> devices = ((GaRoSelectionItemJAXB)gwSelectionItem).getGwData().getDevicesByRoom(resource).get();
		List<String> result = new ArrayList<>();
		for(Resource dev: devices) result.add(dev.getPath());
		return result;
	}
	
	@Override
	public TimeSeriesData getTimeSeriesData() {
		if(level == GaRoMultiEvalDataProvider.TS_LEVEL) {
			RecordedDataStorage recData = getLogRecorder().getRecordedDataStorage(id);
			return new TimeSeriesDataImpl(recData, id,
					id, InterpolationMode.STEPS);
		}
		return null;
	}
	
	//TODO: Shall be replaced
	//@Override
	protected Resource getResource() {
		if(resource == null) {
			switch(level) {
			case GaRoMultiEvalDataProvider.GW_LEVEL:
				throw new IllegalArgumentException("No gateway resource available");
			case GaRoMultiEvalDataProvider.ROOM_LEVEL:
				return resource;
			case GaRoMultiEvalDataProvider.TS_LEVEL:
				throw new UnsupportedOperationException("Access to resources of data row parents not implemented yet, but should be done!");
			}
		}
		return resource;
	}

	@Override
	public Integer getRoomType() {
		if(resource == null) return null;
		return getRoomTypeStatic(getResource());
	}
	
	public static Integer getRoomTypeStatic(Resource room) {
		Resource typeRes = room.get("type");
		if(typeRes instanceof IntegerResource)
			return ((IntegerResource)typeRes).getValue();
		return null;		
	}

	@Override
	public String getRoomName() {
		if(resource == null) return null;
		return getResource().getName();
	}

	@Override
	public String getPath() {
		if(resource == null) return null;
		return getResource().getPath();
	}
}
