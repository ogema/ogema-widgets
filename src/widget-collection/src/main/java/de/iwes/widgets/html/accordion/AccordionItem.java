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
package de.iwes.widgets.html.accordion;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.LabelledItem;

/**
 *
 * @author pzuehlcke
 */
public class AccordionItem {

    private String id;
    private String label;
    private String html;
    // null in legacy apps
    private final LabelledItem labelledItem;
    private boolean expanded;
    private final static AtomicInteger idCount = new AtomicInteger(0);
    private final int intId;
    private final ItemType type;
    private OgemaWidgetBase<?> widget;

    AccordionItem(String title, String html, boolean expanded, ItemType type) {
    	if (type.equals(ItemType.WIDGET)) 
    		throw new IllegalArgumentException("Widgets may only be added to an Accordion via the dedicated constructor provided");
        this.intId = idCount.getAndIncrement();
        this.id = title;
        this.label = StringEscapeUtils.escapeHtml4(title);
        this.html = html;
        this.expanded = expanded;
        this.type = type;
        this.labelledItem = null;
    }
    
    AccordionItem(String title, OgemaWidgetBase<?> widget, boolean expanded) {
    	this.widget = widget;
        this.intId = idCount.getAndIncrement();
        this.id = title;
        this.label = StringEscapeUtils.escapeHtml4(title);
        this.html = widget.getTag();
        this.expanded = expanded;
        this.type = ItemType.WIDGET;
        this.labelledItem = null;
    }
    
    AccordionItem(LabelledItem item, String html, boolean expanded, ItemType type) {
    	if (type.equals(ItemType.WIDGET)) 
    		throw new IllegalArgumentException("Widgets may only be added to an Accordion via the dedicated constructor provided");
        this.intId = idCount.getAndIncrement();
        this.labelledItem = Objects.requireNonNull(item);
        this.id = Objects.requireNonNull(labelledItem.id());
        this.label = null;
        this.html = html;
        this.expanded = expanded;
        this.type = type;
    }
    
    AccordionItem(LabelledItem item, OgemaWidgetBase<?> widget, boolean expanded) {
    	this.labelledItem = Objects.requireNonNull(item);
    	this.widget = Objects.requireNonNull(widget);
    	this.id = Objects.requireNonNull(item.id());
    	this.intId = idCount.getAndIncrement();
    	this.label = null;
    	this.expanded = expanded;
    	this.type = ItemType.WIDGET;
    	this.html = widget.getTag();
	}
    
    public String getId() {
    	return id;
    }
    
    public String getTitle(OgemaLocale locale) {
    	String label = null;
    	if (labelledItem != null)
    		label = labelledItem.label(locale);
    	if (label == null)
    		label = this.label != null ? this.label : id;
    	return label;
    }
    
    /**
     * Actually returns the id of the item
     * @return
     */
    public String getTitle() {
        return id;
    }
    
    private String getLabelEncoded(final OgemaLocale locale) {
    	if (labelledItem != null) {
    		try {
    			final String lab = labelledItem.label(locale);
    			if (lab != null)
    				return StringEscapeUtils.escapeHtml4(lab);
    		} catch (Exception ignore) {}
    	}
    	if (this.label != null)
    		return this.label; // already encoded!
    	return StringEscapeUtils.escapeHtml4(id);
    }
    
    private String getDescriptionEncoded(final OgemaLocale locale) {
    	if (labelledItem == null)
    		return null;
    	final String descr = labelledItem.description(locale);
    	return descr == null ? null : StringEscapeUtils.escapeHtml4(descr);
    }

    /**
     * @deprecated Set in constructor... id must be immutable
     * @param title
     */
    @Deprecated 
    public void setTitle(String title) {
    	throw new UnsupportedOperationException("Title/id is immutable");
//        this.id = title;
//        this.label = StringEscapeUtils.escapeHtml4(title);
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    JSONObject getJSON(final OgemaLocale locale) {
        JSONObject result = new JSONObject();
        result.put("title", getLabelEncoded(locale));
        result.put("data", html);
        result.put("expanded", expanded);
        result.put("type", type);
        result.put("id", intId);
        final String description = getDescriptionEncoded(locale);
        if (description != null)
        	result.put("tooltip", description);
        return result;
    }
    
    public ItemType getType() {
    	return type;
    }
    
    /**
     * Returns null, unless the item type is {@link ItemType#WIDGET}
     */
    public OgemaWidgetBase<?> getWidget() {
    	return widget;
    }

}
