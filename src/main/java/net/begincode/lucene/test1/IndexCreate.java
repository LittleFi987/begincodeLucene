package net.begincode.lucene.test1;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import net.begincode.lucene.analyzer.MyIkAnalyzer;
import net.begincode.lucene.test.Index;
import net.begincode.lucene.util.ConfigBean;
import net.begincode.lucene.util.IndexConfig;

public class IndexCreate {
	private static final String driverClassName="com.mysql.jdbc.Driver";
    private static final String url="jdbc:mysql://127.0.0.1:3306/begincode?characterEncoding=utf-8";
    private static final String username="root";
    private static final String password="root";

    private Directory directory = null;
    private DirectoryReader ireader = null;
    private IndexWriter iwriter = null;
    private MyIkAnalyzer analyzer;

    private Connection conn;

    public IndexCreate() {
//        directory = new RAMDirectory();
    	try {
			directory = FSDirectory.open(Paths.get("E:\\begincodeDB"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public IndexSearcher getSearcher(){
        try {
            if(ireader==null) {
                ireader = DirectoryReader.open(directory);
            } else {
                DirectoryReader tr = DirectoryReader.openIfChanged(ireader) ;
                if(tr!=null) {
                    ireader.close();
                    ireader = tr;
                }
            }
            return new IndexSearcher(ireader);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection(){
        if(this.conn == null){
            try {
                Class.forName(driverClassName);
                conn = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return conn;
    }

    private MyIkAnalyzer getAnalyzer(){
        if(analyzer == null){
            return new MyIkAnalyzer();
        }else{
            return analyzer;
        }
    }

    public void createIndex(){
        Connection conn = getConnection();
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        if(conn == null){
            System.out.println("get the connection error...");
            return ;
        }
        String sql = "SELECT p.problem_id AS id,p.title AS title,p.content AS content FROM problem p";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            IndexWriterConfig iwConfig =  new IndexWriterConfig(getAnalyzer());
            iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            iwriter = new IndexWriter(directory,iwConfig);

            while(rs.next()){
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Document doc = new Document();
                doc.add(new TextField("uid", id+"",Field.Store.YES));
                doc.add(new TextField("uname", title+"",Field.Store.YES));
                doc.add(new TextField("upsd", content+"",Field.Store.YES));
                iwriter.addDocument(doc);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                if(iwriter != null)
                iwriter.close();
                rs.close();
                pstmt.close();
                if(!conn.isClosed()){
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void searchByTerm(String field,String keyword,int num) throws InvalidTokenOffsetsException{
         IndexSearcher isearcher = getSearcher();
         Analyzer analyzer =  getAnalyzer();
        //使用QueryParser查询分析器构造Query对象
        QueryParser qp = new QueryParser(field,analyzer);
        //这句所起效果？
        qp.setDefaultOperator(QueryParser.OR_OPERATOR);
        try {
            Query query = qp.parse(keyword);
            ScoreDoc[] hits;

            //注意searcher的几个方法
            hits = isearcher.search(query, null, num).scoreDocs;

            System.out.println("the ids is =");
            for (int i = 0; i < hits.length; i++) {
                Document doc = isearcher.doc(hits[i].doc);
                System.out.print(doc.get("uname")+" ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InvalidTokenOffsetsException {
        IndexCreate ld = new IndexCreate();
        ld.createIndex();
        ld.searchByTerm("uname", "测试", 100);
    }
    
}
