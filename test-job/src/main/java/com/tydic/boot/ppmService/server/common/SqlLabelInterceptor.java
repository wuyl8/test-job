package com.tydic.boot.ppmService.server.common;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.tydic.fm.mybatis.sqlparse.sqlvo.ParseSqlText;

@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class }),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class SqlLabelInterceptor implements Interceptor {
	private final static Log LOG = LogFactory.getLog(SqlLabelInterceptor.class);
	private String serviceName;
	private String verName;
	private Set<String> noSqlLabelList = new HashSet<String>();

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			String sql = getSqlByInvocation(invocation);
			if (isNull(sql)) {
				return invocation.proceed();
			}

			String operType = this.getOperateType(invocation);
			if (isNull(operType)) {
				return invocation.proceed();
			}
			//后期完善过滤
			sql = sql.trim();
			String sqlId = getSqlId(invocation);

			if (noSqlLabelList.contains(sqlId)) {
				LOG.info("不需要添加sql标签:sqlId=" + sqlId);
				return invocation.proceed();
			}

			LOG.info("开始添加sql标签:sqlId=" + sqlId);
			String sql2Reset = sql.substring(0,6) + " /* [sqlId:" + sqlId + ",appName:" + serviceName + ",Mode:" + verName
					+ "] */ " + sql.substring(6);
			// 包装sql后，重置到invocation中
			LOG.info("添加sql标签后:" + sql2Reset);
			resetSql2Invocation(invocation, sql2Reset);
		} catch (Exception e) {
			LOG.info("添加sql标签失败" + e.getMessage());
		}
		// 返回，继续执行
		return invocation.proceed();
	}

	public static boolean isNull(Object obj) {
		return obj == null ? true
				: (obj instanceof String ? "".equals(obj) || "null".equals(obj)
						: (obj instanceof Collection ? ((Collection) obj).size() == 0
								: (obj instanceof Map ? ((Map) obj).size() == 0 : false)));
	}

	public Object plugin(Object obj) {
		return Plugin.wrap(obj, this);
	}

	public void setProperties(Properties properties) {
		this.serviceName = properties.getProperty("serviceName");
		this.verName = properties.getProperty("verName");
		String temp = properties.getProperty("noSqlLabelList");
		if (temp != null) {
			StringTokenizer st = new StringTokenizer(temp, ",");
			while (st.hasMoreElements()) {
				String ele = (String) st.nextElement();
				if (null != ele && !"".equals(ele)) {
					noSqlLabelList.add(ele.trim());
				}
			}
			LOG.info("不需要添加标签，放过的sqlId:" + noSqlLabelList);
		}
	}

	private String getSqlByInvocation(Invocation invocation) {
		final Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		Object parameterObject = args[1];
		BoundSql boundSql = ms.getBoundSql(parameterObject);
		return boundSql.getSql();
	}

	private void resetSql2Invocation(Invocation invocation, String sql) throws SQLException {
		final Object[] args = invocation.getArgs();
		MappedStatement statement = (MappedStatement) args[0];
		Object parameterObject = args[1];
		BoundSql boundSql = statement.getBoundSql(parameterObject);
		MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSql));
		MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(),
				new DefaultObjectWrapperFactory());

		msObject.setValue("sqlSource.boundSql.sql", sql);
		args[0] = newStatement;
	}

	private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	class BoundSqlSqlSource implements SqlSource {

		private BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}

		@Override
		public void setSqlTextList(List<ParseSqlText> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public List<ParseSqlText> getSqlTextList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setSql(String arg0) {
			// TODO Auto-generated method stub

		}
	}

	private String getOperateType(Invocation invocation) {
		final Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		SqlCommandType commondType = ms.getSqlCommandType();
		if (commondType.compareTo(SqlCommandType.SELECT) == 0) {
			return "select";
		}
		if (commondType.compareTo(SqlCommandType.INSERT) == 0) {
			return "insert";
		}
		if (commondType.compareTo(SqlCommandType.UPDATE) == 0) {
			return "update";
		}
		if (commondType.compareTo(SqlCommandType.DELETE) == 0) {
			return "delete";
		}
		return null;
	}

	private String getSqlId(Invocation invocation) {
		final Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		return ms.getId();
	}

}