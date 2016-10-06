package net.begincode.lucene.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import net.begincode.lucene.model.SearchResultBean;
import net.begincode.lucene.util.IndexManager;

public class Search {
	private IndexManager indexManager;

	public Search(String indexName) {
		this.indexManager = IndexManager.getIndexManager(indexName);
	}
	
	/**
	 * @param query ��ѯ����
	 * @param start �ӵڼ��� ��0 ��ʼ���� ����start
	 * @param end  ���ڼ��� ������end
	 * @param sort �Զ�������ʽ
	 * @return
	 * @Description: ��ҳ��ѯ
	 */
	public SearchResultBean search(Query query, int start, int end, Sort sort) {
		start = start > 0 ? start : 0;
		end = end > 0 ? end : 0;
		if (query == null || start > end || indexManager == null) {
			return null;
		}
		SearchResultBean bean = new SearchResultBean();
		List<Document> docs = new ArrayList<Document>();
		bean.setDocs(docs);
		IndexSearcher searcher = indexManager.getIndexSearcher();
		
		try {
			TopDocs topDocs = searcher.search(query, end, sort);
			bean.setSumCount(topDocs.totalHits);
			end = end > topDocs.totalHits ? topDocs.totalHits : end;
			for (int i = start; i < end; i++) {
				docs.add(searcher.doc(topDocs.scoreDocs[i].doc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexManager.relase(searcher);
		}
		
		return bean;
	}
	
	/**
	 * @param query ��ѯ����
	 * @param start �ӵڼ��� ��0 ��ʼ���� ����start
	 * @param end  ���ڼ��� ������end
	 * @return
	 * @Description: ��ҳ��ѯ���������Ĭ�ϵ�����ʽ
	 */
	public SearchResultBean search(Query query, int start, int end) {
		start = start > 0 ? start : 0;
		end = end > 0 ? end : 0;
		if (query == null || start > end || indexManager == null) {
			return null;
		}
		SearchResultBean bean = new SearchResultBean();
		List<Document> docs = new ArrayList<Document>();
		bean.setDocs(docs);
		IndexSearcher searcher = indexManager.getIndexSearcher();
		
		try {
			TopDocs topDocs = searcher.search(query, end);
			bean.setSumCount(topDocs.totalHits);
			end = end > topDocs.totalHits ? topDocs.totalHits : end;
			for (int i = start; i < end; i++) {
				docs.add(searcher.doc(topDocs.scoreDocs[i].doc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexManager.relase(searcher);
		}
		
		return bean;
	}
}
