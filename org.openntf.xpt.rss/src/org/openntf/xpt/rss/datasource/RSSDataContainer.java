/*
 * � Copyright WebGate Consulting AG, 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.openntf.xpt.rss.datasource;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.openntf.xpt.rss.model.FeedReaderService;
import org.openntf.xpt.rss.model.RSSEntry;

import com.ibm.xsp.model.AbstractDataContainer;

public class RSSDataContainer extends AbstractDataContainer {

	private List<FeedEntryData> m_FeedEntries;
	private String m_FeedURL;
	private RSSDataModel m_DataModel;

	public RSSDataContainer() {

	}

	public RSSDataContainer(String strBeanID, String strUniqueID, String feedURL) {
		super(strBeanID, strUniqueID);
		setFeedURL(feedURL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ObjectInput in) throws IOException {
		this.setFeedURL(readUTF(in));
		try {
			m_FeedEntries = (List<FeedEntryData>) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(ObjectOutput out) throws IOException {
		writeUTF(out, m_FeedURL);
		out.writeObject(m_FeedEntries);

	}

	public String getFeedURL() {
		return m_FeedURL;
	}

	public void setFeedURL(String feedURL) {
		m_FeedURL = feedURL;
	}

	public List<?> getRSSasList() {
		if (m_FeedEntries == null) {
			m_FeedEntries = buildEntries();
		}
		return m_FeedEntries;
	}

	private ArrayList<FeedEntryData> buildEntries() {
		ArrayList<FeedEntryData> lstRC = new ArrayList<FeedEntryData>();
		try {
			List<RSSEntry> lstEntries = FeedReaderService.getInstance()
					.getAllEntriesFromURL(m_FeedURL);
			for (RSSEntry rssEntry : lstEntries) {
				FeedEntryData fed = new FeedEntryData();
				fed.setColumnValue("title", rssEntry.getTitle());
				fed.setColumnValue("link", rssEntry.getLink());
				fed.setColumnValue("description", rssEntry.getDescription());
				fed.setColumnValue("author", rssEntry.getAuthors() == null
						|| rssEntry.getAuthors().size() == 0 ? "" : rssEntry
						.getAuthors().get(0));
				fed.setColumnValue("authors", rssEntry.getAuthorsTXT());
				fed.setColumnValue("categories", rssEntry.getCategoriesTXT());
				fed.setColumnValue("contents", rssEntry.getContentsTXT());
				fed.setColumnValue("links", rssEntry.getLinksTXT());
				fed.setColumnValue("date", rssEntry.getCreated());
				fed.setColumnValue("update", rssEntry.getUpdated());
				lstRC.add(fed);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lstRC;

	}

	public Object getData() {
		if (m_DataModel == null) {
			m_DataModel = new RSSDataModel(this);
		}
		return m_DataModel;
	}
}
