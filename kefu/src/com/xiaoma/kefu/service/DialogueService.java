package com.xiaoma.kefu.service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoma.kefu.cache.CacheFactory;
import com.xiaoma.kefu.cache.CacheMan;
import com.xiaoma.kefu.cache.CacheName;
import com.xiaoma.kefu.cache.CacheUtil;
import com.xiaoma.kefu.dao.DialogueDao;
import com.xiaoma.kefu.dict.DictMan;
import com.xiaoma.kefu.model.AllotRule;
import com.xiaoma.kefu.model.Customer;
import com.xiaoma.kefu.model.Dialogue;
import com.xiaoma.kefu.model.DictItem;
import com.xiaoma.kefu.model.User;
import com.xiaoma.kefu.redis.JedisDao;
import com.xiaoma.kefu.redis.JedisTalkDao;
import com.xiaoma.kefu.util.FileUtil;
import com.xiaoma.kefu.util.JsonUtil;
import com.xiaoma.kefu.util.database.DataBase;
import com.xiaoma.kefu.util.database.DataSet;


/**
 * 对话信息	业务处理类
 * *********************************
* @Description: TODO
* @author: wangxingfei
* @createdAt: 2015年4月3日下午3:27:45
**********************************
 */
@Service("dialogueService")
public class DialogueService {
	private Logger logger = Logger.getLogger(DialogueService.class);
	@Autowired
	private DialogueDao dialogueDaoImpl;
	@Autowired
	private ChatRecordFieldService chatRecordFieldService;//结果展示字段
	@Autowired
	private AllotRuleService allotRuleService;//结果展示字段
	
	/**
	 * 逻辑删除 对话信息
	* @Description: TODO
	* @param ids	1,2,3
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	public int delete4Logic(String ids){
		int num = 0;
		if(StringUtils.isBlank(ids)) return num;
		String[] temp = ids.split(",");
		for(String str : temp){
			Dialogue dialogue = new Dialogue();
			dialogue.setId(Long.valueOf(str));
			num += dialogueDaoImpl.update2Del(dialogue);
		}
		return num;
	}
	
	/**
	 * 物理删除	对话信息
	* @Description: TODO
	* @param ids	1,2,3
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	public int delete(String ids){
		int num = 0;
		if(StringUtils.isBlank(ids)) return num;
		String[] temp = ids.split(",");
		for(String str : temp){
			Dialogue dialogue = new Dialogue();
			dialogue.setId(Long.valueOf(str));
			num += dialogueDaoImpl.delete(dialogue);
		}
		return num;
	}
	
	/**
	 * 回收站信息还原
	* @Description: TODO
	* @param ids	1,2,3
	 * @return 
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	public int restore(String ids){
		int num = 0;
		if(StringUtils.isBlank(ids)) return num;
		String[] temp = ids.split(",");
		for(String str : temp){
			Dialogue dialogue = new Dialogue();
			dialogue.setId(Long.valueOf(str));
			num += dialogueDaoImpl.update2Restore(dialogue);
		}
		return num;
	}
	
	/**
	 * 
	* @Description: 根据id,获取对话信息
	* @param dialogueId
	 * @return 
	* @Author: wangxingfei
	* @Date: 2015年4月7日
	 */
	public Dialogue findById(Long dialogueId) {
		return dialogueDaoImpl.findById(Dialogue.class, dialogueId);
	}
	
	/**
	 * 每天凌晨1点,定时生成前一天的聊天数据
	 * @throws Exception 
	 * @Description: TODO
	* @Author: wangxingfei
	* @Date: 2015年4月7日
	 */
	public void writeExcelByTime() throws Exception{
		Map<String,String> recordFieldMap = chatRecordFieldService.findDefaultMap();
		List<String> title = getDisplayTitle(recordFieldMap);
		List<List<String>> contentList = getContentList(recordFieldMap);
		createExcel(title,contentList);
	}
	
	/**
	 * 生成excel文件
	* @Description: TODO
	* @param title	标题
	* @param contentList	内容
	 * @throws Exception 
	* @Author: wangxingfei
	* @Date: 2015年4月7日
	 */
	private void createExcel(List<String> title, List<List<String>> contentList) throws Exception {
//		String basePath = SystemConfiguration.getInstance().getFileUrl()
//				+"/" + SysConst.EXP_TALK_PATH ;
		String fileName = getFileName();
		String basePath = FileUtil.getExpTalkRootPath(fileName);
		
		String path = basePath + "/" + fileName + ".xlsx";
		File file = new File(path);
		createFileWithDirs(file);//创建目录
		
		XSSFWorkbook book = new XSSFWorkbook();
        FileOutputStream out = new FileOutputStream(file);  
        XSSFSheet sheet = book.createSheet(fileName);
        
        // title样式
        XSSFCellStyle titleStyle = book.createCellStyle();
        titleStyle.setFillForegroundColor((short) 13);// 设置背景色
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        
        //内容样式
		XSSFCellStyle contentStyle = book.createCellStyle();
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		contentStyle.setWrapText(true);// 换行
		
		 //设置默认列宽和列高	...没生效
	    sheet.setDefaultColumnWidth(50);
	    sheet.setDefaultRowHeight((short) 512);
	    
	    //开始set数据	标题
		XSSFRow row0 = sheet.createRow(0);
		for(int i=0;i<title.size();i++){
			XSSFCell cell = row0.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(titleStyle);
		}
		
		//set 内容
		for(int i=0;i<contentList.size();i++){
			XSSFRow row = sheet.createRow(i+1);
			List<String> tempList = contentList.get(i);
			for(int j=0;j<tempList.size();j++){
				XSSFCell cell = row.createCell(j);
				cell.setCellValue(tempList.get(j));
				cell.setCellStyle(contentStyle);
			}
		}
		book.write(out);
		out.flush();
		out.close();
	}

	/**
	 * 获取文件名称(取前一天)
	* @Description: TODO
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月7日
	 */
	private String getFileName() {
		Calendar c = Calendar.getInstance();  
        Date date = new Date();  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day - 1);  
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
        return dayBefore;  
	}

	/**
	 * 获取前一天的结果
	* @Description: TODO
	* @param recordFieldMap
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月7日
	 */
	private List<List<String>> getContentList(Map<String, String> recordFieldMap) {
		String sql = " SELECT t1.id dialogueId,IFNULL(t2.customerName,t1.customerId) customerName "
				+ " , CASE WHEN t2.customerName IS NULL THEN 0 ELSE 1 END hasName,t1.customerId "
				+ " , t1.ipInfo, t1.consultPage, t1.keywords  "
				+ " , case when t1.isWait=1 then '是' else '否' end isWait "
				+ " , t3.name styleId, t1.openType, t1.closeType, IFNULL(t1.waitListId,0) waitListId "
				+ " , t1.deviceType, t1.cardName, t1.maxSpace, t1.scoreType "
				+ " , t1.landingPage, t1.durationTime, t1.btnCode "
				+ " , t1.waitTime, t1.firstTime, t1.beginDate, t1.totalNum  "
				+ " , t2.firstLandingPage, t2.firstVisitSource, t2.updateDate "
				+ " FROM dialogue t1 "
				+ " INNER JOIN customer t2 ON t1.customerId = t2.id " 
				+ " INNER JOIN style t3 on t1.styleId = t3.id "//风格(站点来源)
				+ " WHERE t1.isDel = 0 " 
				+ " AND DATE(t1.beginDate) = DATE_SUB(CURDATE(),INTERVAL 1 DAY) "
				+ " order by t1.beginDate ";
		
		DataSet ds = DataBase.Query(sql);
		List<List<String>> contentList = new ArrayList<List<String>>((int) ds.RowCount);
		for(int i=0;i<ds.RowCount;i++){
			List<String> tempList = new ArrayList<String>(recordFieldMap.size()+1);
			for(Map.Entry<String, String> entry:recordFieldMap.entrySet()){
				tempList.add(ds.getRow(i).getString(entry.getKey()));
			}
			contentList.add(tempList);
		}
		return contentList;
	}

	/**
	* @Description: 根据需要展示字段map,封装个list显示使用
	* @param recordFieldMap
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	private List<String> getDisplayTitle(Map<String, String> recordFieldMap) {
		List<String> list = new ArrayList<String>(recordFieldMap.size()+1);
		for(Map.Entry<String, String> entry:recordFieldMap.entrySet()){
			list.add(entry.getValue());
		}   
		return list;
	}
	
	/**
	 * 如果目录不存在,创建目录
	* @Description: TODO
	* @param f
	* @return
	* @throws IOException
	* @Author: wangxingfei
	* @Date: 2015年4月7日
	 */
	private boolean createFileWithDirs(File f) throws IOException {
		File parentDir = f.getParentFile();
		boolean parentCreated = false;
		if (!parentDir.exists()) {
			parentCreated = parentDir.mkdirs();
		}
		if (parentCreated) {
			return f.createNewFile();
		}
		return false;
	}

	public Long add(Dialogue dialogue){
		return (Long) dialogueDaoImpl.add(dialogue);
	}
	
	/***
	 * 回复方式
	 * @return
	 */
	public List<DictItem> findReplyWayList(){
		try {
			List<DictItem> list = DictMan.getDictList("d_cus_reply_way");
			DictItem dictItem = DictMan.getDictItem("d_sys_param", 9);
			List<DictItem> dList = new ArrayList<DictItem>();
			for(DictItem d : list){
				if(dictItem.getItemName().indexOf(d.getItemCode()) >= 0){
					dList.add(d);
				}
			}
			return dList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}
	/***
	 * 回复方式
	 * @return
	 */
	public List<Dialogue> findDialogByCustomerId(Long customerId){
		try {
			return dialogueDaoImpl.findDialogByCustomerId(customerId);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	/***
	 * 留言信息
	 * @return
	 */
	public List<DictItem> findMessageObject(){
		try {
			List<DictItem> list = DictMan.getDictList("d_cus_reply_obj");
			DictItem dictItem = DictMan.getDictItem("d_sys_param", 10);
			List<DictItem> dList = new ArrayList<DictItem>();
			for(DictItem d : list){
				if(dictItem.getItemName().indexOf(d.getItemCode()) >= 0){
					dList.add(d);
				}
			}
			return dList;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	/***
	 * 留言信息
	 * @return
	 */
	public List<DictItem> findInfoList(){
		try {
			List<DictItem> list = DictMan.getDictList("d_cus_info");
			DictItem dictItem = DictMan.getDictItem("d_sys_param", 7);
			List<DictItem> dList = new ArrayList<DictItem>();
			for(DictItem d : list){
				if(dictItem.getItemName().indexOf(d.getItemCode()) >= 0){
					dList.add(d);
				}
			}
			return dList;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	public Dialogue getLastBycustomerIdAndUserId(Long customerId,Integer userId){
		if(customerId == null || userId == null){
			return null;
		}
		
		return dialogueDaoImpl.getLastBycustomerIdAndUserId(customerId, userId);
	}
	
	public Boolean update(Dialogue dialogue){
		Integer id = dialogueDaoImpl.update(dialogue);
		
		return (id >= 0);
	}

	
	/**
	 * 根据客户,分配对应的客服
	 * 
	 * @param customer
	 * @return
	 */
	public String allocateCcnIdByCustomer(Long customerId,Integer styleId){
		
		try {
			//如果当前风格下没有客服，直接返回。
			List<Integer> userIds = CacheMan.getOnlineUserIdsByStyleId(styleId);
			if (CollectionUtils.isEmpty(userIds)) {
				return null;
			}
			
			//如果所有客服都满员，需要直接进入等待队列。返回空。
			List<User> userList = null;
			for(Integer userId : userIds){
				int count = JedisTalkDao.getLastReceiveCount(userId+"");
				if(count > 0){
					if(userList == null)
						userList = new ArrayList<User>();
					userList.add((User)CacheMan.getObject(CacheName.SUSER, userId));
				}
			}
			if(userList == null || userList.size() < 1)
				return null;
			if(userList.size() == 1){
				return userList.get(0).getId()+"";
			}
			//如果有客服，获取风格规则
			AllotRule allotRule = allotRuleService.getByStyleId(styleId);
			//如果没规则，分配当前可接通数最大的一个
			if(allotRule == null){
				Integer max = 0;
				String allocateUserId = null;
				for (Integer id : userIds) {
					String userId = id.toString();
					Integer result = JedisTalkDao.getLastReceiveCount(userId+"");
					if (result > max) {
						max = result;
						allocateUserId = userId;
					}
				}
				return JedisTalkDao.getUserCcnList(allocateUserId).get(0);
			}
			//如果有规则,先判断第一个规则
			if(allotRule.getFirstRule() != null && allotRule.getFirstRule()==1){
				//获取客户的对话记录，如果有，则获取对应的客服，判断客服是否在当前等待中。如果该客服需等待，则不返回。
				List<Dialogue> list = findDialogByCustomerId(customerId);
				if(list != null && list.size()>0){
					Dialogue d = list.get(0);
					if (userIds.contains(d.getUserId())) {
						return String.valueOf(d.getUserId());
					}
				}
			}
			//如果第一个不满足,判断第二个
			if(allotRule.getSecondRule() != null ){
				if(allotRule.getSecondRule() == 2){
					int max = 0;
					User user = null;
					for(User u : userList){
						if (u.getListenLevel()>max)
							user = u;
					}
					return user.getId()+"";
				}else{
					int min = 0;
					User user = null;
					for(User u : userList){
						int count = JedisTalkDao.getReceiveCount(u.getId()+"");
						if (count<min)
							user = u;
					}
					return user.getId()+"";
				}
			}
			
			//第三种规则 暂时到第二级就筛选出人了。
			
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		} 
		return null;
		
	}
	public List<Integer> getOnlineUserIdsByStyleId(Integer styleId) {
		try {
			List<Integer> list = (List<Integer>)CacheMan.getObject(CacheName.ONLINE_USER_STYLEID, styleId);
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}
			Set<Integer> userIds = new HashSet<Integer>(list);
			List<String> ids = JedisTalkDao.getSwitchList();
			Set<Integer> onUserIds = JsonUtil.convertString2IntegerSet(ids);
			if(CollectionUtils.isEmpty(onUserIds)){
				return null;
			}
			onUserIds.retainAll(userIds);
			return new ArrayList<Integer>(onUserIds);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}
}
