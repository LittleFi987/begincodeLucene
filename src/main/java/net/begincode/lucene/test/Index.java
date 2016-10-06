package net.begincode.lucene.test;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.Query;

import net.begincode.lucene.util.IndexManager;

public class Index {

	private TrackingIndexWriter trackingIndexWriter;
	private String indexName;
	
	public Index(String indexName) {
		this.indexName = indexName;
		this.trackingIndexWriter = IndexManager.getIndexManager(indexName).getTrackingIndexWriter();
	}
	
	/**
	 * @param doc
	 * @return
	 * @Author:lulei  
	 * @Description: 向索引添加一条记录
	 */
	public boolean addDocument(Document doc) {
		try {
			trackingIndexWriter.addDocument(doc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param query
	 * @return
	 * @Author:lulei  
	 * @Description: 删除符合条件的记录
	 */
	public boolean deleteDocument(Query query) {
		try {
			trackingIndexWriter.deleteDocuments(query);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 清空索引中的记录
	 */
	public boolean deleteAll() {
		try {
			trackingIndexWriter.deleteAll();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @param term
	 * @param doc
	 * @return
	 * @Author:lulei  
	 * @Description: 更新索引中的记录
	 */
	public boolean updateDocument(Term term, Document doc) {
		try {
			trackingIndexWriter.updateDocument(term, doc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @Author:lulei  
	 * @Description: 将内存中的内容提交到磁盘
	 */
	public void commit() {
		IndexManager.getIndexManager(indexName).commit();
	}
}
