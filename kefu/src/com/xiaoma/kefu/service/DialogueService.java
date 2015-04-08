package com.xiaoma.kefu.service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoma.kefu.cache.CacheName;
import com.xiaoma.kefu.dao.DialogueDao;
import com.xiaoma.kefu.model.Dialogue;
import com.xiaoma.kefu.util.PropertiesUtil;
import com.xiaoma.kefu.util.SysConst;
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
@Service
public class DialogueService {
	
	@Autowired
	private DialogueDao dialogueDaoImpl;
	@Autowired
	private ChatRecordFieldService chatRecordFieldService;//结果展示字段
	
	/**
	 * 逻辑删除 对话信息
	* @Description: TODO
	* @param list
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	public void delete4Logic(List<Dialogue> list){
		for(Dialogue dialogue : list){
			dialogueDaoImpl.update2Del(dialogue);
		}
	}
	
	/**
	 * 物理删除	对话信息
	* @Description: TODO
	* @param list
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	public void delete(List<Dialogue> list){
		for(Dialogue dialogue : list){
			dialogueDaoImpl.delete(dialogue);
		}
	}
	
	/**
	 * 回收站信息还原
	* @Description: TODO
	* @param list
	* @Author: wangxingfei
	* @Date: 2015年4月3日
	 */
	public void restore(List<Dialogue> list){
		for(Dialogue dialogue : list){
			dialogueDaoImpl.update2Restore(dialogue);
		}
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
		String basePath = PropertiesUtil.getProperties(CacheName.FILEROOT)
				+File.separator + SysConst.EXP_TALK_PATH ;
		String fileName = getFileName();
		String path = basePath + File.separator + fileName + ".xlsx";
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
		String sql = " SELECT t1.id dialogueId,IFNULL(t2.customerName,t1.customerId) customerId "
				+ " , t1.ipInfo, t1.consultPage, t1.keywords  "
				+ " , t1.styleId, t1.openType, t1.closeType, t1.isWait, t1.waitListId "
				+ " , t1.deviceType, t1.cardName, t1.maxSpace, t1.scoreType "
				+ " , t1.landingPage, t1.keywords, t1.durationTime, t1.btnCode "
				+ " , t1.waitTime, t1.firstTime, t1.beginDate, t1.totalNum  "
				+ " , t2.firstLandingPage, t2.firstVisitSource, t2.updateDate "
				+ " FROM dialogue t1 "
				+ " INNER JOIN customer t2 ON t1.customerId = t2.id " 
				+ " WHERE t1.isDel = 0 " ;
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


}
