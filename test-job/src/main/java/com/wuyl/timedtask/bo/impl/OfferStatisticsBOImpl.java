package com.wuyl.timedtask.bo.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wuyl.server.utils.SftpClient;
import com.wuyl.server.utils.Tools;
import com.wuyl.timedtask.bo.OfferStatisticsBO;
import com.wuyl.timedtask.dao.TimedTaskDAOImpl;

@Service("offerStatisticsBO")
public class OfferStatisticsBOImpl implements OfferStatisticsBO {
	public static Logger logger = LoggerFactory.getLogger(OfferStatisticsBOImpl.class);

	@Resource(name="timedTaskDao")
	private TimedTaskDAOImpl timedTaskDao;
	
	@Override
	public void handle(Map<String,String> paramMap) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter the method " + this.getClass().getName() + ".handle(Map paramMap).");
		}
		String ftpServer = paramMap.get("ftpServer");
		String ftpUsername = paramMap.get("ftpUsername");
		String ftpPassword = paramMap.get("ftpPassword");
		String ftpFilePath = paramMap.get("ftpFilePath");
		String ftpFileDownloadPath = paramMap.get("ftpFileDownloadPath");
		SftpClient sftpClient = new SftpClient();
		try{
			//本地文件保存目录,若为空则默认tomcat根目录
			if(Tools.isNull(ftpFileDownloadPath)){
				String tomcatHome = System.getProperty("catalina.home");
				ftpFileDownloadPath = tomcatHome+"/cpcFile";
			}
			//本地文件保存目录,路径不存在创建路径
			File dir = new File(ftpFileDownloadPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 设置sftp服务器连接
			sftpClient.connect(ftpServer, 22, ftpUsername, ftpPassword);
			//当月文件名前缀
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String dateStr = sdf.format(new Date());
			//获取目录下所有文件名
			List<String> ftpFileNameList = sftpClient.listFiles(ftpFilePath);
			List<String> fileNameList = new ArrayList<String>();
			//正常的文件
			String fileNamePrefix = "DW_LABEL_OFFER_HZ_M.1000." + dateStr;
			for(String flieName : ftpFileNameList){
				if(flieName.startsWith(fileNamePrefix)){
					fileNameList.add(flieName);
				}
			}
			//重传的文件
			fileNamePrefix = "DW_LABEL_OFFER_HZ_M.2000." + dateStr;
			for(String flieName : ftpFileNameList){
				if(flieName.startsWith(fileNamePrefix)){
					fileNameList.add(flieName);
				}
			}
			if(fileNameList != null && fileNameList.size() > 0){
				//当月文件全部下载到本地
				for(String flieName : fileNameList){
					String localFileName = ftpFileDownloadPath + "/"+flieName;
					//下载
					sftpClient.download(ftpFilePath, flieName, localFileName);
					//下载完成后，修改服务器上的文件名
					sftpClient.rename(ftpFilePath, flieName, "ALREADY_DOWNLOADED_"+flieName);
				}
				
				//清空OfferStatisticsOnce表
				Map<String, String> paramMapNull = new HashMap<>();
				timedTaskDao.delOfferStatisticsOnce(paramMapNull);
				
				//遍历本地文件，解析到数据库
				for(String flieName : fileNameList){
					String localFileName = ftpFileDownloadPath + "/"+flieName;
					File localFile = new File(localFileName);
					if(localFile.length() > 0){
						InputStreamReader read = new InputStreamReader(new FileInputStream(localFile), "UTF-8");
						BufferedReader bf = new BufferedReader(read);
						long index = 0L;
						String str = null;
						// 按行读取字符串
						while ((str = bf.readLine()) != null) {
							index++;
							try{
								byte bytes[] = {0x01};
								String splitStr = new String(bytes);
								String[] strArr = str.split(splitStr);
								if(strArr.length > 0){
									Map<String, String> map = new HashMap<String, String>();
									map.put("ACCT_PERIOD",strArr[0]);
									//Latn_id取值，如果取得是861，我们这边转换成888存储
									if(strArr[1].trim().equals("861")){
										map.put("LATN_ID","888");
									}else{
										map.put("LATN_ID",strArr[1]);
									}
									map.put("LATN_NAME",strArr[2]);
									map.put("OFFER_ID",strArr[3]);
									map.put("OFFER_NAME",strArr[4]);
									map.put("OFFER_TYPE",strArr[5]);
									map.put("OFFER_ARRIVAL_NUM",strArr[6]);
									map.put("USER_ARRIVAL_NUM",strArr[7]);
									map.put("OFFER_GROWTH_NUM",strArr[8]);
									map.put("OFFER_NEW_ORDERS",strArr[9]);
									map.put("OFFER_CHANGE_NUM",strArr[10]);
									map.put("OFFER_MOVE_NUM",strArr[11]);
									map.put("OFFER_OFF_NUM",strArr[12]);
									map.put("OFFER_ALL_INCOME",strArr[13]);
									map.put("NEW_CUR_ARPU",strArr[14]);
									map.put("CHANGE_CUR_ARPU",strArr[15]);
									map.put("CHANGE_LAST_MONTN_ARPU",strArr[16]);
									map.put("OFFER_TRAFFIC",strArr[17]);
									map.put("OFFER_FLOW",strArr[18]);
									map.put("OFFER_MOU",strArr[19]);
									map.put("OFFER_DOU",strArr[20]);
									map.put("ONE_MONTH_ORDERS",strArr[21]);
									map.put("USERS_AMOUNT",strArr[22]);
									map.put("THREE_MONTH_ORDERS",strArr[23]);
									map.put("SIX_MONTH_ORDERS",strArr[24]);
									map.put("TWELVE_MONTH_ORDERS",strArr[25]);
									
									map.put("AVG_MOBCARD_NUM", strArr[26]);
									map.put("AVG_ACTIVE_MOBCARD_NUM", strArr[27]);
									map.put("AVG_BB_NUM", strArr[28]);
									map.put("AVG_ACTIVE_BB_NUM", strArr[29]);
									map.put("AVG_NETITV_NUM", strArr[30]);
									map.put("AVG_ACTIVE_NATITV_NUM", strArr[31]);
									map.put("MOB_ARRIVAL", strArr[32]);
									map.put("MOB_MAINCARD_ARRIVAL", strArr[33]);
									map.put("MOB_GROWTH", strArr[34]);
									map.put("MOB_MAINCARD_GROWTH", strArr[35]);
									map.put("ADD_MOB_NUM", strArr[36]);
									map.put("MOB_ADD", strArr[37]);
									map.put("MOB_MAINCARD_ADD", strArr[38]);
									map.put("MOB_INCOME", strArr[39]);
									map.put("MOB_ARPU", strArr[40]);
									map.put("MOB_DOU", strArr[41]);
									map.put("MOB_MOU", strArr[42]);
									map.put("MOB_LOST_USERS", strArr[43]);
									map.put("BB_BILLING_ARRIVAL", strArr[44]);
									map.put("BB_BILLING_GROWTH", strArr[45]);
									map.put("BB_ADD_NUM", strArr[46]);
									map.put("BB_ADD", strArr[47]);
									map.put("BB_FIRST_ADD", strArr[48]);
									map.put("BB_SECOND_ADD", strArr[49]);
									map.put("BB_ACTIVE_USER", strArr[50]);
									map.put("BB_INCOME", strArr[51]);
									map.put("BB_ARPU", strArr[52]);
									map.put("IPTV_ADD_NUM", strArr[53]);
									map.put("IPTV_ADD_INSTALL", strArr[54]);
									map.put("IPTV_ADD_HD", strArr[55]);
									map.put("IPTV_ADD_SD", strArr[56]);
									map.put("IPTV_ARRIVAL", strArr[57]);
									map.put("IPTV_ARRIVAL_HD", strArr[58]);
									map.put("IPTV_ARRIVAL_SD", strArr[59]);
									map.put("IPTV_GROWTH", strArr[60]);
									map.put("IPTV_GROWTH_HD", strArr[61]);
									map.put("IPTV_GROWTH_SD", strArr[62]);
									map.put("IPTV_TEARDOWN_NUM", strArr[63]);
									map.put("ARREAR_LOST_NUM", strArr[64]);
									map.put("TOTAL_WATCH_NUM", strArr[65]);
									timedTaskDao.insertOfferStatistics(map);

									timedTaskDao.insertOfferStatisticsOnce(map);
								}
							}catch(Exception e){
								logger.error("处理文件["+flieName+"]第["+index+"]行出错，该行跳过，继续下一行！", e.getMessage());
							}
						}
						bf.close();
						read.close();
					}
				}
			}
		}catch(Exception e){
			logger.error("定时任务处理出错！", e);
		}finally {
			//刷新offer_statistics_once字段数据
			Map<String,String> map = new HashMap<>();
			String userAmountByear = timedTaskDao.sumUserAmountByear(map);
			String userArrivalByear = timedTaskDao.sumUserArrivalByear(map);
			map.put("USER_AMOUNT_BYEAR", userAmountByear);
			map.put("USER_ARRIVAL_BYEAR", userArrivalByear);
			timedTaskDao.updateOfferStatisticsOnce(map);
			
			//关闭远程连接
			sftpClient.close();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("exit the method " + this.getClass().getName() + ".handle(Map paramMap).");
		}
	}

	public static void main(String[] args) {
		Map paramMap = new HashMap();
		paramMap.put("ftpServer", "133.64.177.72");
		paramMap.put("ftpUsername", "mktftpuser");
		paramMap.put("ftpPassword", "mkt^dev30!@");
		paramMap.put("ftpFilePath", "/data1/mktftpuser/cpc");
		paramMap.put("ftpFileDownloadPath", "D:\\test");
		
		OfferStatisticsBOImpl bo = new OfferStatisticsBOImpl();
		bo.handle(paramMap);
	}
}
