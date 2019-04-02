package com.tydic.boot.ppmService.timedtask.init;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* @ClassName JobInit  
* @Description 此为定时任务初始化抽象类，新建job继承该类并调用init方法即可完成定时任务的初始化和注册 
* @author zhangpeng 
* @date 2018年6月12日  
*
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public abstract class JobInit implements Job {
	
	protected Logger initLogger;
	
	/**job名称*/
	protected String jobName;
	
	/**本地网,默认为888*/
	protected String latnId = "888";
	
	/**处理条数*/
	protected String dealNum;
	
	/**开关*/
	protected String switchState; 
	
	/**
	 *  执行时间 
	*   #cronExpression从左到右（用空格隔开）：秒 分 小时 月份中的日期 月份 星期中的日期 年份
	*	#字段				允许值					允许的特殊字符
	*	#秒（Seconds）		0~59的整数				, - * /    四个字符
	*	#分（Minutes）		0~59的整数				, - * /    四个字符
	*	#小时（Hours）		0~23的整数				, - * /    四个字符
	*	#日期（DayofMonth）	1~31的整数				,- * ? / L W C     八个字符
	*	#月份（Month）		1~12的整数或者 JAN-DEC	, - * /    四个字符
	*	#星期（DayofWeek）	1~7的整数或者 SUN-SAT（1=SUN）	, - * ? / L C #     八个字符
	*	#年(可选，留空)（Year）	1970~2099			, - * /    四个字符
	*
	*	示例：
	*		0 0 2 1 * ? *   表示在每月的1日的凌晨2点执行任务
	*		0/20 1/2 * * * ? 表示从01分开始，每隔两分钟的00秒，20秒，40秒执行任务
	*/
	protected String cronExpression; 
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getLatnId() {
		return latnId;
	}

	public void setLatnId(String latnId) {
		this.latnId = latnId;
	}

	public String getDealNum() {
		return dealNum;
	}
	
	public void setDealNum(String dealNum) {
		this.dealNum = dealNum;
	}

	public String getSwitchState() {
		return switchState;
	}

	public void setSwitchState(String switchState) {
		this.switchState = switchState;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	public Logger getInitLogger() {
		return initLogger;
	}

	public void setInitLogger(Logger initLogger) {
		this.initLogger = initLogger;
	}


	public static Scheduler scheduler = null;
	
	static{
		Logger staticLogger = LoggerFactory.getLogger(JobInit.class);
		try {
			// 获取Scheduler实例
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			 // 调度器启动
	        scheduler.start();
	        staticLogger.info("调度器启动成功！");
		} catch (SchedulerException e) {
			staticLogger.error("调度器启动失败！", e);
		}
	}
	
	public void init(){
		//设置日志
		setInitLogger(LoggerFactory.getLogger(this.getClass()));
		
		if(JobInit.isNull(switchState)) {
			switchState = "off";
		}
		
		if(JobInit.isNull(jobName)){
			jobName = this.getClass().getName();
		}
		
		if(!"on".equals(switchState.toLowerCase())) {
			initLogger.info("定时任务["+jobName+"]开关为："+switchState+"任务不启动！");
			return;
		}
		
		if(JobInit.isNull(cronExpression)){
			initLogger.error("定时任务["+jobName+"]执行时间配置[cronExpression]为空，禁止任务启动！");
			return;
		}
		
		if(JobInit.isNull(latnId)){
			initLogger.error("定时任务["+jobName+"]本地网配置为空，禁止任务启动！");
			return;
		}
		
		initLogger.info("定时任务["+jobName+"]启动中...");
		initLogger.info("定时任务["+jobName+"]启动类:" + this.getClass().getName());
		initLogger.info("定时任务["+jobName+"]启动cron表达式:"+cronExpression);
		try {
			// 判断Scheduler实例是否存在，不存在再次获取，并再次启动调度器
			if(scheduler == null){
				scheduler = StdSchedulerFactory.getDefaultScheduler();
				// 调度器启动
		        scheduler.start();
			}
			// 具体任务
			JobDetail jobDetail = JobBuilder.newJob(this.getClass()).withIdentity(jobName, "group").build();
	        
	        //参数
	        JobDataMap jobDataMap = jobDetail.getJobDataMap();
	        jobDataMap.put("latnId",latnId);
	        jobDataMap.put("dealNum",dealNum);
	        this.initParam(jobDataMap);
	        
	        // 触发器
	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
	        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName+".trigger", "group").withSchedule(cronScheduleBuilder).build();
	        //注册
	        scheduler.scheduleJob(jobDetail, cronTrigger);
	        initLogger.info("定时任务["+jobName+"]启动成功！");
		} catch (SchedulerException e) {
			initLogger.error("定时任务["+jobName+"]启动失败！", e);
		}
	}
	/**
	 * 供定时任务的个性化配置和配置扩展使用
	 * 具体定时任务job初始化参数使用，若无可实现后放空
	 * @Title: initParam  
	 * @return void
	 * @param jobDataMap
	 *
	 */
	public abstract void initParam(JobDataMap jobDataMap);
	
	public void destroyed() {
        /* 注销定时任务 */
        try {
            // 关闭Scheduler
            scheduler.shutdown();
        } catch (SchedulerException e) {
        	initLogger.error("调度器关闭失败！", e);
        }
    }
	
	public static boolean isNull(String str){
		boolean re = false;
		if(str == null){
			re = true;
		}else if("".equals(str.trim())){
			re = true;
		}else if("null".equalsIgnoreCase(str.trim())){
			re = true;
		}else if(str.contains("${") && str.contains("}")){
			re = true;
		}
		return re;
	}
}
