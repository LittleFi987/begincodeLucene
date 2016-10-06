package net.begincode.lucene.test;

import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import net.begincode.lucene.analyzer.MyIkAnalyzer;

public class LuceneDemo3 {

	public static void main(String[] args) throws Exception {
		MyIkAnalyzer ik = new MyIkAnalyzer();
		Directory directory = FSDirectory.open(Paths.get("d:/begincodeIndex/test"));
		IndexWriterConfig config = new IndexWriterConfig(ik);
		IndexWriter iwriter = new IndexWriter(directory, config);
		// 读取索引
		DirectoryReader ireader = DirectoryReader.open(directory);
		// 创建索引检索对象 统一利用IndexSearcher来进行
		IndexSearcher searcher = new IndexSearcher(ireader);
		//创建Query
	    QueryParser parser = new QueryParser("title", ik);
	    Query query = parser.parse("mysql");
		TopDocs hits = searcher.search(query, 100);
		QueryScorer scorer = new QueryScorer(query);// 查询得分
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);// 得到得分的片段，就是得到一段包含所查询的关键字的摘要
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");// 对查询的数据格式化；无参构造器的默认是将关键字加粗
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);// 根据得分和格式化
		highlighter.setTextFragmenter(fragmenter);// 设置成高亮
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			String desc = doc.get("content");
			if (desc != null) {
				TokenStream tokenStream = ik.tokenStream("content", new StringReader(desc));// TokenStream将查询出来的搞成片段，得到的是整个内容
				System.out.println(highlighter.getBestFragment(tokenStream, desc));// 将权重高的摘要显示出来，得到的是关键字内容
			}
		}
	}
}
