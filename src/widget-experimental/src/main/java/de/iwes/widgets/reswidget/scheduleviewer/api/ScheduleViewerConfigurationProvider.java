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
package de.iwes.widgets.reswidget.scheduleviewer.api;

import java.util.Collection;
import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.reswidget.scheduleviewer.utils.DefaultSessionConfiguration;

public interface ScheduleViewerConfigurationProvider {
	public final static String VIEWER_URL_PATH = "/de/iwes/tools/schedule/viewer-basic-example";

	/**The id of the configurationProvider must be provided as parameter
	 * configProvider with URL
	 */
	String getConfigurationProviderId();
	
	/** Get configuration for specific session
	 * 
	 * @param configurationId paramter configurationId provided with URL. If the parameter is null
	 * 		a standard session configuration for the provider may be returned, but in this case
	 * 		also just null be returned, but no exception should be thrown.
	 * @return
	 */
	SessionConfiguration getSessionConfiguration(String configurationId);
	
	/** Current selections/configurations sent to the provider. The provider can
	 * save these configurations fully or partially or just ignore this information. The
	 * message is generated by the ScheduleViewer when a "Save Configuration" button is pressed.
	 */
	void saveCurrentConfiguration(SelectionConfiguration currentConfiguration, String configurationId);
	
	public interface SessionConfiguration extends SelectionConfiguration {
		/**Provide ScheduleViewerConfiguration. There is a {@link ScheduleViewerConfiguration#DEFAULT_CONFIGURATION}
		 * that is used by the {@link DefaultSessionConfiguration}. Otherwise it is recommended
		 * to use the {@link ScheduleViewerConfigurationBuilder} to set up a specific configuration.
		 * Most important is setting startTime and endTime as well as {@link ScheduleViewerConfigurationBuilder#setPrograms(List)}
		 * for providing basic filters. It is recommended to create elements of {@link TimeSeriesFilterExtended}
		 * in order to provider short name and long name values for the time series.*/
		ScheduleViewerConfiguration viewerConfiguration();
		
		public enum PreSelectionControllability {
			/** The preselection cannot be changed by the user as long as
			 * the configuration is active (e.g. no datepickers / schedule selector visible)
			 */
			FIXED,
			/** Preselected values may be unselected, but no additional elements
			 * may be selected (e.g. the time range may be changed by the user, but no
			 * time ranges outside the predefined interval may be chosen)
			 */
			MAX_SIZE,
			/** The selection may be changed like with schedule viewer without
			 * configuration
			 */
			FLEXIBLE
		}
		PreSelectionControllability intervalControllability();
		PreSelectionControllability timeSeriesSelectionControllability();
		PreSelectionControllability filterControllability();
		/**If true the standard programs of the ScheduleViewer are not shown if not
		 * given with {@link #viewerConfiguration()}. Otherwise the superset
		 * of both program definitions is used.<br>
		 * If true and no programs are provided then the program selector fields shall
		 * not be displayed
		 */
		boolean overwritePrograms();
		boolean overwriteConditionalFilters();
		boolean overwriteProgramlistFixed();
		default boolean overwriteDefaultTimeSeries() {
			return false;
		};
		
		/**If true all time series found via the filter selected are
		 * preselected, otherwise only the time series given explicitly via
		 * {@link #timeSeriesSelected()} are preselected
		 */
		boolean markTimeSeriesSelectedViaPreselectedFilters();
		
		/**If true the chart shall be generated on the initial page loading, otherwise
		 * the chart shall only be generated when the user clicks the "Apply" button
		 */
		boolean generateGraphImmediately();
	}
	
	/** Information on selections made by the user or preselection information*/
	public interface SelectionConfiguration {
		/**Preselected timeseries. The timeseries shall be used initially independently
		 * from preselected filters. If null is returned a default set of time series
		 * is presented. Otherwise only the time series returned or a superset may
		 * be used depending on overwriteDefaultTimeSeries().
		 */
		List<ReadOnlyTimeSeries> timeSeriesSelected();
		/** Timeseries that are offered, but only preselected if they are part of {@link #timeSeriesSelected()}.
		 * Note that all elements of timeSeriesSelected will be offered even if they are
		 * not listed here. If null is returned id is treated like an empty list.*/
		//TODO: Add this later when release 2.2.0 is completely done
		//List<ReadOnlyTimeSeries> timeSeriesOffered();

		/** The programs given here must be part of the respective Collection&lt;TimeSeriesFilter&gt;
		 * provided by #viewerConfiguration().setPrograms. These programs will be
		 * preselected.
		 */
		List<Collection<TimeSeriesFilter>> programsPreselected();
		/**Index of outer list to be used
		 * @see ScheduleViewerConfigurationBuilder#setFilters(List)*/
		Integer conditionalTimeSeriesFilterCategoryPreselected();
		List<ConditionalTimeSeriesFilter<?>> filtersPreSelected();
	}
	
}
