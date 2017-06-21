package com.k4m.dx.tcontrol.mypage.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.k4m.dx.tcontrol.admin.accesshistory.service.AccessHistoryService;
import com.k4m.dx.tcontrol.cmmn.CmmnUtils;
import com.k4m.dx.tcontrol.common.service.HistoryVO;
import com.k4m.dx.tcontrol.login.service.UserVO;
import com.k4m.dx.tcontrol.mypage.service.MyPageService;

/**
 * Mypage 컨트롤러 클래스를 정의한다.
 *
 * @author 김주영
 * @see
 * 
 *      <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2017.06.20   김주영 최초 생성
 *      </pre>
 */

@Controller
public class MypageController {

	@Autowired
	private MyPageService myPageService;
	
	@Autowired
	private AccessHistoryService accessHistoryService;
	
	
	/**
	 * 개인정보수정 화면을 보여준다.
	 * 
	 * @param historyVO
	 * @param request
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/myPage.do")
	public ModelAndView myPage(@ModelAttribute("historyVO") HistoryVO historyVO,@ModelAttribute("userVo") UserVO userVo,HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<UserVO> result = null;
		try {
			
			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");

			// 개인정보수정 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0042");
			accessHistoryService.insertHistory(historyVO);
			
			
			result = myPageService.selectDetailMyPage(usr_id);
			mv.addObject("usr_nm", result.get(0).getUsr_nm());
			mv.addObject("aut_id", result.get(0).getAut_id());
			mv.addObject("bln_nm", result.get(0).getBln_nm());
			mv.addObject("dept_nm", result.get(0).getDept_nm());
			mv.addObject("pst_nm", result.get(0).getPst_nm());
			mv.addObject("cpn", result.get(0).getCpn());
			mv.addObject("rsp_bsn_nm", result.get(0).getRsp_bsn_nm());
			mv.addObject("usr_expr_dt", result.get(0).getUsr_expr_dt());
			
			mv.setViewName("mypage/myPage");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;

	}
	
	
	/**
	 * 패스워드변경 팝업을 보여준다.
	 * 
	 * @param request
	 * @param historyVO
	 * @throws Exception
	 */
	@RequestMapping(value = "/popup/pwdRegForm.do")
	public ModelAndView connectorReg(HttpServletRequest request,@ModelAttribute("historyVO") HistoryVO historyVO) {
		ModelAndView mv = new ModelAndView();
		try {
			//패스워드변경 팝업 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0043");
			accessHistoryService.insertHistory(historyVO);
					
			mv.setViewName("popup/pwdRegForm");	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 개인정보수정
	 * 
	 * @param userVo
	 * @param historyVO
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateMypage.do")
	public @ResponseBody void updateMypage(@ModelAttribute("userVo") UserVO userVo,@ModelAttribute("historyVO") HistoryVO historyVO,HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			String usr_id = (String)session.getAttribute("usr_id");
			userVo.setLst_mdfr_id(usr_id);
			
			myPageService.updateMypage(userVo);
			
			//개인정보수정 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0042_01");
			accessHistoryService.insertHistory(historyVO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
