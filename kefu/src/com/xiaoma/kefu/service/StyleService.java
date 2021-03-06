package com.xiaoma.kefu.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoma.kefu.dao.StyleDao;
import com.xiaoma.kefu.model.AllotRule;
import com.xiaoma.kefu.model.Style;
import com.xiaoma.kefu.util.FileUtil;
import com.xiaoma.kefu.util.PageBean;
import com.xiaoma.kefu.util.SysConst;
import com.xiaoma.kefu.util.SysConst.DeviceType;
import com.xiaoma.kefu.util.SysConst.StylePicName;

/**
 **********************************
* @Description: 风格	业务实现类
* @author: wangxingfei
* @createdAt: 2015年4月7日上午9:21:23
**********************************
 */
@Service
public class StyleService {
	
	@Autowired
	private StyleDao styleDaoImpl;
	
	@Autowired
	private ClientStyleService clientStyleService;//访客端界面
	@Autowired
	private ServiceIconService serviceIconService;//客服图标
	@Autowired
	private InviteIconService inviteIconService;//对话邀请框
	@Autowired
	private AllotRuleService allotRuleService;//分配机制

	public List<Style> findByNameLike(String styleName) {
		return styleDaoImpl.findByNameLike(styleName);
	}
	
	/**
	 * 查询所有风格
	* @Description: TODO
	* @param conditions	查询条件
	* @param pageBean	分页
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	public void getResult(Map<String, String> conditions, PageBean<Style> pageBean) {
		styleDaoImpl.findByCondition(conditions,pageBean);
		
	}
	
	/**
	 * 根据主键id,获取风格
	* @Description: TODO
	* @param styleId
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	public Style get(Integer id) {
		return styleDaoImpl.findById(Style.class, id);
	}
	
	/**
	 * 更新风格
	* @Description: TODO
	* @param style
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	public Integer update(Style style) {
		return styleDaoImpl.update(style);
	}
	
	/**
	 * 校验风格名称	
	* @Description: TODO
	* @param style	
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	public Integer validateName(Style style) {
		return styleDaoImpl.validateName(style);
	}
	
	/**
	 * 初始化风格
	 * 1.创建风格
	 * 2.创建风格下的图标等表
	* @param style
	* @return
	 * @throws Exception 
	* @Author: wangxingfei
	* @Date: 2015年4月19日
	 */
	public Integer initStyle(Style style) throws Exception {
		//创建风格
		style.setCreateDate(new Date());
		Integer num = (Integer) styleDaoImpl.add(style);
		Integer styleId = style.getId();
		
		//访问端界面
		clientStyleService.initClientStyle(styleId);
		
		//客服图标PC
		serviceIconService.initServiceIcon(styleId,DeviceType.PC);
		
		//对话邀请框PC
		inviteIconService.initInviteIcon(styleId,DeviceType.PC);
		
		//客服图标-移动
		serviceIconService.initServiceIcon(styleId,DeviceType.移动);
		
		//对话邀请框-移动
		inviteIconService.initInviteIcon(styleId,DeviceType.移动);
		
		//分配机制
		AllotRule allotRule = new AllotRule();
		allotRule.setStyleId(styleId);
		allotRule.setFirstRule(1);
		allotRule.setSecondRule(1);
		allotRule.setThirdRule(1);
		allotRuleService.create(allotRule);
		
		//创建js文件
		FileUtil.createStyleJsFile(styleId);
		
		return num;
	}
	
	/**
	 * 获取缩略图路径
	* @param styleId	风格id
	* @param type	图片类型
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月28日
	 */
	public String getMinPicPath(Integer styleId,StylePicName type){
		return 
				FileUtil.getStyleSavePath(styleId)
				+ "/"+type.getCode()	//类别
				+ SysConst.MIN_PIC_SUFFIX //缩略图后缀
				+ SysConst.MIN_EXTENSION	//后缀
				;
	}

}