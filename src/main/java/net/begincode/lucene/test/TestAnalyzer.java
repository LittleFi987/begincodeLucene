package net.begincode.lucene.test;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import net.begincode.lucene.analyzer.MyIkAnalyzer;
/**
 * 
 * @author Stay
 *
 */
public class TestAnalyzer {

	private static void testAnalyzer(Analyzer analyzer,String text) throws Exception{
		System.out.println("当前使用的分词器:"+analyzer.getClass());
		TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
		CharTermAttribute cta = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();  //必须先调用reset方法
		while(tokenStream.incrementToken()){
			System.out.println(cta);
		}
	}
	public static void main(String[] args) throws Exception{
		Analyzer analyzer = new MyIkAnalyzer();
		testAnalyzer(analyzer, "begincode社区,java初学者");
	}
	
}
