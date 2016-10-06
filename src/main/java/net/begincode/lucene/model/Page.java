package net.begincode.lucene.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ����Article�ķ�ҳ��
 * @author Stay
 */
public class Page {
	private Integer currPageNO;//��ǰҳ��OK
	private Integer perPageSize = 5;//ÿҳ��ʾ��¼����Ĭ��Ϊ2��OK
	private Integer allRecordNO;//�ܼ�¼��OK
	private Integer allPageNO;//��ҳ��OK
	private List<Article> articleList = new ArrayList<Article>();//����OK
	public Page(){}
	public Integer getCurrPageNO() {
		return currPageNO;
	}
	public void setCurrPageNO(Integer currPageNO) {
		this.currPageNO = currPageNO;
	}
	public Integer getPerPageSize() {
		return perPageSize;
	}
	public void setPerPageSize(Integer perPageSize) {
		this.perPageSize = perPageSize;
	}
	public Integer getAllRecordNO() {
		return allRecordNO;
	}
	public void setAllRecordNO(Integer allRecordNO) {
		this.allRecordNO = allRecordNO;
	}
	public Integer getAllPageNO() {
		return allPageNO;
	}
	public void setAllPageNO(Integer allPageNO) {
		this.allPageNO = allPageNO;
	}
	public List<Article> getArticleList() {
		return articleList;
	}
	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}
}
