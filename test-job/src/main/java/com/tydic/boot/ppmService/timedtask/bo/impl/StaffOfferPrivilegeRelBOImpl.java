package com.tydic.boot.ppmService.timedtask.bo.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tydic.boot.ppmService.server.utils.Tools;
import com.tydic.boot.ppmService.timedtask.bo.StaffOfferPrivilegeRelBO;
import com.tydic.boot.ppmService.timedtask.dao.TimedTaskDAOImpl;

@Service("staffOfferPrivilegeRelBO")
public class StaffOfferPrivilegeRelBOImpl implements StaffOfferPrivilegeRelBO {

	public static Logger logger = LoggerFactory.getLogger(StaffOfferPrivilegeRelBOImpl.class);

	@Resource(name="timedTaskDao")
	private TimedTaskDAOImpl timedTaskDao;

	@Override
	public void handle(Map<String, String> paramMap) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter the method " + this.getClass().getName() + ".handle(Map paramMap).");
		}
		String batchNo = Tools.getBatchNo();
		Map numMap = new HashMap();
		numMap.put("NUM", "500000");
		List<Map> staffOfferPrivilegeLogList= timedTaskDao.queryStaffOfferPrivilegeLog(numMap);
		if(!Tools.isNull(staffOfferPrivilegeLogList)){
			boolean isOnceAgain = false;
			for(Map staffOfferPrivilegeLogMap : staffOfferPrivilegeLogList){
				String staffId = staffOfferPrivilegeLogMap.get("STAFF_ID")+"";
				if(!Tools.isNull(staffId)){
					if("-9999".equals(staffId)){
						isOnceAgain = true;
						List<Map> systemUserList = timedTaskDao.querySystemUser(numMap);
						if(!Tools.isNull(systemUserList)){
							for(Map systemUserMap : systemUserList){
								timedTaskDao.insertStaffOfferPrivilegeLog(systemUserMap);
							}
						}
						staffOfferPrivilegeLogMap.put("BATCH_NO", batchNo);
						timedTaskDao.updateStaffOfferPrivilegeLog(staffOfferPrivilegeLogMap);
					}else{
						timedTaskDao.deleteStaffOfferrivilegeRel(staffOfferPrivilegeLogMap);
						List<Map> SOPRInfoList = timedTaskDao.querySOPRInfo(staffOfferPrivilegeLogMap);
						if(!Tools.isNull(SOPRInfoList)){
							for(Map temp : SOPRInfoList){
								timedTaskDao.insertStaffOfferrivilegeRel(temp);
							}
						}
						staffOfferPrivilegeLogMap.put("BATCH_NO", batchNo);
						timedTaskDao.updateStaffOfferPrivilegeLog(staffOfferPrivilegeLogMap);
					}
				}
			}
			
			if(isOnceAgain){
				List<Map> list= timedTaskDao.queryStaffOfferPrivilegeLog(numMap);
				for(Map map : list){
					timedTaskDao.deleteStaffOfferrivilegeRel(map);
					List<Map> SOPRInfoList = timedTaskDao.querySOPRInfoAll(map);
					if(!Tools.isNull(SOPRInfoList)){
						for(Map temp : SOPRInfoList){
							timedTaskDao.insertStaffOfferrivilegeRel(temp);
						}
					}
					map.put("BATCH_NO", batchNo);
					timedTaskDao.updateStaffOfferPrivilegeLog(map);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("exit the method " + this.getClass().getName() + ".handle(Map paramMap).");
		}
	}
}
