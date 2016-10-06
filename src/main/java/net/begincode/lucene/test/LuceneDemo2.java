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
		//指定分词技术，这里使用的是标准分词
				Analyzer analyzer = new StandardAnalyzer();

				// 将索引存储到内存中
//				Directory directory = new RAMDirectory();
				//想实现把索引库存到硬盘中请使用此方法    Paths 是nio包下的
				Directory directory = FSDirectory.open(Paths.get("d:/123"));

				//指定了分词器
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				
				//实现索引的 CRUD
				IndexWriter indexWriter = new IndexWriter(directory, config);

				String[] texts = new String[] { "Elasticsearch安装",
						"Elasticsearch配置详解",
						"Elasticsearch索引操作",
						"Elasticsearch mapping操作",
						"Elasticsearch数据操作",
						"elasticsearch-head安装",
						"Elasticsearch安装ik中文分词",
						"安装拼音插件",
						"JAVA操作ES",
						"TermQuery 详解",
						"Compound Query详解",
						"Elasticsearch FullText Query"};
				for (String text : texts) {
					Document doc = new Document();
					//使用指定分词策略
					//StringField域，当成一个整体，不会被分词
					//TextField域，采用指定的分词技术
					doc.add(new Field("fieldname", text, TextField.TYPE_STORED));  //可以理解为索引表添加一个fieldname字段 把text的内容存入索引表
					//将文档写入索引中
					indexWriter.addDocument(doc);
				}
				indexWriter.forceMerge(1);  //如果资源再次写入到索引库 不会再创建新的文件，会默认合并成一个文件  如果不设置 默认为十个文件时合并
				indexWriter.commit();
				indexWriter.close();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		//指定分词技术，这里使用的是标准分词
		Analyzer analyzer = new StandardAnalyzer();

		// 将索引存储到内存中
		Directory directory = new RAMDirectory();
		//想实现把索引库存到硬盘中请使用此方法    Paths 是nio包下的
//		Directory directory = FSDirectory.open(Paths.get("d:/123"));

		//指定了分词器
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		
		//索引的打开方式:没有就创建，有就打开
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		//实现索引的 CRUD
		IndexWriter indexWriter = new IndexWriter(directory, config);

		String[] texts = new String[] { "Elasticsearch安装",
				"Elasticsearch配置详解",
				"Elasticsearch索引操作",
				"Elasticsearch mapping操作",
				"Elasticsearch数据操作",
				"elasticsearch-head安装",
				"Elasticsearch安装ik中文分词",
				"安装拼音插件",
				"JAVA操作ES",
				"TermQuery 详解",
				"Compound Query详解",
				"Elasticsearch FullText Query"};
		for (String text : texts) {
			Document doc = new Document();
			//使用指定分词策略
			//StringField域，当成一个整体，不会被分词
			//TextField域，采用指定的分词技术
			doc.add(new Field("fieldname", text, TextField.TYPE_STORED));  //可以理解为索引表添加一个fieldname字段 把text的内容存入索引表
			//将文档写入索引中
			indexWriter.addDocument(doc);
		}
//		indexWriter.commit();
		indexWriter.close();
//		indexWriter.forceMerge(3);  //如果资源再次写入到索引库 不会再创建新的文件，会默认合并成一个文件  如果不设置 默认为十个文件时合并
		//读取索引
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    //创建索引检索对象  统一利用IndexSearcher来进行
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    
	    //创建Query
	    QueryParser parser = new QueryParser("fieldname", analyzer);
	    Query query = parser.parse("Elasticsearch");
	    //检索索引,获取符合条件的前10条记录 query可以理解为sql里面的where条件
	    TopDocs topDoc = isearcher.search(query, 10);
	    System.out.println("总共匹配多少个:"+topDoc.totalHits);
	    ScoreDoc[] hits = topDoc.scoreDocs;
	    for(ScoreDoc scoreDoc : hits){
	    	System.out.println("匹配得分:"+scoreDoc.score);
	    	System.out.println("文档索引ID:"+scoreDoc.doc);
	    	Document document = isearcher.doc(scoreDoc.doc);
	    	System.out.println(document.get("fieldname"));
	    }
	    ireader.close();
	    directory.close();
	}

}
