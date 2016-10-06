package net.begincode.lucene.model;

import java.util.List;

import org.apache.lucene.document.Document;

public class SearchResultBean {
	private int sumCount;
	private List<Document> docs; //查询的结构数
	public int getSumCount() {
		return sumCount;
	}
	public void setSumCount(int sumCount) {
		this.sumCount = sumCount;
	}
	public List<Document> getDocs() {
		return docs;
	}
	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}
	
}
