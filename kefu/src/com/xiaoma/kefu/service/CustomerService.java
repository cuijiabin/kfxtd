package com.xiaoma.kefu.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.xiaoma.kefu.cache.CacheName;
import com.xiaoma.kefu.dao.CustomerDao;
import com.xiaoma.kefu.model.Customer;
import com.xiaoma.kefu.redis.JedisDao;
import com.xiaoma.kefu.util.CookieUtil;
import com.xiaoma.kefu.util.DesUtil;
import com.xiaoma.kefu.util.PageBean;
import com.xiaoma.kefu.util.PropertiesUtil;

/**
 * @author frongji
 * @time 2015年4月1日下午5:33:36
 *
 */

@Service("customerService")
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
		 * 添加
		 */
		public Long insert(Customer customer){
			
		   customer.setCreateDate(new Date());
		   return customerDaoImpl.insert(customer);
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
		
		/**
		 * 根据请求获取或者创建用户
		 * @param request
		 * @return
		 * @throws Exception
		 */
		public Customer genCustomer(HttpServletRequest request) throws Exception{
			Cookie cookie = CookieUtil.getCustomerCookie(request);
			
			Customer customer = new Customer();
			Long customerId;
			if (cookie == null) {
				//创建一个新的Customer
			    customerId = this.insert(customer);
			} else {
				String id = DesUtil.decrypt(cookie.getValue(),PropertiesUtil.getProperties(CacheName.SECRETKEY));
				customerId = Long.valueOf(id);
				cookie.setMaxAge(5 * 365 * 24 * 60 * 60);
			}
			customer = this.getCustomerById(customerId);
			return customer;
		}
}
