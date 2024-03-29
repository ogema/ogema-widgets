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
/**
 * Copyright 2009 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IEE
 *
 * All Rights reserved
 */
package org.ogema.widgets.pushover.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.StringResource;
import org.ogema.widgets.pushover.model.PushoverConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(specVersion = "1.2")
@Service(Application.class)
public class PushoverService implements Application, MessageListener {

	private final static String TARGET_URI = "https://api.pushover.net/1/messages.json";
	private volatile BundleContext ctx;
	private volatile ServiceRegistration<MessageListener> sreg;
	private volatile PushoverConfiguration config;
	private volatile Logger logger;
	private WidgetApp wapp;
	
	@Reference
	private OgemaGuiService widgetService;
	
	@Activate
	protected void start(BundleContext ctx) {
		this.ctx = ctx;
	}
	
	@Deactivate
	protected void stop() {
		this.ctx = null;
	}
	
	@Override
	public void start(ApplicationManager appManager) {
		this.sreg = ctx.registerService(MessageListener.class, this, null);
		this.config = appManager.getResourceManagement().createResource("pushoverConfiguration", PushoverConfiguration.class);
		config.userTokens().create();
		config.applicationTokens().create();
		this.logger = appManager.getLogger();
		this.wapp = widgetService.createWidgetApp("/de/iwes/messaging/pushover/config", appManager);
		new ConfigPage(wapp.createStartPage(), config);
	}

	@Override
	public void stop(AppStopReason reason) {
		final ServiceRegistration<MessageListener> sreg = this.sreg;
		if (sreg != null)
			sreg.unregister();
		if (wapp != null)
			wapp.close();
		this.wapp = null;
		this.sreg = null;
		this.config = null;
		this.logger = null;
	}
	
	@Override
	public String getId() {
		return "Pushover";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "Forward messages through the Pushover app.";
	}

	@Override
	public void newMessageAvailable(final ReceivedMessage message, final List<String> recipients) {
		logger.trace("New message from {}, to {}",message.getAppName(),recipients);
		final List<String> receivers = new ArrayList<>(recipients);
		receivers.retainAll(getKnownUsers());
		if (receivers.isEmpty()) {
			logger.info("No recipient found; original list: " + recipients);
			return;
		}
		final List<StringResource> senders = config.userTokens().getAllElements();
		String sender = null;
		for (StringResource s: senders) {
			if (s.isActive()) {
				String str = s.getValue().trim();
				if (!str.isEmpty()) {
					sender = str;
					break;
				}
			}
		}
		if (sender == null) {
			logger.warn("No sender found, could not forward message");
			return;
		}
		final URI uri;
		try {
			uri = new URI(TARGET_URI);
		} catch (URISyntaxException e) {
			logger.error("Unexpected exception:",e);
			return;
		}
		final List<NameValuePair> bodyEntries = new ArrayList<>();
		bodyEntries.add(new BasicNameValuePair("token", receivers.iterator().next()));
		bodyEntries.add(new BasicNameValuePair("user", sender));
		bodyEntries.add(new BasicNameValuePair("message", message.getOriginalMessage().message(OgemaLocale.ENGLISH)));
		String title = message.getOriginalMessage().title(OgemaLocale.ENGLISH);
		if (title != null) {
			title= title.trim();
			if (!title.isEmpty()) {
				bodyEntries.add(new BasicNameValuePair("title", title));
			}
		}
		final HttpResponse response;
		try {
			response = AccessController.doPrivileged(new PrivilegedExceptionAction<HttpResponse>() {

				@Override
				public HttpResponse run() throws Exception {
					return Request.Post(uri).body(new UrlEncodedFormEntity(bodyEntries, "UTF-8")).execute().returnResponse();
				}
			});
		} catch (PrivilegedActionException | RuntimeException e) {
			final Exception cause = e instanceof RuntimeException ? e : (Exception) e.getCause();
			logger.warn("Error sending message",cause);
			return;
		}
		final StatusLine status = response.getStatusLine();
		final int code = status.getStatusCode();
		if (code >= 400)
			logger.warn("Message sending failed. Response: {}", status);
		else
			logger.debug("Message sent successfully. Response: {}", code);
	}

	@Override
	public List<String> getKnownUsers() {
		final PushoverConfiguration config = this.config;
		if (config == null)
			return Collections.emptyList();
		final List<String> users = new ArrayList<>();
		for (StringResource app : config.applicationTokens().getAllElements()) {
			if (app.isActive()) 
				users.add(app.getValue());
		}
		return users;
	}

}
