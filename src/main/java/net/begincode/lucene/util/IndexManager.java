package net.begincode.lucene.util;

import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
  
public class IndexManager {
	private IndexWriter indexWriter;
	private TrackingIndexWriter trackingIndexWriter;
    private ControlledRealTimeReopenThread<IndexSearcher> crtReopenThread;
	private IndexCommitThread indexCommitThread;
	private ConfigBean configBean;
	private Analyzer analyzer;
    private SearcherManager searcherManager;
    
	private static class LazyIndexManager {
		//����ϵͳ�е�IndexManager����
		private static HashMap<String, IndexManager> indexManagerMap = new HashMap<String, IndexManager>();
		
		static {
			for (ConfigBean bean : IndexConfig.getConfig()) {
				indexManagerMap.put(bean.getIndexName(), new IndexManager(bean));
			}
		}
	}
	
	/**
	 * ��ȡ������IndexManager����
	 * @param indexName
	 * @return
	 */
	public static IndexManager getIndexManager(String indexName) {
		return LazyIndexManager.indexManagerMap.get(indexName);
	}
	
	/**
	* @param configBean
	 */
	private IndexManager(ConfigBean configBean) {
		//�����Ĵ洢·��
		String indexFile = configBean.getIndexPath()  + "/" + configBean.getIndexName();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(configBean.getAnalyzer());
		analyzer = configBean.getAnalyzer();
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.configBean = configBean;
		Directory directory = null;
		try {
			directory = NIOFSDirectory.open(Paths.get(indexFile));
			if (IndexWriter.isLocked(directory)) {
				directory.obtainLock(IndexWriter.WRITE_LOCK_NAME).close();;
			}
			this.indexWriter = new IndexWriter(directory, indexWriterConfig);
			//��indexWriterί�и�trackingIndexWriter
			this.trackingIndexWriter = new TrackingIndexWriter(indexWriter);
			//true ��ʾ���ڴ���ɾ����false����ɾ���ܲ�ɾ����Ϊfalse���ܻ����һЩ
            searcherManager = new SearcherManager(indexWriter,false,new SearcherFactory());
			//����NRTManager
			this.crtReopenThread = new ControlledRealTimeReopenThread<IndexSearcher>(trackingIndexWriter, searcherManager, configBean.getIndexReopenMaxStaleSec(), configBean.getIndexReopenMinStaleSec());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//����ϵͳ���ػ��߳�
		setThread();
	}
	
	/**
	 * ��ȡ���¿��õ�indexSearcher
	 * @return
	 */
	public IndexSearcher getIndexSearcher() {
		try {
			return this.searcherManager.acquire();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * �ͷ�indexSearcher
	 * @param indexSearcher
	 */
	public void relase(IndexSearcher indexSearcher) {
		try {
			this.searcherManager.release(indexSearcher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����indexSearcher���ػ��߳�
	 */
	private void setThread () {
		//�ڴ������ض��߳�
		this.crtReopenThread.setName("NRTManager reopen thread");
		this.crtReopenThread.setDaemon(true);
		this.crtReopenThread.start();
		
		//�ڴ������ύ�߳�
		this.indexCommitThread = new IndexCommitThread(configBean.getIndexName() + " index commmit thread");
		this.indexCommitThread.setDaemon(true);
		this.indexCommitThread.start();
	}
	
	private class IndexCommitThread extends Thread {
		private boolean flag = false;
		public IndexCommitThread (String name) {
			super(name);
		}
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			flag = true;
			while (flag){
				try {
					//�ڴ������ύ��Ӳ��
					indexWriter.commit();
					System.out.println(new Date().toLocaleString() + "\t" + configBean.getIndexName() + "\tcommit");
					TimeUnit.SECONDS.sleep(configBean.getIndexCommitSeconds());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.run();
		}
	}
	
	public void commit() {
		try {
			indexWriter.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public TrackingIndexWriter getTrackingIndexWriter() {
		return trackingIndexWriter;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}
}
