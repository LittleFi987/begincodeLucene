package net.begincode.lucene.test;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
/**
 * 
 * @author Stay
 *
 */
public class LuceneDemo2 {
	
	@Test
	public void createDB() throws Exception{
		//ָ���ִʼ���������ʹ�õ��Ǳ�׼�ִ�
				Analyzer analyzer = new StandardAnalyzer();

				// �������洢���ڴ���
//				Directory directory = new RAMDirectory();
				//��ʵ�ְ�������浽Ӳ������ʹ�ô˷���    Paths ��nio���µ�
				Directory directory = FSDirectory.open(Paths.get("d:/123"));

				//ָ���˷ִ���
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				
				//ʵ�������� CRUD
				IndexWriter indexWriter = new IndexWriter(directory, config);

				String[] texts = new String[] { "Elasticsearch��װ",
						"Elasticsearch�������",
						"Elasticsearch��������",
						"Elasticsearch mapping����",
						"Elasticsearch���ݲ���",
						"elasticsearch-head��װ",
						"Elasticsearch��װik���ķִ�",
						"��װƴ�����",
						"JAVA����ES",
						"TermQuery ���",
						"Compound Query���",
						"Elasticsearch FullText Query"};
				for (String text : texts) {
					Document doc = new Document();
					//ʹ��ָ���ִʲ���
					//StringField�򣬵���һ�����壬���ᱻ�ִ�
					//TextField�򣬲���ָ���ķִʼ���
					doc.add(new Field("fieldname", text, TextField.TYPE_STORED));  //�������Ϊ���������һ��fieldname�ֶ� ��text�����ݴ���������
					//���ĵ�д��������
					indexWriter.addDocument(doc);
				}
				indexWriter.forceMerge(1);  //�����Դ�ٴ�д�뵽������ �����ٴ����µ��ļ�����Ĭ�Ϻϲ���һ���ļ�  ��������� Ĭ��Ϊʮ���ļ�ʱ�ϲ�
				indexWriter.commit();
				indexWriter.close();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		//ָ���ִʼ���������ʹ�õ��Ǳ�׼�ִ�
		Analyzer analyzer = new StandardAnalyzer();

		// �������洢���ڴ���
		Directory directory = new RAMDirectory();
		//��ʵ�ְ�������浽Ӳ������ʹ�ô˷���    Paths ��nio���µ�
//		Directory directory = FSDirectory.open(Paths.get("d:/123"));

		//ָ���˷ִ���
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		
		//�����Ĵ򿪷�ʽ:û�оʹ������оʹ�
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		//ʵ�������� CRUD
		IndexWriter indexWriter = new IndexWriter(directory, config);

		String[] texts = new String[] { "Elasticsearch��װ",
				"Elasticsearch�������",
				"Elasticsearch��������",
				"Elasticsearch mapping����",
				"Elasticsearch���ݲ���",
				"elasticsearch-head��װ",
				"Elasticsearch��װik���ķִ�",
				"��װƴ�����",
				"JAVA����ES",
				"TermQuery ���",
				"Compound Query���",
				"Elasticsearch FullText Query"};
		for (String text : texts) {
			Document doc = new Document();
			//ʹ��ָ���ִʲ���
			//StringField�򣬵���һ�����壬���ᱻ�ִ�
			//TextField�򣬲���ָ���ķִʼ���
			doc.add(new Field("fieldname", text, TextField.TYPE_STORED));  //�������Ϊ���������һ��fieldname�ֶ� ��text�����ݴ���������
			//���ĵ�д��������
			indexWriter.addDocument(doc);
		}
//		indexWriter.commit();
		indexWriter.close();
//		indexWriter.forceMerge(3);  //�����Դ�ٴ�д�뵽������ �����ٴ����µ��ļ�����Ĭ�Ϻϲ���һ���ļ�  ��������� Ĭ��Ϊʮ���ļ�ʱ�ϲ�
		//��ȡ����
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    //����������������  ͳһ����IndexSearcher������
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    
	    //����Query
	    QueryParser parser = new QueryParser("fieldname", analyzer);
	    Query query = parser.parse("Elasticsearch");
	    //��������,��ȡ����������ǰ10����¼ query�������Ϊsql�����where����
	    TopDocs topDoc = isearcher.search(query, 10);
	    System.out.println("�ܹ�ƥ����ٸ�:"+topDoc.totalHits);
	    ScoreDoc[] hits = topDoc.scoreDocs;
	    for(ScoreDoc scoreDoc : hits){
	    	System.out.println("ƥ��÷�:"+scoreDoc.score);
	    	System.out.println("�ĵ�����ID:"+scoreDoc.doc);
	    	Document document = isearcher.doc(scoreDoc.doc);
	    	System.out.println(document.get("fieldname"));
	    }
	    ireader.close();
	    directory.close();
	}

}
