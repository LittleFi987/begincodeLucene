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
		//保存系统中的IndexManager对象
		private static HashMap<String, IndexManager> indexManagerMap = new HashMap<String, IndexManager>();
		
		static {
			for (ConfigBean bean : IndexConfig.getConfig()) {
				indexManagerMap.put(bean.getIndexName(), new IndexManager(bean));
			}
		}
	}
	
	/**
	 * 获取索引的IndexManager对象
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
		//索引的存储路径
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
			//将indexWriter委托给trackingIndexWriter
			this.trackingIndexWriter = new TrackingIndexWriter(indexWriter);
			//true 表示在内存中删除，false可能删可能不删，设为false性能会更好一些
            searcherManager = new SearcherManager(indexWriter,false,new SearcherFactory());
			//创建NRTManager
			this.crtReopenThread = new ControlledRealTimeReopenThread<IndexSearcher>(trackingIndexWriter, searcherManager, configBean.getIndexReopenMaxStaleSec(), configBean.getIndexReopenMinStaleSec());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//开启系统的守护线程
		setThread();
	}
	
	/**
	 * 获取最新可用的indexSearcher
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
	 * 释放indexSearcher
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
	 * 设置indexSearcher的守护线程
	 */
	private void setThread () {
		//内存索引重读线程
		this.crtReopenThread.setName("NRTManager reopen thread");
		this.crtReopenThread.setDaemon(true);
		this.crtReopenThread.start();
		
		//内存索引提交线程
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
					//内存索引提交至硬盘
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
