package net.begincode.lucene.util;

import java.util.HashSet;

public class IndexConfig {

	// ϵͳ�����ö������
	private static HashSet<ConfigBean> configBeans;

	private static class DefaultIndexConfig {
		private static final HashSet<ConfigBean> configBeansDefault = new HashSet<ConfigBean>();
		static {
			ConfigBean bean = new ConfigBean();
			configBeansDefault.add(bean);
		}
	}

	public static HashSet<ConfigBean> getConfig() {
		if (configBeans == null) {
			// ���configBeansΪ�գ�����ϵͳĬ��ֵ
			return DefaultIndexConfig.configBeansDefault;
		}
		return configBeans;
	}

	/**
	 * ����ϵͳ��������
	 * @param configBeans
	 */
	public static void setConfig(HashSet<ConfigBean> configBeans) {
		IndexConfig.configBeans = configBeans;
	}
}
