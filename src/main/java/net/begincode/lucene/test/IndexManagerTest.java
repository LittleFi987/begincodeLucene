package net.begincode.lucene.test;

import java.io.StringReader;
import java.util.HashSet;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import net.begincode.lucene.analyzer.MyIkAnalyzer;
import net.begincode.lucene.util.ConfigBean;
import net.begincode.lucene.util.IndexConfig;
import net.begincode.lucene.util.IndexManager;

public class IndexManagerTest {

	public static void main(String[] args) throws Exception {
		HashSet<ConfigBean> set = new HashSet<ConfigBean>();
		/*
		 * for (int i = 0; i < 4; i++) { ConfigBean bean = new ConfigBean();
		 * bean.setIndexPath("d:/begincodeIndex"); bean.setIndexName("test" +
		 * i); set.add(bean); }
		 */

		/*
		 * String indexName = "test1"; Index index = new Index(indexName);
		 * Document doc = new Document(); doc.add(new
		 * StringField("id","1",Store.YES)); doc.add(new Field("fieldname",
		 * "Ϊʲôssm����ʧ��", TextField.TYPE_STORED)); index.addDocument(doc);
		 */
		ConfigBean bean = new ConfigBean();
		bean.setIndexPath("d:/begincodeIndex");
		bean.setIndexName("test");
		IndexConfig.setConfig(set);
		set.add(bean);
		Index index = new Index("test");
		Search search = new Search("test");

		// QueryParser parser = new QueryParser("title",new MyIkAnalyzer());
		QueryParser parser = new MultiFieldQueryParser(new String[] { "title", "content" }, new MyIkAnalyzer());
		Query query = parser.parse("mysql");
		/*
		 * SearchResultBean sbean = search.search(query, 0, 10);
		 * System.out.println(sbean.getSumCount()); for(Document document :
		 * sbean.getDocs()){ System.out.println(document.get("id"));
		 * System.out.println(document.get("title")); }
		 */
		// SimpleHTMLFormatter simpleHTMLFormatter = new
		// SimpleHTMLFormatter("<font color='red'>","</font>");
		// QueryParser scorer = new QueryParser("mysql",new MyIkAnalyzer());
		// Highlighter highlighter=new Highlighter(simpleHTMLFormatter, (Scorer)
		// scorer);
		IndexManager manager = IndexManager.getIndexManager("test");
		IndexSearcher searcher = manager.getIndexSearcher();
		TopDocs hits = searcher.search(query, 100);
		QueryScorer scorer = new QueryScorer(query);// ��ѯ�÷�
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);// �õ��÷ֵ�Ƭ�Σ����ǵõ�һ�ΰ�������ѯ�Ĺؼ��ֵ�ժҪ
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");// �Բ�ѯ�����ݸ�ʽ�����޲ι�������Ĭ���ǽ��ؼ��ּӴ�
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);// ���ݵ÷ֺ͸�ʽ��
		highlighter.setTextFragmenter(fragmenter);// ���óɸ���
		

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
//			System.out.println(doc.get("title"));
//			System.out.println(doc.get("content"));
			String desc = doc.get("content");
			if (desc != null) {
				TokenStream tokenStream = bean.getAnalyzer().tokenStream("content", new StringReader(desc));// TokenStream����ѯ�����ĸ��Ƭ�Σ��õ�������������
				System.out.println(highlighter.getBestFragment(tokenStream, desc));// ��Ȩ�ظߵ�ժҪ��ʾ�������õ����ǹؼ�������
			}
		}

	}

}
