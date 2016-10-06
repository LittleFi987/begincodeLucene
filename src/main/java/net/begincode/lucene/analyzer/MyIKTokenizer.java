package net.begincode.lucene.analyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class MyIKTokenizer extends Tokenizer {

	 // IK�ִ���ʵ��
    private IKSegmenter _IKImplement;

    // ��Ԫ�ı�����
    private final CharTermAttribute termAtt;
    // ��Ԫλ������
    private final OffsetAttribute offsetAtt;
    // ��Ԫ�������ԣ������Է���ο�org.wltea.analyzer.core.Lexeme�еķ��ೣ����
    private final TypeAttribute typeAtt;
    // ��¼���һ����Ԫ�Ľ���λ��
    private int endPosition;

    public MyIKTokenizer(Reader in) {
        this(in, false);
    }

    public MyIKTokenizer(Reader in, boolean useSmart) {
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        _IKImplement = new IKSegmenter(input, useSmart);
    }

    @Override
    public boolean incrementToken() throws IOException {
        // ������еĴ�Ԫ����
        clearAttributes();
        Lexeme nextLexeme = _IKImplement.next();
        if (nextLexeme != null) {
            // ��Lexemeת��Attributes
            // ���ô�Ԫ�ı�
            termAtt.append(nextLexeme.getLexemeText());
            // ���ô�Ԫ����
            termAtt.setLength(nextLexeme.getLength());
            // ���ô�Ԫλ��
            offsetAtt.setOffset(nextLexeme.getBeginPosition(),
                    nextLexeme.getEndPosition());
            // ��¼�ִʵ����λ��
            endPosition = nextLexeme.getEndPosition();
            // ��¼��Ԫ����
            typeAtt.setType(nextLexeme.getLexemeTypeString());
            // ����true��֪�����¸���Ԫ
            return true;
        }
        // ����false��֪��Ԫ������
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        _IKImplement.reset(input);
    }

    @Override
    public final void end() {
        // set final offset
        int finalOffset = correctOffset(this.endPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }
	

}
