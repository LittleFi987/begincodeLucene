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
		// ��ȡ����
		DirectoryReader ireader = DirectoryReader.open(directory);
		// ���������������� ͳһ����IndexSearcher������
		IndexSearcher searcher = new IndexSearcher(ireader);
		//����Query
	    QueryParser parser = new QueryParser("title", ik);
	    Query query = parser.parse("mysql");
		TopDocs hits = searcher.search(query, 100);
		QueryScorer scorer = new QueryScorer(query);// ��ѯ�÷�
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);// �õ��÷ֵ�Ƭ�Σ����ǵõ�һ�ΰ�������ѯ�Ĺؼ��ֵ�ժҪ
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");// �Բ�ѯ�����ݸ�ʽ�����޲ι�������Ĭ���ǽ��ؼ��ּӴ�
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);// ���ݵ÷ֺ͸�ʽ��
		highlighter.setTextFragmenter(fragmenter);// ���óɸ���
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			String desc = doc.get("content");
			if (desc != null) {
				TokenStream tokenStream = ik.tokenStream("content", new StringReader(desc));// TokenStream����ѯ�����ĸ��Ƭ�Σ��õ�������������
				System.out.println(highlighter.getBestFragment(tokenStream, desc));// ��Ȩ�ظߵ�ժҪ��ʾ�������õ����ǹؼ�������
			}
		}
	}
}
