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
		 * "为什么ssm整合失败", TextField.TYPE_STORED)); index.addDocument(doc);
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
		QueryScorer scorer = new QueryScorer(query);// 查询得分
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);// 得到得分的片段，就是得到一段包含所查询的关键字的摘要
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");// 对查询的数据格式化；无参构造器的默认是将关键字加粗
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);// 根据得分和格式化
		highlighter.setTextFragmenter(fragmenter);// 设置成高亮
		

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
//			System.out.println(doc.get("title"));
//			System.out.println(doc.get("content"));
			String desc = doc.get("content");
			if (desc != null) {
				TokenStream tokenStream = bean.getAnalyzer().tokenStream("content", new StringReader(desc));// TokenStream将查询出来的搞成片段，得到的是整个内容
				System.out.println(highlighter.getBestFragment(tokenStream, desc));// 将权重高的摘要显示出来，得到的是关键字内容
			}
		}

	}

}
