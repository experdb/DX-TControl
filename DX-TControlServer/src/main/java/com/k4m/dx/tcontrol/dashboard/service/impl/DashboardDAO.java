package com.k4m.dx.tcontrol.dashboard.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.k4m.dx.tcontrol.dashboard.service.DashboardVO;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("dashboardDAO")
public class DashboardDAO extends EgovAbstractMapper{
	
	
	/**
	 * Dashboard 스케쥴 정보 조회
	 * 
	 * @param param
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public DashboardVO selectDashboardScheduleInfo() throws SQLException{
		return (DashboardVO) selectOne("dashboardSql.selectDashboardScheduleInfo");
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public DashboardVO selectDashboardBackupInfo() throws SQLException{
		return (DashboardVO) selectOne("dashboardSql.selectDashboardBackupInfo");
	}
	
	public List<DashboardVO> selectDashboardServerInfo(DashboardVO vo) throws SQLException{
		return (List<DashboardVO>) list("dashboardSql.selectDashboardServerInfo", vo);
	}
	
}
