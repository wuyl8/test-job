package com.tydic.boot.ppmService.timedtask.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tydic.boot.ppmService.server.common.ParentService;

@Repository("timedTaskDao")
public class TimedTaskDAOImpl extends ParentService {
	
	public List autoTest(){
		return this.selectList(null, "TimedTask.autoTest",new HashMap());
	}
	
	public void insertOfferStatistics(Map<String, String> paramMap){
		this.insert(null, "TimedTask.insertOfferStatistics", paramMap);
	}
	
	public List<Map> queryStaffOfferPrivilegeLog(Map<String, String> paramMap){
		return this.selectList(null, "TimedTask.queryStaffOfferPrivilegeLog",paramMap);
	}
	
	public List<Map> querySystemUser(Map<String, String> paramMap){
		return this.selectList(null, "TimedTask.querySystemUser",paramMap);
	}
	
	public void insertStaffOfferPrivilegeLog(Map<String, String> paramMap){
		this.insert(null, "TimedTask.insertStaffOfferPrivilegeLog", paramMap);
	}
	
	public void updateStaffOfferPrivilegeLog(Map<String, String> paramMap){
		this.update(null, "TimedTask.updateStaffOfferPrivilegeLog", paramMap);
	}
	
	public void deleteStaffOfferrivilegeRel(Map<String, String> paramMap){
		this.delete(null, "TimedTask.deleteStaffOfferrivilegeRel", paramMap);
	}
	
	public List<Map> querySOPRInfoAll(Map<String, String> paramMap){
		return this.selectList(null, "TimedTask.querySOPRInfoAll",paramMap);
	}
	
	public List<Map> querySOPRInfo(Map<String, String> paramMap){
		return this.selectList(null, "TimedTask.querySOPRInfo",paramMap);
	}
	
	public void insertStaffOfferrivilegeRel(Map<String, String> paramMap){
		this.insert(null, "TimedTask.insertStaffOfferrivilegeRel", paramMap);
	}

	public void delOfferStatisticsOnce(Map<String, String> paramMap) {
		this.delete(null, "TimedTask.delOfferStatisticsOnce",paramMap);
	}

	public String sumUserAmountByear(Map<String, String> paramMap) {
		return this.selectOne(null, "TimedTask.sumUserAmountByear",paramMap);
	}

	public String sumUserArrivalByear(Map<String, String> paramMap) {
		return this.selectOne(null, "TimedTask.sumUserArrivalByear",paramMap);
	}

	public void insertOfferStatisticsOnce(Map<String, String> paramMap) {
		this.insert(null, "TimedTask.insertOfferStatisticsOnce", paramMap);		
	}

	public void updateOfferStatisticsOnce(Map<String, String> paramMap) {
		this.update(null, "TimedTask.updateOfferStatisticsOnce", paramMap);		
	}
	
}
