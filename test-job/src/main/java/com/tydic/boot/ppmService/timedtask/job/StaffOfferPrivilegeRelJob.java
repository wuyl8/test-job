package com.tydic.boot.ppmService.timedtask.job;

import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tydic.boot.ppmService.server.utils.SpringContextUtil;
import com.tydic.boot.ppmService.timedtask.bo.OfferStatisticsBO;
import com.tydic.boot.ppmService.timedtask.bo.StaffOfferPrivilegeRelBO;
import com.tydic.boot.ppmService.timedtask.init.JobInit;

public class StaffOfferPrivilegeRelJob extends JobInit{
	public static Logger logger = LoggerFactory.getLogger(StaffOfferPrivilegeRelJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("enter the method " + this.getClass().getName() + ".execute(JobExecutionContext context).");
		}
		try{
			StaffOfferPrivilegeRelBO staffOfferPrivilegeRelBO = (StaffOfferPrivilegeRelBO)SpringContextUtil.getBean("staffOfferPrivilegeRelBO");
			//获取任务key
			String jobKey = context.getJobDetail().getKey().toString();
			logger.info("任务Key:"+jobKey+"开始执行！");
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			Map paramMap = jobDataMap.getWrappedMap();
			staffOfferPrivilegeRelBO.handle(paramMap);
		}catch(Exception e){
			logger.error("get error in" +  this.getClass().getName(),e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("exit the method " + this.getClass().getName() + ".execute(JobExecutionContext context).");
		}
	}

	@Override
	public void initParam(JobDataMap jobDataMap) {
		// TODO Auto-generated method stub
		
	}

}
