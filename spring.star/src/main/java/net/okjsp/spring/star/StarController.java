package net.okjsp.spring.star;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Profile("star")
public class StarController {

	@Autowired
	StarService starService;
	
	@RequestMapping(value="/addStar", method=RequestMethod.POST, headers="Accept=application/xml, application/json ")
	public @ResponseBody Map<String, Object> addStar(HttpServletRequest request,
			HttpServletResponse response, @RequestBody Map<String, Object> reqMap, Map<String, Object> resMap) {
		int i = (Integer) reqMap.get("contentsId");
		double d = (Double) reqMap.get("starPoint");
		int userid = (Integer) reqMap.get("userid");
		double av = 0d;
		try {
			av = starService.add(i, d, userid);
			resMap.put("res_code", "200");
			resMap.put("average", av);
		} catch (DuplicateVoteException e) {
			resMap.put("res_code", "400");
			resMap.put("res_msg", "이미 투표 하였습니다.");
		} catch (Exception e) {
			resMap.put("res_code", "600");
			resMap.put("res_msg", "서버 오류가 발생하였습니다.");
		}
		return resMap;
	}
	
	@RequestMapping(value="/getAverageOfItem", method=RequestMethod.POST, headers="Accept=application/xml, application/json ")
	public @ResponseBody Map<String, Object> getAverageOfItem(HttpServletRequest request,
			HttpServletResponse response, @RequestBody Map<String, Object> reqMap, Map<String, Object> resMap) {
		int i = (Integer) reqMap.get("contentsId");
		double av = 0d;
		try {
			av = starService.getAverageOfItem(i);
			resMap.put("res_code", "200");
			resMap.put("average", av);
		} catch (Exception e) {
			resMap.put("res_code", "600");
			resMap.put("res_msg", "서버 오류가 발생하였습니다.");
		}
		return resMap;
	}
}
