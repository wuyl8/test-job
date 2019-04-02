package com.wuyl.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.wuyl.server.common.ParentService;
import com.wuyl.server.utils.SpringContextUtil;
import com.wuyl.timedtask.dao.TimedTaskDAOImpl;

public class AutoTestServlet extends HttpServlet {

	private static final long serialVersionUID = -4016678827911759540L;
	
	private static final Logger logger = LoggerFactory.getLogger(AutoTestServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			TimedTaskDAOImpl timedTaskDao = (TimedTaskDAOImpl) SpringContextUtil.getBean("timedTaskDao");
			timedTaskDao.autoTest();
			resultMap.put("code", "0000");
		} catch (Exception e) {
			logger.error("get error in " + this.getClass().getName() + " .doPost()", e);
			resultMap.put("code", "0001");
		} 
		Gson gson = new Gson();
		String str = gson.toJson(resultMap);
		response.getWriter().print(str);
	}
	
}
