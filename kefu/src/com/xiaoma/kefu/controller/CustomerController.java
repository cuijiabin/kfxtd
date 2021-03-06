package com.xiaoma.kefu.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaoma.kefu.comet4j.DialogueInfo;
import com.xiaoma.kefu.model.Customer;
import com.xiaoma.kefu.model.Dialogue;
import com.xiaoma.kefu.model.MessageRecords;
import com.xiaoma.kefu.model.Style;
import com.xiaoma.kefu.redis.JedisTalkDao;
import com.xiaoma.kefu.service.CustomerService;
import com.xiaoma.kefu.service.DialogueService;
import com.xiaoma.kefu.service.MessageRecordsService;
import com.xiaoma.kefu.service.StyleService;
import com.xiaoma.kefu.util.Ajax;
import com.xiaoma.kefu.util.MapEntity;
import com.xiaoma.kefu.util.PageBean;

/**
 * @author frongji
 * @time 2015年4月2日上午9:54:59 访客管理--控制层
 */
@Controller
@RequestMapping(value = "customer")
public class CustomerController {
	private Logger logger = Logger.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;
	@Autowired
	private DialogueService dialogueService;// 对话信息
	@Autowired
	private MessageRecordsService messageRecordsService;// 留言信息
	@Autowired
	private StyleService styleService;
	
	/**
	 * 编辑用户信息页面(用于 对话中 添加 )
	* @param model
	* @param customerId	客户id
	* @param currentCcnId	连接id
	* @return
	* @Author: wangxingfei
	* @Date: 2015年5月7日
	 */
	@RequestMapping(value = "editCus4Dia.action", method = RequestMethod.GET)
	public String editCus4Dia(Model model, String customerId, String currentCcnId) {
		try {
			Customer customer = customerService.getCustomerById(Long.valueOf(customerId));
			
			if(!JedisTalkDao.isUser(currentCcnId)){
		    	 logger.error("传入的不是用户连接点 currentCcnId："+currentCcnId);
		     }
			DialogueInfo diaInfo = JedisTalkDao.getDialogueInfo(customerId, currentCcnId);
			if(diaInfo.getStyleId()!=null){//目前有时候 styleid会为空
				Style style = styleService.get(diaInfo.getStyleId());
				model.addAttribute("styleName", style!=null?style.getName():"");
			}
			model.addAttribute("customer", customer);
			model.addAttribute("dialogue", diaInfo);//对话临时信息
			return "/customer/editCus";
		} catch (Exception e) {
			logger.error("editCus4Dia",e);
			model.addAttribute("error", "对不起出错了");
			return "/error";
		}
	}
	
	/**
	 * 获取访客信息, 用于 对话框中展示
	* @param customerId
	* @param currentCcnId
	* @return
	* @Author: wangxingfei
	* @Date: 2015年5月7日
	 */
	@RequestMapping(value = "getCustomerInfo.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCustomerInfo(String customerId,String currentCcnId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Customer customer = customerService.getCustomerById(Long.valueOf(customerId));
			if(customer!=null && StringUtils.isBlank(customer.getCustomerName())){
				customer.setCustomerName("--");
			}
			
			if(!JedisTalkDao.isUser(currentCcnId)){
		    	 logger.error("传入的不是用户连接点 currentCcnId："+currentCcnId);
		    }
			DialogueInfo diaInfo = JedisTalkDao.getDialogueInfo(customerId, currentCcnId);
//			diaInfo.setConsultPage(customer.getId().toString());
			result.put("result", 0);
			result.put("customer", customer);
			result.put("diaInfo", diaInfo);
		} catch (Exception e) {
			logger.error("updateCus", e);
			result.put("result", 1);
			result.put("msg", "获取失败!");
		}
		return result;
	}
	
	/**
	 * 查询所有、条件查询
	 * 
	 * @param conditions
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value = "find.action", method = RequestMethod.GET)
	public String queryAll(MapEntity conditions, Model model,String beginDate,String endDate,PageBean pageBean) {
		try {
			List list = customerService.getResultByCon(conditions.getMap(),beginDate,endDate,pageBean);
		    model.addAttribute("beginDate", initDate(beginDate));
			model.addAttribute("endDate", initDate(endDate));
			model.addAttribute("list", list);
			model.addAttribute("pageBean", pageBean);
			
			if (conditions == null || conditions.getMap() == null
					|| conditions.getMap().get("typeId") == null){
				return "customer/customer";
			}else {
				return "customer/customerList";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "/error500";
		}
	}

	/**
	 * 查询所有
	 * 
	 * @param conditions
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value = "getLeft.action", method = RequestMethod.GET)
	public String getLeft(Model model, Integer funcId) {

		return "customer/left";
	}

	/**
	 * 添加前检查访客表中是否存在该访客
	 * 
	 * @param model
	 * @param messageType
	 * @return
	 */
	@RequestMapping(value = "check.action", method = RequestMethod.GET)
	public String checkCus(Model model, Long id) {
		try {
			if (id == null) {
				model.addAttribute("result", Ajax.toJson(1, "缺少参数，请重新提交！"));
				return "resultjson";
			}
			Integer count = customerService.checkCus(id); // 查看是否存在该访客
			System.out.println(count);
			if (count != null && count == 0) {
				model.addAttribute("result", Ajax.toJson(0, "该访客信息不存在,不可以添加"));
			} else {
				model.addAttribute("result", Ajax.toJson(1, "该访客信息存在，可以添加"));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			model.addAttribute("result", Ajax.toJson(1, "查询出错啦，请刷新后重试！"));
		}
		return "resultjson";
	}
	/**
	 * 添加
	 */

	@RequestMapping(value = "add.action", method = RequestMethod.GET)
	public String addCustomer(HttpSession session, Model model,
			Customer customer) {

		try {

			boolean isSuccess = customerService.createNewCustomer(customer);
			if (isSuccess) {
				model.addAttribute("result", Ajax.JSONResult(0, "添加成功!"));
			} else {
				model.addAttribute("result", Ajax.JSONResult(1, "添加失败!"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			model.addAttribute("result", Ajax.JSONResult(1, "添加失败!"));
		}

		return "resultjson";
	}

	/**
	 * 修改
	 * 
	 * @return
	 */
	@RequestMapping(value = "update.action", method = RequestMethod.POST)
	public String updateCustomer(Model model, Customer customer) {

		try {

			Customer toUpdate = customerService.getCustomerById(customer
					.getId());
			toUpdate.setPhone(customer.getCustomerName());

			boolean isSuccess = customerService.updateCustomer(toUpdate);

			if (isSuccess) {
				model.addAttribute("result", Ajax.JSONResult(0, "修改成功!"));
			} else {
				model.addAttribute("result", Ajax.JSONResult(1, "修改失败!"));
			}

		} catch (Exception e) {
			model.addAttribute("result", Ajax.JSONResult(1, "修改失败!"));
		}
		return "XX";

	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "delete.action", method = RequestMethod.GET)
	public String deleteCustomer(Model model, Long id) {

		boolean isSuccess = customerService.deleteCustomerById(id);
		String message = "failure";
		Integer code = -1;

		if (isSuccess) {
			message = "success";
			code = 200;
		}
		model.addAttribute("message", message);
		model.addAttribute("code", code);

		return "iews/message";

	}

	/**
	 * 编辑用户信息页面(用于记录中心-聊天记录)
	 * 
	 * @Description: TODO
	 * @param model
	 * @param customerId
	 * @param dialogueId
	 * @return
	 * @Author: wangxingfei
	 * @Date: 2015年4月10日
	 */
	@RequestMapping(value = "editCus.action", method = RequestMethod.GET)
	public String editCus(Model model, String customerId, String dialogueId) {
		try {
			Customer customer = customerService.getCustomerById(Long
					.valueOf(customerId));
			Dialogue dialogue = dialogueService.findById(Long
					.valueOf(dialogueId));
			Style style = styleService.get(dialogue.getStyleId());
			model.addAttribute("styleName", style!=null?style.getName():"");
			model.addAttribute("customer", customer);
			model.addAttribute("dialogue", dialogue);
			return "/customer/editCus";
		} catch (Exception e) {
			logger.error("editCus",e);
			model.addAttribute("error", "对不起出错了");
			return "error500";
		}
	}

	/**
	 * 更新用户信息,用于记录中心
	 * 
	 * @Description: TODO
	 * @param model
	 * @param customer
	 * @return
	 * @Author: wangxingfei
	 * @Date: 2015年4月12日
	 */
	@RequestMapping(value = "updateCus.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateCus(Model model, Customer customer) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			Customer toUpdate = customerService.getCustomerById(customer
					.getId());
			if(StringUtils.isNotBlank(customer.getCustomerName())){
				toUpdate.setCustomerName(customer.getCustomerName());
			}
			toUpdate.setPhone(customer.getPhone());
			toUpdate.setEmail(customer.getEmail());
			toUpdate.setRemark(customer.getRemark());
			boolean isSuccess = customerService.updateCustomer(toUpdate);
			if (isSuccess) {
				result.put("result", 0);
				result.put("customer", toUpdate);
			} else {
				result.put("result", 1);
				result.put("msg", "修改失败!");
			}
		} catch (Exception e) {
			logger.error("updateCus", e);
			result.put("result", 1);
			result.put("msg", "修改失败!");
		}
//		return "resultjson";
		return result;
	}

	/**
	 * 编辑用户信息页面(用于记录中心留言记录)
	 * 
	 * @Description: TODO
	 * @param model
	 * @param customerId
	 * @param dialogueId
	 * @return
	 * @Author: wangxingfei
	 * @Date: 2015年4月10日
	 */
	@RequestMapping(value = "editCus4Msg.action", method = RequestMethod.GET)
	public String editCus4Msg(Model model, String customerId, Integer msgId) {
		try {
			Customer customer = customerService.getCustomerById(Long
					.valueOf(customerId));
			MessageRecords msg = messageRecordsService.findById(Integer
					.valueOf(msgId));
			Style style = styleService.get(msg.getStyleId());
			model.addAttribute("styleName", style!=null?style.getName():"");
			model.addAttribute("customer", customer);
			model.addAttribute("dialogue", msg);// 和聊天记录用一个
			return "/customer/editCus";
		} catch (Exception e) {
			logger.error("editCus4Msg",e);
			model.addAttribute("error", "对不起出错了");
			return "error500";
		}
	}

	/**
	 * 初始化查询日期, 如果为空,则默认当天
	 * 
	 * @Description: TODO
	 * @param beginDate
	 * @return
	 * @Author:frongji
	 * @Date: 2015年4月14日
	 */
	private String initDate(String beginDate) {
		String result = beginDate;
		if (StringUtils.isBlank(beginDate)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			result = sdf.format(new Date());
		}
		return result;
	}
	/**
	 * 导出报表的方法
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	 @RequestMapping(value= "exportExcel.action" ,method=RequestMethod.GET)  
	 public void exportExcel(HttpServletRequest request, HttpServletResponse response, Model model,
			 String beginDate,String endDate,String customerName,String id,String phone, 
			 String styleName,String consultPage,String keywords){  
		
	     // 生成提示信息，  
	     response.setContentType("application/vnd.ms-excel");  
	     String codedFileName = null;  
	     OutputStream fOut = null;  
	     try  
	     {  
	    	 Date d = new Date();
	    	 SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	         // 进行转码，使其支持中文文件名  
	         codedFileName = java.net.URLEncoder.encode("中文", "UTF-8");  
	         response.setHeader("content-disposition", "attachment;filename=" + "customer_"+format.format(d)  + ".xls");  
	         // response.addHeader("Content-Disposition", "attachment;   filename=" + codedFileName + ".xls");  
	         // 产生工作簿对象  
	         HSSFWorkbook workbook = new HSSFWorkbook();  
	         //产生工作表对象  
	         HSSFSheet sheet = workbook.createSheet();  
	         HSSFRow rows = sheet.createRow(0);//创建一行  
	         HSSFCell cell0 = rows.createCell(0);//创建一列  
	          cell0.setCellValue("访问时间"); 
	         HSSFCell cell1 = rows.createCell(1);//创建一列  
            cell1.setCellValue("风格"); 
            HSSFCell cell2 = rows.createCell(2);//创建一列  
            cell2.setCellValue("客户编号");  
            HSSFCell cell3 = rows.createCell(3);//创建一列  
            cell3.setCellValue("客户姓名");  
            HSSFCell cell4 = rows.createCell(4);//创建一列  
            cell4.setCellValue("联系方式");  
            HSSFCell cell5 = rows.createCell(5);//创建一列  
            cell5.setCellValue("咨询页面");  
            HSSFCell cell6 = rows.createCell(6);//创建一列  
            cell6.setCellValue("关键词");  
            HSSFCell cell7 = rows.createCell(7);//创建一列  
            cell7.setCellValue("备注");  
            
	         List list = customerService.getResultByConExl(beginDate,endDate,customerName,id,phone,styleName,consultPage,keywords);
	        
	         
	         SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         for (int i = 0; i < list.size() ; i++){
	        	 
	         Object [] obj = (Object[])list.get(i);
	         Customer customer =(Customer)obj[0];
	         Dialogue dialogue = (Dialogue)obj[1];
	         String createDate = formatter.format(customer.getCreateDate());//创建当前条黑名单的时间
	        	 rows = sheet.createRow(i+1);//创建一行  
		         cell0 = rows.createCell(0);//创建一列  
	             cell0.setCellValue(createDate); 
	             cell1 = rows.createCell(1);//创建一列  
	             cell1.setCellValue(customer.getStyleName());  
	             cell2 = rows.createCell(2);//创建一列  
	             cell2.setCellValue(Long.toString(customer.getId()));  
	             cell3 = rows.createCell(3);//创建一列  
	             cell3.setCellValue(customer.getCustomerName());  
	             cell4 = rows.createCell(4);//创建一列  
	             cell4.setCellValue(customer.getPhone());  
	             cell5 = rows.createCell(5);//创建一列  
	             cell5.setCellValue(dialogue.getConsultPage());  
	             cell6 = rows.createCell(6);//创建一列  
	             cell6.setCellValue(dialogue.getKeywords());  
	             cell7 = rows.createCell(7);//创建一列  
	             cell7.setCellValue(customer.getRemark());  
	         }  
	         fOut = response.getOutputStream();  
	         workbook.write(fOut);  
	     
	     }
	     catch (UnsupportedEncodingException e1){
	    	 e1.printStackTrace();
	     }catch (Exception e){
	    	 e.printStackTrace();
	     }  
	     finally  
	     {  
	         try  
	         {  
	             fOut.flush();  
	             fOut.close();  
	         }  
	         catch (IOException e)  
	         {}  
	     }  
	     System.out.println("文件生成...");  
	 }  
	
}
