package com.xiaoma.kefu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoma.kefu.dao.BlacklistDao;
import com.xiaoma.kefu.model.Blacklist;
import com.xiaoma.kefu.model.Customer;
import com.xiaoma.kefu.util.PageBean;

/**
 * @author frongji
 * @time 2015年4月2日上午11:23:15
 *
 */
@Service
public class BlacklistService {
   
	  @Autowired
	  private BlacklistDao blacklistDaoImpl;
	  
		/**
		 * 条件查询
		 */
	   public PageBean<Blacklist> getResultByConditions(Integer currentPage,Integer geRecorders,
			Long	customerId,Integer userId,String description){
		
		Integer totalCount = blacklistDaoImpl.getAllBlacklistCount();
		PageBean<Blacklist> result = new PageBean<Blacklist>();
		
		result.setCurrentPage(currentPage);
//		result.setPageRecorders(pageRecorders);
		result.setTotalRows(totalCount);
		
		Integer start = result.getStartRow();
		
//		  List<Customer> list =blacklistDaoImpl.getCustomerByLoginNameOrPhone(start,pageRecorders,customerId ,userId,description);		
	//	  result.setObjList(list);
		
		return result;
	    }
	   

			/**
			 * 添加
			 */
	       public boolean createNewBlacklist(Blacklist blacklist){
	          return blacklistDaoImpl.createNewBlacklist(blacklist);
	       }
	      
	    
		   
		   /**
		    * 修改
		    */
		   public boolean updateBlacklist(Blacklist blacklist){
			  return blacklistDaoImpl.updateBlacklist(blacklist); 
		   }
		   
		    /**
			 * 删除
			 */
		public boolean deleteBlacklistById(Integer id){
			
			return blacklistDaoImpl.deleteBlacklistById(id);
		}
		
		/**
		 * 查询一条
		 */
			public Blacklist getBlacklistById(Integer id){
				return blacklistDaoImpl.getBlacklistByBlacklistId(id);
			}
			

}
