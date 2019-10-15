package com.k4m.dx.tcontrol.db2pg.setting.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.k4m.dx.tcontrol.db2pg.setting.service.CodeVO;
import com.k4m.dx.tcontrol.db2pg.setting.service.DDLConfigVO;
import com.k4m.dx.tcontrol.db2pg.setting.service.DataConfigVO;
import com.k4m.dx.tcontrol.db2pg.setting.service.Db2pgSettingService;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("Db2pgSettingServiceImpl")
public class Db2pgSettingServiceImpl extends EgovAbstractServiceImpl implements Db2pgSettingService {

	@Resource(name = "db2pgSettingDAO")
	private Db2pgSettingDAO db2pgSettingDAO;

	@Override
	public List<CodeVO> selectCode(String grp_cd) throws Exception {
		return db2pgSettingDAO.selectCode(grp_cd);
	}

	@Override
	public List<DDLConfigVO> selectDDLWork(Map<String, Object> param) throws Exception {
		return db2pgSettingDAO.selectDDLWork(param);
	}	
	
	@Override
	public List<DataConfigVO> selectDataWork(Map<String, Object> param) throws Exception {
		return db2pgSettingDAO.selectDataWork(param);
	}

	@Override
	public int selectExrttrgSrctblsSeq() throws Exception {
		return db2pgSettingDAO.selectExrttrgSrctblsSeq();
	}

	@Override
	public int selectExrtexctSrctblsSeq() throws Exception {
		return db2pgSettingDAO.selectExrtexctSrctblsSeq();
	}

	@Override
	public void insertDDLWork(DDLConfigVO ddlConfigVO) throws Exception {
		db2pgSettingDAO.insertDDLWork(ddlConfigVO);
		
	}
	
	@Override
	public void updateDDLWork(DDLConfigVO ddlConfigVO) throws Exception {
		db2pgSettingDAO.updateDDLWork(ddlConfigVO);
	}
	
	@Override
	public void insertDataWork(DataConfigVO dataConfigVO) throws Exception {
		db2pgSettingDAO.insertDataWork(dataConfigVO);
	}

	@Override
	public DDLConfigVO selectDetailDDLWork(int db2pg_ddl_wrk_id) throws Exception {
		return db2pgSettingDAO.selectDetailDDLWork(db2pg_ddl_wrk_id);
	}


}
