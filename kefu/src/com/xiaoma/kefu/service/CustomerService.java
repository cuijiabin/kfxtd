package com.xiaoma.kefu.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.xiaoma.kefu.dao.CustomerDao;
import com.xiaoma.kefu.model.Customer;
import com.xiaoma.kefu.redis.JedisDao;
import com.xiaoma.kefu.util.PageBean;

/**
 * @author frongji
 * @time 2015年4月1日下午5:33:36
 *
 */

@Service
public class CustomerService {
   
	@Autowired
	private CustomerDao customerDaoImpl;
	
	private Jedis jedis = JedisDao.getJedis();
	
	/**
	 * 查询所有、 条件查询
	 */
   public PageBean<Customer> getResultNameOrPhone(Integer currentPage,Integer pageRecorders,String customerName ,String phone,Long customerId){
	
	Integer totalCount = customerDaoImpl.getAllCustomerCount();
	PageBean<Customer> result = new PageBean<Customer>();
	
	result.setCurrentPage(currentPage);
	result.setPageRecorders(pageRecorders);
	result.setTotalRows(totalCount);
	
	Integer start = result.getStartRow();
	
	  List<Customer> list =customerDaoImpl.getCustomerByConditions(start,pageRecorders,customerName ,phone,customerId);
	result.setObjList(list);
	
	return result;
    }

		/**
		 * 添加
		 */
		public boolean createNewCustomer(Customer customer){
		   return customerDaoImpl.createNewCustomer(customer);
		}
		
		
		/**
		* 修改
		*/
		public boolean updateCustomer(Customer customer){
		  return customerDaoImpl.updateCustomer(customer); 
		}
		
		/**
		 * 删除
		 */
		public boolean deleteCustomerById(Long id){
		boolean flag = true;
		Customer customer = customerDaoImpl.getCustomerById(id);
		if (customer!=null) {
		customer.setStatus(1);
		flag = customerDaoImpl.updateCustomer(customer);
		   }
		    return flag;
		  }

	/**
	 * 查询一条
	 */
		public Customer getCustomerById(long id){
			return customerDaoImpl.getCustomerById(id);
		}
		
		public Long getMaxCustomerId(){
			String maxCustomerId = jedis.get("MaxCustomerId");
			
			//如果缓存中不空，直接取
			if(StringUtils.isNotBlank(maxCustomerId)){
				return Long.valueOf(maxCustomerId);
			}
			
			return customerDaoImpl.getMaxCustomerId();
		}
}
