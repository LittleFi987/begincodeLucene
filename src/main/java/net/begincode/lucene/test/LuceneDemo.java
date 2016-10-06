package net.begincode.lucene.test;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class LuceneDemo {

	@Test
	public void luceneTest() throws Exception{
		Analyzer analyzer = new StandardAnalyzer();

	    // �洢�������ڴ���:
//	    Directory directory = new RAMDirectory();
	    // �洢������Ӳ���� Ӧ��ʹ�õķ���:
	    Directory directory = FSDirectory.open(Paths.get("d:/begincodeIndex/test1"));
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    IndexWriter iwriter = new IndexWriter(directory, config);
	    Document doc = new Document();
	    String text = "This is the text to be indexed.";
	    doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
	    iwriter.addDocument(doc);
	    iwriter.close();
	    
	    // ��ȡ�������е�����
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    // һ���򵥵Ĳ�ѯ
	    QueryParser parser = new QueryParser("fieldname", analyzer);
	    Query query = parser.parse("text");
	    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
	    assertEquals(1, hits.length);
	    // ����ƥ��Ľ��
	    for (int i = 0; i < hits.length; i++) {
	      Document hitDoc = isearcher.doc(hits[i].doc);
	      System.out.println(hitDoc.get("fieldname"));
	    }
	    ireader.close();
	    directory.close();
	}
}
