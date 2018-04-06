/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.timeseries.eval.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.timeseries.eval.viewer.api.ProfileCategory;
import de.iwes.timeseries.eval.viewer.gui.ScheduleViewerPage;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

@References({
	@Reference(
			name="sources",
			referenceInterface=DataProvider.class,
			cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			policy=ReferencePolicy.DYNAMIC,
			bind="addSource",
			unbind="removeSource"),
	@Reference(
			name="profiles",
			referenceInterface=ProfileCategory.class,
			cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			policy=ReferencePolicy.DYNAMIC,
			bind="addProfile",
			unbind="removeProfile")
})
@Component(specVersion = "1.2")
@Service(Application.class)
public class TimeSeriesEvaluationScheduleViewer implements Application {


	public final static Logger logger = LoggerFactory.getLogger(TimeSeriesEvaluationScheduleViewer.class);
//	private ApplicationManager appMan;
	private WidgetApp wapp;
	private final Map<String, DataProvider<?>> sources = Collections.synchronizedMap(new LinkedHashMap<String, DataProvider<?>>());
	private final Map<String, ProfileCategory> profiles = Collections.synchronizedMap(new LinkedHashMap<String, ProfileCategory>());
	private final AtomicInteger sourcesRevision = new AtomicInteger(0);
	private final AtomicInteger profilesRevision = new AtomicInteger(0);
	private final LabelledItemProvider<DataProvider<?>> sourceProviders = new LabelledItemProvider<DataProvider<?>>() {

		@Override
		public int getRevision() {
			return sourcesRevision.get();
		}

		@Override
		public List<DataProvider<?>> getItems() {
			synchronized (sources) {
	    		return new ArrayList<>(sources.values());
			}
		}
	};
	private final LabelledItemProvider<ProfileCategory> profileProviders = new LabelledItemProvider<ProfileCategory>() {

		@Override
		public int getRevision() {
			return profilesRevision.get();
		}

		@Override
		public List<ProfileCategory> getItems() {
			synchronized (profiles) {
	    		return new ArrayList<>(profiles.values());
			}
		}
	};

    @Reference
    private OgemaGuiService widgetService;
    
    protected void addSource(DataProvider<?> source) {
    	sources.put(source.id(), source);
    	sourcesRevision.incrementAndGet();
    }
    
    protected void removeSource(DataProvider<?> source) {
    	sources.remove(source.id());
    	sourcesRevision.incrementAndGet();
    }
    
    protected void addProfile(ProfileCategory source) {
    	profiles.put(source.id(), source);
    	profilesRevision.incrementAndGet();
    }
    
    protected void removeProfile(ProfileCategory source) {
    	profiles.remove(source.id());
    	profilesRevision.incrementAndGet();
    }
    
	@Override
	public void start(ApplicationManager appManager) {
		this.wapp = widgetService.createWidgetApp("/de/iwes/tools/eval/schedule/viewer", appManager);
		final WidgetPage<?> page = wapp.createStartPage();
		new ScheduleViewerPage(page, sourceProviders, profileProviders, appManager);
		logger.info("{} started",getClass().getName());
		
		// menu
//		final NavigationMenu menu = new NavigationMenu(" Browse pages");
//		menu.addEntry("Main page", page);
//		page.getMenuConfiguration().setCustomNavigation(menu);
	}

    @Override
	public void stop(AppStopReason reason) {
    	if (wapp != null)
    		wapp.close();
    	wapp = null;
		logger.info("{} closing down",getClass().getName());
	}

}