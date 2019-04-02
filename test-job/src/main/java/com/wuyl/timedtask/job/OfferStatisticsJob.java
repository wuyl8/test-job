package com.wuyl.timedtask.job;

import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuyl.server.utils.SpringContextUtil;
import com.wuyl.timedtask.bo.OfferStatisticsBO;
import com.wuyl.timedtask.init.JobInit;

public class OfferStatisticsJob extends JobInit{
	
	public static Logger logger = LoggerFactory.getLogger(OfferStatisticsJob.class);

	/**ftp服务器地址*/
	private String ftpServer; 
	/**用户*/
	private String ftpUsername;
	/**密码*/
	private String ftpPassword;
	/**文件路径*/
	private String ftpFilePath;
	/**下载路径*/
	private String ftpFileDownloadPath;
	
	public String getFtpServer() {
		return ftpServer;
	}

	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpFilePath() {
		return ftpFilePath;
	}

	public void setFtpFilePath(String ftpFilePath) {
		this.ftpFilePath = ftpFilePath;
	}

	public String getFtpFileDownloadPath() {
		return ftpFileDownloadPath;
	}

	public void setFtpFileDownloadPath(String ftpFileDownloadPath) {
		this.ftpFileDownloadPath = ftpFileDownloadPath;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("enter the method " + this.getClass().getName() + ".execute(JobExecutionContext context).");
		}
		try{
			OfferStatisticsBO offerStatisticsBO = (OfferStatisticsBO)SpringContextUtil.getBean("offerStatisticsBO");
			//获取任务key
			String jobKey = context.getJobDetail().getKey().toString();
			logger.info("任务Key:"+jobKey+"开始执行！");
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			Map paramMap = jobDataMap.getWrappedMap();
			offerStatisticsBO.handle(paramMap);
		}catch(Exception e){
			logger.error("get error in" +  this.getClass().getName(),e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("exit the method " + this.getClass().getName() + ".execute(JobExecutionContext context).");
		}
	}

	@Override
	public void initParam(JobDataMap jobDataMap) {
		jobDataMap.put("ftpServer", ftpServer);
        jobDataMap.put("ftpUsername", ftpUsername);
        jobDataMap.put("ftpPassword", ftpPassword);
        jobDataMap.put("ftpFilePath", ftpFilePath);
        jobDataMap.put("ftpFileDownloadPath", ftpFileDownloadPath);
	}

}
