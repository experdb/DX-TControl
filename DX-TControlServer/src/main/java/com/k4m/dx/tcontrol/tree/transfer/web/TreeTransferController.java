package com.k4m.dx.tcontrol.tree.transfer.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.k4m.dx.tcontrol.accesscontrol.service.DbIDbServerVO;
import com.k4m.dx.tcontrol.admin.accesshistory.service.AccessHistoryService;
import com.k4m.dx.tcontrol.admin.dbserverManager.service.DbServerManagerService;
import com.k4m.dx.tcontrol.admin.dbserverManager.service.DbServerVO;
import com.k4m.dx.tcontrol.cmmn.AES256;
import com.k4m.dx.tcontrol.cmmn.AES256_KEY;
import com.k4m.dx.tcontrol.cmmn.CmmnUtils;
import com.k4m.dx.tcontrol.cmmn.client.ClientAdapter;
import com.k4m.dx.tcontrol.cmmn.client.ClientInfoCmmn;
import com.k4m.dx.tcontrol.cmmn.client.ClientProtocolID;
import com.k4m.dx.tcontrol.cmmn.client.ClientTranCodeType;
import com.k4m.dx.tcontrol.common.service.AgentInfoVO;
import com.k4m.dx.tcontrol.common.service.CmmnServerInfoService;
import com.k4m.dx.tcontrol.common.service.HistoryVO;
import com.k4m.dx.tcontrol.functions.transfer.service.ConnectorVO;
import com.k4m.dx.tcontrol.functions.transfer.service.TransferService;
import com.k4m.dx.tcontrol.functions.transfer.service.TransferVO;
import com.k4m.dx.tcontrol.tree.transfer.service.BottlewaterVO;
import com.k4m.dx.tcontrol.tree.transfer.service.TransferDetailMappingVO;
import com.k4m.dx.tcontrol.tree.transfer.service.TransferDetailVO;
import com.k4m.dx.tcontrol.tree.transfer.service.TransferMappingVO;
import com.k4m.dx.tcontrol.tree.transfer.service.TransferRelationVO;
import com.k4m.dx.tcontrol.tree.transfer.service.TransferTargetVO;
import com.k4m.dx.tcontrol.tree.transfer.service.TreeTransferService;

/**
 * TransferSetting 컨트롤러 클래스를 정의한다.
 *
 * @author 김주영
 * @see
 * 
 *      <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2017.07.24   김주영 최초 생성
 *      </pre>
 */
@Controller
public class TreeTransferController {

	@Autowired
	private AccessHistoryService accessHistoryService;

	@Autowired
	private TreeTransferService treeTransferService;

	@Autowired
	private TransferService transferService;

	@Autowired
	private DbServerManagerService dbServerManagerService;

	@Autowired
	private CmmnServerInfoService cmmnServerInfoService;

	/**
	 * 전송설정 화면을 보여준다.
	 * 
	 * @param
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/treeTransferSetting.do")
	public ModelAndView transferSetting(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		try {
			// 전송설정 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0011");
			accessHistoryService.insertHistory(historyVO);

			mv.setViewName("transfer/transferSetting");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 전송대상설정 화면을 보여준다.
	 * 
	 * @param
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/transferTarget.do")
	public ModelAndView transferTarget(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		try {
			// 전송대상리스트 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0014");
			accessHistoryService.insertHistory(historyVO);
			mv.addObject("cnr_id", request.getParameter("cnr_id"));
			mv.addObject("cnr_nm", request.getParameter("cnr_nm"));
			mv.setViewName("transfer/transferTarget");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 전송대상 리스트를 조회한다.
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectTransferTarget.do")
	public @ResponseBody JSONObject selectTransferTarget(
			@ModelAttribute("transferTargetVO") TransferTargetVO transferTargetVO, HttpServletRequest request) {
		JSONObject result = new JSONObject();
		List<ConnectorVO> resultList = null;

		try {
			int cnr_id = Integer.parseInt(request.getParameter("cnr_id"));
			resultList = transferService.selectDetailConnectorRegister(cnr_id);

			// strName : 공백이면 전체 검색
			String strName = "";

			JSONObject serverObj = new JSONObject();

			String strServerIp = resultList.get(0).getCnr_ipadr();
			String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());

			serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
			serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);
			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.kafakConnect_select(serverObj, strName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 전송대상등록 팝업 화면을 보여준다.
	 * 
	 * @param
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/popup/transferTargetRegForm.do")
	public ModelAndView transferTargetRegForm(@ModelAttribute("historyVO") HistoryVO historyVO,
			HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		JSONObject result = new JSONObject();
		List<ConnectorVO> resultList = null;

		try {

			CmmnUtils.saveHistory(request, historyVO);

			String act = request.getParameter("act");
			int cnr_id = Integer.parseInt(request.getParameter("cnr_id"));
			if (act.equals("i")) {
				// 전송대상등록팝업 이력 남기기
				historyVO.setExe_dtl_cd("DX-T0015");
				accessHistoryService.insertHistory(historyVO);
			}
			if (act.equals("u")) {
				// 전송대상수정팝업 이력 남기기
				historyVO.setExe_dtl_cd("DX-T0015_01");
				accessHistoryService.insertHistory(historyVO);

				resultList = transferService.selectDetailConnectorRegister(cnr_id);

				String strName = request.getParameter("name");
				JSONObject serverObj = new JSONObject();
				String strServerIp = resultList.get(0).getCnr_ipadr();
				String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());

				serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
				serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);
				ClientInfoCmmn cic = new ClientInfoCmmn();
				result = cic.kafakConnect_select(serverObj, strName);

				for (int i = 0; i < result.size(); i++) {
					JSONArray data = (JSONArray) result.get("data");
					for (int m = 0; m < data.size(); m++) {
						JSONObject jsonObj = (JSONObject) data.get(m);

						JSONObject hp = (JSONObject) jsonObj.get("hp");

						String rotate_interval_ms = String.valueOf(hp.get("rotate.interval.ms"));
						String hadoop_home = (String) hp.get("hadoop.home");
						String trf_trg_url = (String) hp.get("hdfs.url");
						String topics = (String) hp.get("topics");
						String task_max = String.valueOf(hp.get("tasks.max"));
						String trf_trg_cnn_nm = (String) hp.get("name");
						String hadoop_conf_dir = (String) hp.get("hadoop.conf.dir");
						String flush_size = String.valueOf(hp.get("flush.size"));
						String connector_class = (String) hp.get("connector.class");

						mv.addObject("trf_trg_cnn_nm", trf_trg_cnn_nm);
						mv.addObject("trf_trg_url", trf_trg_url);
						mv.addObject("topics", topics);
						mv.addObject("connector_class", connector_class);
						mv.addObject("task_max", task_max);
						mv.addObject("hadoop_conf_dir", hadoop_conf_dir);
						mv.addObject("hadoop_home", hadoop_home);
						mv.addObject("flush_size", flush_size);
						mv.addObject("rotate_interval_ms", rotate_interval_ms);
					}
				}

			}
			mv.addObject("act", act);
			mv.addObject("cnr_id", cnr_id);
			mv.setViewName("popup/transferTargetRegForm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 커넥트명 중복 체크한다.
	 * 
	 * @param trf_trg_cnn_nm
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/transferTargetNameCheck.do")
	public @ResponseBody String transferTargetNameCheck(@RequestParam("trf_trg_cnn_nm") String trf_trg_cnn_nm) {
		try {
			int resultSet = treeTransferService.transferTargetNameCheck(trf_trg_cnn_nm);
			if (resultSet > 0) {
				// 중복값이 존재함.
				return "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "true";
	}

	/**
	 * 전송대상을 등록한다.
	 * 
	 * @param transferTargetVO
	 * @param request
	 * @param historyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/insertTransferTarget.do")
	public @ResponseBody boolean insertTransferTarget(
			@ModelAttribute("transferTargetVO") TransferTargetVO transferTargetVO, HttpServletRequest request,
			@ModelAttribute("historyVO") HistoryVO historyVO) {
		List<ConnectorVO> resultList = null;
		JSONObject serverObj = new JSONObject();
		JSONObject param = new JSONObject();
		ClientInfoCmmn cic = new ClientInfoCmmn();
		try {
			// 전송대상 등록 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0014_02");
			accessHistoryService.insertHistory(historyVO);

			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");
			transferTargetVO.setFrst_regr_id(usr_id);
			transferTargetVO.setLst_mdfr_id(usr_id);

			int cnr_id = Integer.parseInt(request.getParameter("cnr_id"));
			resultList = transferService.selectDetailConnectorRegister(cnr_id);

			String strServerIp = resultList.get(0).getCnr_ipadr();
			String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());
			serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
			serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);

			param.put("strName", transferTargetVO.getTrf_trg_cnn_nm());
			param.put("strConnector_class", transferTargetVO.getConnector_class());
			param.put("strTasks_max", Integer.toString(transferTargetVO.getTask_max()));
			param.put("strHdfs_url", transferTargetVO.getTrf_trg_url());
			param.put("strHadoop_conf_dir", transferTargetVO.getHadoop_conf_dir());
			param.put("strHadoop_home", transferTargetVO.getHadoop_home());
			param.put("strFlush_size", Integer.toString(transferTargetVO.getFlush_size()));
			param.put("strRotate_interval_ms", Integer.toString(transferTargetVO.getRotate_interval_ms()));

			Map<String, Object> result = cic.kafakConnect_create(serverObj, param);
			String strResultCode = (String) result.get("strResultCode");
			if (strResultCode.equals("0")) {
				treeTransferService.insertTransferTarget(transferTargetVO);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 전송대상을 수정한다.
	 * 
	 * @param transferTargetVO
	 * @param request
	 * @param historyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateTransferTarget.do")
	public @ResponseBody boolean updateTransferTarget(
			@ModelAttribute("transferTargetVO") TransferTargetVO transferTargetVO, HttpServletRequest request,
			@ModelAttribute("historyVO") HistoryVO historyVO) {
		ClientInfoCmmn cic = new ClientInfoCmmn();
		JSONObject serverObj = new JSONObject();
		JSONObject param = new JSONObject();
		List<ConnectorVO> resultList = null;
		try {
			// 전송대상 수정 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0014_03");
			accessHistoryService.insertHistory(historyVO);
			int cnr_id = transferTargetVO.getCnr_id();

			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");
			transferTargetVO.setFrst_regr_id(usr_id);
			transferTargetVO.setLst_mdfr_id(usr_id);
			transferTargetVO.setCnr_id(cnr_id);

			resultList = transferService.selectDetailConnectorRegister(cnr_id);

			String strServerIp = resultList.get(0).getCnr_ipadr();
			String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());
			serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
			serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);

			String strName = transferTargetVO.getTrf_trg_cnn_nm();

			String strConnector_class = transferTargetVO.getConnector_class();
			String strTasks_max = Integer.toString(transferTargetVO.getTask_max());
			String strHdfs_url = transferTargetVO.getTrf_trg_url();
			String strHadoop_conf_dir = transferTargetVO.getHadoop_conf_dir();
			String strHadoop_home = transferTargetVO.getHadoop_home();
			String strFlush_size = Integer.toString(transferTargetVO.getFlush_size());
			String strRotate_interval_ms = Integer.toString(transferTargetVO.getRotate_interval_ms());
			String strTopics = request.getParameter("strTopics");

			param.put("strName", strName);
			param.put("strConnector_class", strConnector_class);
			param.put("strTasks_max", strTasks_max);
			param.put("strTopics", strTopics);
			param.put("strHdfs_url", strHdfs_url);
			param.put("strHadoop_conf_dir", strHadoop_conf_dir);
			param.put("strHadoop_home", strHadoop_home);
			param.put("strFlush_size", strFlush_size);
			param.put("strRotate_interval_ms", strRotate_interval_ms);

			Map<String, Object> result = cic.kafakConnect_update(serverObj, param);
			String strResultCode = (String) result.get("strResultCode");
			if (strResultCode.equals("0")) {
				treeTransferService.updateTransferTarget(transferTargetVO);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 전송대상을 삭제한다.
	 * 
	 * @param request
	 * @param historyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteTransferTarget.do")
	public @ResponseBody boolean deleteTransferTarget(
			@ModelAttribute("transferTargetVO") TransferTargetVO transferTargetVO, HttpServletRequest request,
			@ModelAttribute("historyVO") HistoryVO historyVO) {
		ClientInfoCmmn cic = new ClientInfoCmmn();
		JSONObject serverObj = new JSONObject();
		List<ConnectorVO> resultList = null;

		try {
			// 전송대상 삭제 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0014_04");
			accessHistoryService.insertHistory(historyVO);

			int cnr_id = Integer.parseInt(request.getParameter("cnr_id"));

			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");
			transferTargetVO.setFrst_regr_id(usr_id);
			transferTargetVO.setLst_mdfr_id(usr_id);
			transferTargetVO.setCnr_id(cnr_id);

			resultList = transferService.selectDetailConnectorRegister(cnr_id);

			String strServerIp = resultList.get(0).getCnr_ipadr();
			String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());

			serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
			serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);

			String[] param = request.getParameter("name").toString().split(",");
			for (int i = 0; i < param.length; i++) {
				Map<String, Object> result = cic.kafakConnect_delete(serverObj, param[i]);
				String strResultCode = (String) result.get("strResultCode");
				if (strResultCode.equals("0")) {
					String trf_trg_mpp_id = treeTransferService.selectTransfermappid(param[i]);
					if (trf_trg_mpp_id != null) {
						/* 전송대상매핑관계,전송매핑테이블내역 삭제 */
						treeTransferService.deleteTransferRelation(Integer.parseInt(trf_trg_mpp_id));
						treeTransferService.deleteTransferMapping(Integer.parseInt(trf_trg_mpp_id));
					}
					/* 전송대상설정정보 삭제 */
					treeTransferService.deleteTransferTarget(param[i]);
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 전송대상상세 화면을 보여준다.
	 * 
	 * @param
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/popup/transferTargetDetailRegForm.do")
	public ModelAndView transferTargetDetail(@ModelAttribute("historyVO") HistoryVO historyVO,
			HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<ConnectorVO> resultList = null;
		JSONObject serverObj = new JSONObject();
		JSONObject result = new JSONObject();
		ClientInfoCmmn cic = new ClientInfoCmmn();
		try {
			// 전송설정 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0016");
			accessHistoryService.insertHistory(historyVO);

			int cnr_id = Integer.parseInt(request.getParameter("cnr_id"));
			resultList = transferService.selectDetailConnectorRegister(cnr_id);

			String strServerIp = resultList.get(0).getCnr_ipadr();
			String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());
			serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
			serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);
			String strName = request.getParameter("name");
			result = cic.kafakConnect_select(serverObj, strName);
			for (int i = 0; i < result.size(); i++) {
				JSONArray data = (JSONArray) result.get("data");
				for (int m = 0; m < data.size(); m++) {
					JSONObject jsonObj = (JSONObject) data.get(m);

					JSONObject hp = (JSONObject) jsonObj.get("hp");

					String rotate_interval_ms = String.valueOf(hp.get("rotate.interval.ms"));
					String hadoop_home = (String) hp.get("hadoop.home");
					String trf_trg_url = (String) hp.get("hdfs.url");
					String topics = (String) hp.get("topics");
					String task_max = String.valueOf(hp.get("tasks.max"));
					String trf_trg_cnn_nm = (String) hp.get("name");
					String hadoop_conf_dir = (String) hp.get("hadoop.conf.dir");
					String flush_size = String.valueOf(hp.get("flush.size"));
					String connector_class = (String) hp.get("connector.class");

					mv.addObject("rotate_interval_ms", rotate_interval_ms);
					mv.addObject("hadoop_home", hadoop_home);
					mv.addObject("trf_trg_url", trf_trg_url);
					mv.addObject("topics", topics);
					mv.addObject("task_max", task_max);
					mv.addObject("trf_trg_cnn_nm", trf_trg_cnn_nm);
					mv.addObject("hadoop_conf_dir", hadoop_conf_dir);
					mv.addObject("flush_size", flush_size);
					mv.addObject("connector_class", connector_class);
				}
			}
			mv.addObject("connector_type", resultList.get(0).getCnr_cnn_tp_cd());
			mv.setViewName("popup/transferTargetDetailRegForm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 전송 관리 화면을 보여준다.
	 * 
	 * @param
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/transferDetail.do")
	public ModelAndView transferDetail(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		try {
			// 전송 관리 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0017");
			accessHistoryService.insertHistory(historyVO);

			mv.addObject("cnr_id", request.getParameter("cnr_id"));
			mv.addObject("cnr_nm", request.getParameter("cnr_nm"));
			mv.setViewName("transfer/transferDetail");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 전송관리 리스트를 조회한다.
	 * 
	 * @return resultSet
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectTransferDetail.do")
	public @ResponseBody List<TransferDetailVO> selectTransferDetail(
			@ModelAttribute("transferDetailVO") TransferDetailVO transferDetailVO, HttpServletRequest request) {
		List<TransferDetailVO> resultSet = null;
		try {
			transferDetailVO.setCnr_id(Integer.parseInt(request.getParameter("cnr_id")));
			resultSet = treeTransferService.selectTransferDetail(transferDetailVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultSet;

	}

	/**
	 * Database 매핑작업 팝업 화면을 보여준다.
	 * 
	 * @param
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/popup/transferMappingRegForm.do")
	public ModelAndView transferMappingRegForm(@ModelAttribute("dbServerVO") DbServerVO dbServerVO,
			@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<DbServerVO> resultSet = null;
		List<TransferDetailMappingVO> result = null;
		try {
			// Database 매핑팝업 이력 남기기 수정
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0018");
			accessHistoryService.insertHistory(historyVO);

			result = treeTransferService.selectTransferMapping(Integer.parseInt(request.getParameter("trf_trg_id")));
			if (result.size() > 0) {
				mv.addObject("result", result);
			}

			resultSet = dbServerManagerService.selectDbServerList(dbServerVO);
			mv.addObject("resultSet", resultSet);
			mv.addObject("trf_trg_id", request.getParameter("trf_trg_id"));
			mv.addObject("cnr_id", request.getParameter("cnr_id"));
			mv.addObject("trf_trg_cnn_nm", request.getParameter("trf_trg_cnn_nm"));
			mv.setViewName("popup/transferMappingRegForm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * DB를 조회한다.
	 * 
	 * @param dbIDbServerVO
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectServerDbLists.do")
	public @ResponseBody List<DbIDbServerVO> selectServerDbLists(
			@ModelAttribute("dbIDbServerVO") DbIDbServerVO dbIDbServerVO, HttpServletRequest request) {
		List<DbIDbServerVO> resultSet = null;
		try {
			String db_svr_nm = request.getParameter("db_svr_nm");
			resultSet = treeTransferService.selectServerDbList(db_svr_nm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultSet;

	}

	/**
	 * 테이블 리스트를 조회한다.
	 * 
	 * @param dbIDbServerVO
	 * @param request
	 * @return
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectMappingTableList.do")
	public @ResponseBody Map<String, Object> selectMappingTableList(HttpServletRequest request) {
		JSONObject serverObj = new JSONObject();
		ClientInfoCmmn cic = new ClientInfoCmmn();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			AES256 dec = new AES256(AES256_KEY.ENC_KEY);
			
			int db_id = Integer.parseInt(request.getParameter("db_id"));
			DbIDbServerVO dbIDbServerVO = (DbIDbServerVO) treeTransferService.selectServerDb(db_id);
	
			AgentInfoVO vo = new AgentInfoVO();
			vo.setIPADR(dbIDbServerVO.getIpadr());
			AgentInfoVO agentInfo =  (AgentInfoVO) cmmnServerInfoService.selectAgentInfo(vo);
			
			if (agentInfo==null){
				return result;
			}
			String IP = dbIDbServerVO.getIpadr();
			int PORT = agentInfo.getSOCKET_PORT();

			serverObj.put(ClientProtocolID.SERVER_NAME, dbIDbServerVO.getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, dbIDbServerVO.getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, dbIDbServerVO.getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, dbIDbServerVO.getDb_nm());
			serverObj.put(ClientProtocolID.USER_ID, dbIDbServerVO.getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, dec.aesDecode(dbIDbServerVO.getSvr_spr_scm_pwd()));
				
			result = cic.tableList_select(serverObj,IP,PORT);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 전송매핑작업을 등록한다.
	 * 
	 * @param transferRelationVO
	 * @param transferMappingVO
	 * @param request
	 * @param historyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/insertTransferMapping.do")
	public @ResponseBody void insertTransferMapping(
			@ModelAttribute("transferRelationVO") TransferRelationVO transferRelationVO,
			@ModelAttribute("transferMappingVO") TransferMappingVO transferMappingVO, HttpServletRequest request,
			@ModelAttribute("historyVO") HistoryVO historyVO) {
		List<ConnectorVO> resultList = null;
		JSONObject result = new JSONObject();
		JSONObject param = new JSONObject();
		try {
			// Database 매핑저장 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0018_01");
			accessHistoryService.insertHistory(historyVO);

			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");
			transferRelationVO.setFrst_regr_id(usr_id);
			transferRelationVO.setLst_mdfr_id(usr_id);
			transferMappingVO.setFrst_regr_id(usr_id);
			transferMappingVO.setLst_mdfr_id(usr_id);

			transferRelationVO.setTrf_trg_id(Integer.parseInt(request.getParameter("trf_trg_id")));
			transferRelationVO.setCnr_id(Integer.parseInt(request.getParameter("cnr_id")));
			transferRelationVO.setDb_id(Integer.parseInt(request.getParameter("db_id")));

			/* 전송대상매핑관계 DELETE */
			treeTransferService.deleteTransferRelation(Integer.parseInt(request.getParameter("trf_trg_mpp_id")));
			/* 전송매핑테이블내역 DELETE */
			treeTransferService.deleteTransferMapping(Integer.parseInt(request.getParameter("trf_trg_mpp_id")));

			/* 전송대상매핑관계 INSERT */
			treeTransferService.insertTransferRelation(transferRelationVO);

			JSONParser jParser = new JSONParser();
			JSONArray jArr = (JSONArray) jParser
					.parse(request.getParameter("rowList").toString().replace("&quot;", "\""));

			String trf_trg_cnn_nm = request.getParameter("trf_trg_cnn_nm");
			String topic = "";

			for (int i = 0; i < jArr.size(); i++) {
				JSONObject jObj = (JSONObject) jArr.get(i);
				String table_name = (String) jObj.get("table_name");
				String table_schema = (String) jObj.get("table_schema");

				transferMappingVO.setTb_engl_nm(table_name);
				transferMappingVO.setScm_nm(table_schema);

				/* 전송매핑테이블내역 INSERT */
				treeTransferService.insertTransferMapping(transferMappingVO);

				if (i > 0) {
					topic += ",";
				}
				topic += trf_trg_cnn_nm + "." + table_schema + "." + table_name;
			}

			resultList = transferService.selectDetailConnectorRegister(Integer.parseInt(request.getParameter("cnr_id")));

			JSONObject serverObj = new JSONObject();
			String strServerIp = resultList.get(0).getCnr_ipadr();
			String strServerPort = Integer.toString(resultList.get(0).getCnr_portno());
			serverObj.put(ClientProtocolID.SERVER_IP, strServerIp);
			serverObj.put(ClientProtocolID.SERVER_PORT, strServerPort);
			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.kafakConnect_select(serverObj, trf_trg_cnn_nm);

			for (int i = 0; i < result.size(); i++) {
				JSONArray data = (JSONArray) result.get("data");
				for (int m = 0; m < data.size(); m++) {
					JSONObject jsonObj = (JSONObject) data.get(m);
					JSONObject hp = (JSONObject) jsonObj.get("hp");

					param.put("strName", (String) hp.get("name"));
					param.put("strConnector_class", (String) hp.get("connector.class"));
					param.put("strTasks_max", (String) hp.get("tasks.max"));
					param.put("strTopics", topic);
					param.put("strHdfs_url", (String) hp.get("hdfs.url"));
					param.put("strHadoop_conf_dir", (String) hp.get("hadoop.conf.dir"));
					param.put("strHadoop_home", (String) hp.get("hadoop.home"));
					param.put("strFlush_size", (String) hp.get("flush.size"));
					param.put("strRotate_interval_ms", (String) hp.get("rotate.interval.ms"));

				}
			}
			/* kafakConnect_update topic 업데이트 */
			cic.kafakConnect_update(serverObj, param);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * bottlewater를 제어한다.
	 * 
	 * @param historyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bottlewaterControl.do")
	public @ResponseBody String bottlewaterControl(
			@ModelAttribute("transferDetailVO") TransferDetailVO transferDetailVO, HttpServletRequest request) {
		try {
			/*
			 * TODO 실행순서 
			 * 1. bottlewater 실행 및 중지 
			 * bw_pid가 0이면 -> 실행 
			 * bw_pid가 0이 아니면-> 중지 
			 * 2. T_TRFTRGCNG_I 테이블에 (실행-> bottlewater pid 인설트, 중지-> bw_pid 0 인설트)
			 */
			AES256 aes = new AES256(AES256_KEY.ENC_KEY);

			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");
			transferDetailVO.setLst_mdfr_id(usr_id);

			int trf_trg_id = Integer.parseInt(request.getParameter("trf_trg_id"));
			int bw_pid = Integer.parseInt(request.getParameter("bw_pid"));

			if (bw_pid == 0) {
				TransferVO transferInfo = (TransferVO) transferService.selectTransferSetting(usr_id);
				 List<BottlewaterVO> dbInfo = treeTransferService.selectBottlewaterinfo(trf_trg_id);

				/* TOPIC */
				String topicTxt = "";
				for (int i = 0; i < dbInfo.size(); i++) {
					topicTxt += dbInfo.get(i).getTrf_trg_cnn_nm() + "." + dbInfo.get(i).getScm_nm() + "." + dbInfo.get(i).getTb_engl_nm();
					if (i != dbInfo.size() - 1) {
						topicTxt += ",";
					}
				}
				System.out.println("TOPIC : " + topicTxt);
				String dbinfoTxt = "--postgres=postgres://" + dbInfo.get(0).getSvr_spr_usr_id() + ":"
						+ aes.aesDecode(dbInfo.get(0).getSvr_spr_scm_pwd()) + "@" + dbInfo.get(0).getIpadr() + ":"
						+ dbInfo.get(0).getPortno() + "/" + dbInfo.get(0).getDb_nm();
				
				/* bottlewater실행 명령어 */
				String strExecTxt = transferInfo.getBw_home() + dbinfoTxt + " --slot=connect --broker="
						+ transferInfo.getKafka_broker_ip() + ":" + transferInfo.getKafka_broker_port()
						+ " --topic-prefix=" + topicTxt + " --allow-unkeyed --on-error=log";
				System.out.println("명령어 : " + strExecTxt);

				int db_id = Integer.parseInt(request.getParameter("db_id"));
				DbIDbServerVO dbIDbServerVO = (DbIDbServerVO) treeTransferService.selectServerDb(db_id);
				DbServerVO schDbServerVO = new DbServerVO();
				schDbServerVO.setDb_svr_id(dbIDbServerVO.getDb_svr_id());
				DbServerVO dbServerVO = (DbServerVO) cmmnServerInfoService.selectServerInfo(schDbServerVO);
				String strIpAdr = dbServerVO.getIpadr();
				AgentInfoVO vo = new AgentInfoVO();
				vo.setIPADR(strIpAdr);
				AgentInfoVO agentInfo = (AgentInfoVO) cmmnServerInfoService.selectAgentInfo(vo);
				
				if (agentInfo==null){
					return "false";
				}
				
				String IP = dbServerVO.getIpadr();
				int PORT = agentInfo.getSOCKET_PORT();
				
				JSONObject jObj = new JSONObject();
				jObj.put(ClientProtocolID.DX_EX_CODE, ClientTranCodeType.DxT013);
				jObj.put(ClientProtocolID.TRF_TRG_ID, "12");
				jObj.put(ClientProtocolID.COMMAND_CODE, ClientProtocolID.RUN);
				jObj.put(ClientProtocolID.EXEC_TXT, strExecTxt);
				JSONObject objList;
				ClientAdapter CA = new ClientAdapter(IP, PORT);
				CA.open();
				objList = CA.dxT013(ClientTranCodeType.DxT013, jObj);
				String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
				String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
				String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);
				String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);
				System.out.println("RESULT_CODE : " + strResultCode);
				System.out.println("ERR_CODE : " + strErrCode);
				System.out.println("ERR_MSG : " + strErrMsg);
				CA.close();

				/* 실행시킨 bwpid가져오기 */
				int current_bwpid = 1;
				transferDetailVO.setBw_pid(current_bwpid);
				treeTransferService.updateBottleWaterBwpid(transferDetailVO);
				return "start";
			} else {
				/* kill -9 pid 명령어 */
				String strExecTxt = "kill -9 " + bw_pid;
				System.out.println(strExecTxt);
				transferDetailVO.setBw_pid(0);
				treeTransferService.updateBottleWaterBwpid(transferDetailVO);
				return "stop";
			}

		} catch (Exception e) {
			return "false";
		}
	}
}
