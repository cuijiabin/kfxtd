package com.xiaoma.kefu.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoma.kefu.model.Style;
import com.xiaoma.kefu.service.StyleService;
import com.xiaoma.kefu.util.Ajax;
import com.xiaoma.kefu.util.MapEntity;
import com.xiaoma.kefu.util.PageBean;

/**
 * *********************************
* @Description: 风格	controller
* @author: wangxingfei
* @createdAt: 2015年4月7日上午9:20:30
**********************************
 */
@Controller
@RequestMapping(value = "style")
public class StyleController {

	private Logger logger = Logger.getLogger(StyleController.class);

	@Autowired
	private StyleService styleService;
	
	/**
	 * 查询所有风格
	* @Description: TODO
	* @param conditions
	* @param pageBean
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	@RequestMapping(value = "find.action", method = RequestMethod.GET)
	public String queryAll(MapEntity conditions, @ModelAttribute("pageBean") PageBean<Style> pageBean) {
		try {
			styleService.getResult(conditions.getMap(), pageBean);
			if (conditions == null || conditions.getMap() == null
					|| conditions.getMap().get("typeId") == null)
				return "style/style";
			else
				return "style/styleList";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("StyleController.queryAll ERROR");
			return "/error500";
		}
	}
	
	
	/**
	 * 编辑
	* @Description: TODO
	* @param model
	* @param styleId
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	@RequestMapping(value = "edit.action", method = RequestMethod.GET)
	public String edit(Model model,Integer styleId) {
		try {
			Style style;
			if(styleId!=null && styleId>0){
				style = styleService.get(styleId);
			}else{
				style = new Style();
			}
			model.addAttribute("style", style);
			return "/style/edit";
		} catch (Exception e) {
			model.addAttribute("error", "对不起出错了");
			return "error500";
		}
	}
	
	/**
	 * 校验风格名称是否存在
	* @Description: TODO
	* @param model
	* @param customer
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	@RequestMapping(value = "validate.action", method = RequestMethod.POST)
	public String validate(Model model, Style style) {
		try {
			Integer isSuccess = styleService.validateName(style);
			if (isSuccess == 0) {
				model.addAttribute("result", Ajax.JSONResult(0, "成功!"));
			} else {
				model.addAttribute("result", Ajax.JSONResult(1, "名称已存在!"));
			}
		} catch (Exception e) {
			model.addAttribute("result", Ajax.JSONResult(1, "失败!"));
		}
		return "resultjson";
	}
	
	/**
	 * 保存风格
	* @Description: TODO
	* @param model
	* @param customer
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	@RequestMapping(value = "save.action", method = RequestMethod.POST)
	public String save(Model model, @ModelAttribute("style") Style style) {
		try {
			Integer id = style.getId();
			int isSuccess = 0;
			if(id==null){
				isSuccess = styleService.create(style);
			}else{
				Style toUpdate = styleService.get(id);
				toUpdate.setName(style.getName());
				isSuccess = styleService.update(toUpdate);
			}
			if (isSuccess > 0) {
				model.addAttribute("result", Ajax.JSONResult(0, "操作成功!"));
			} else {
				model.addAttribute("result", Ajax.JSONResult(1, "操作失败!"));
			}
		} catch (Exception e) {
			model.addAttribute("result", Ajax.JSONResult(1, "操作失败!"));
		}
		return "resultjson";
	}
	
}
