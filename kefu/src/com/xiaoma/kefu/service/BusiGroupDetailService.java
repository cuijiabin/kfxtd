package com.xiaoma.kefu.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoma.kefu.cache.CacheMan;
import com.xiaoma.kefu.cache.CacheName;
import com.xiaoma.kefu.dao.BusiGroupDetailDao;
import com.xiaoma.kefu.model.BusiGroupDetail;
import com.xiaoma.kefu.model.Department;
import com.xiaoma.kefu.model.User;

/**
 **********************************
* @Description: 业务分组明细	业务实现类
* @author: wangxingfei
* @createdAt: 2015年4月7日上午9:21:23
**********************************
 */
@Service("busiGroupDetailService")
public class BusiGroupDetailService {
	
	@Autowired
	private BusiGroupDetailDao busiGroupDetailDaoDaoImpl;
	
	/**
	 * 获取分组下的明细list
	* @Description: TODO
	* @param groupId
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月13日
	 */
	public List<BusiGroupDetail> findByGroupId(Integer groupId){
		return busiGroupDetailDaoDaoImpl.findByGroupId(groupId);
	}
	
	/**
	 * 根据分组id,删除
	* @param id
	* @Author: wangxingfei
	* @Date: 2015年4月20日
	 */
	public void deleteByGroupId(Integer groupId) {
		busiGroupDetailDaoDaoImpl.deleteByGroupId(groupId);
	}
	
	/**
	 * 根据分组id,用户id,用户类型, 查找当前对象
	* @param busiGroupDetail
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月21日
	 */
	public BusiGroupDetail getByModel(BusiGroupDetail busiGroupDetail) {
		return busiGroupDetailDaoDaoImpl.getByModel(busiGroupDetail);
	}
	
	/**
	 * 创建
	* @param busiGroupDetail
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月21日
	 */
	public Integer create(BusiGroupDetail busiGroupDetail) {
		busiGroupDetail.setCreateDate(new Date());
		return (Integer) busiGroupDetailDaoDaoImpl.add(busiGroupDetail);
	}

	/**
	 * 删除
	* @param busiGroupDetail
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月21日
	 */
	public int delete(BusiGroupDetail busiGroupDetail) {
		return busiGroupDetailDaoDaoImpl.delete(busiGroupDetail);
	}
	
	/**
	 * 保存分流明细
	* @param data	15:0,16:1	id:是否勾选 0否,1是
	* @Author: wangxingfei
	* @Date: 2015年4月21日
	 */
	public void saveDetail(String data) {
		if(StringUtils.isNotBlank(data)){
			String[] strs = data.split(",");
			for(String temp : strs){
				String[] ss = temp.split(":");
				BusiGroupDetail detail = new BusiGroupDetail();
				detail.setId(Integer.valueOf(ss[0]));
				detail.setIsReception(Integer.valueOf(ss[1]));
				busiGroupDetailDaoDaoImpl.updateIsRece(detail);
			}
			if(strs.length>0){
				String[] st = strs[0].split(":");
				BusiGroupDetail bgdTemp = busiGroupDetailDaoDaoImpl.findById(BusiGroupDetail.class, Integer.valueOf(st[0]));
				CacheMan.remove(CacheName.ONLINE_USER_STYLEID, bgdTemp.getStyleId());
			}
		}
	}
	
	/**
	 * 员工离职,删除业务分组中此员工信息
	* @param userId
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月22日
	 */
	public int deleteByUserId(Integer userId){
		return busiGroupDetailDaoDaoImpl.deleteByUserOrDept(userId, 1);
	}
	
	/**
	 * 删除部门, 删除业务分组中部门信息
	* @param deptId
	* @return
	* @Author: wangxingfei
	* @Date: 2015年4月22日
	 */
	public int deleteByDeptId(Integer deptId){
		return busiGroupDetailDaoDaoImpl.deleteByUserOrDept(deptId,2);
	}
	

	/**
	 * 根据风格id获取userId列表
	 * 如果分流中是部门,则取出部门下所有在职人员
	 * @param styleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findUserIdsByStyleId(Integer styleId){
		List<BusiGroupDetail> list = busiGroupDetailDaoDaoImpl.findByStyleId(styleId);
		List<Integer> userIdList = new ArrayList<Integer>();
		for(BusiGroupDetail detail : list){
			//如果是勾选的
			if(detail.getIsReception()!=null && detail.getIsReception() == 1){
				if(detail.getUserType()==1){//如果是用户
					userIdList.add(detail.getUserId());
				}else if(detail.getUserType()==2){//如果是部门
					List<User> tempList = (List<User>) CacheMan.getList(CacheName.DEPT_USER_ON_LIST, detail.getUserId(), User.class);
					for(User user : tempList){
						userIdList.add(user.getId());
					}
				}
			}
		}
		return userIdList;
	}
	
	/**
	 * 根据用户id获取有效风格列表
	 * @param userId
	 * @return
	 */
	public List<Integer> getStyleIdsByuser(User user){
		return busiGroupDetailDaoDaoImpl.getStyleIdsByuser(user.getId(),user.getDeptId());
	}
	
	/**
	 * 更新工号名称,  用于工号更新后,业务分组更新冗余字段
	* @param user
	* @return	受影响数量
	* @Author: wangxingfei
	* @Date: 2015年5月8日
	 */
	public Integer updateUserCardName(User user){
		Integer resultNum = 0; 
		List<BusiGroupDetail> list = busiGroupDetailDaoDaoImpl.findByUserId(user.getId(),1);
		if(list!=null && list.size()>0){
			//如果有记录,并且名称变更
			if(user.getCardName()!=null && list.get(0).getCardName()!=null && !user.getCardName().equals(list.get(0).getCardName())){
				resultNum = busiGroupDetailDaoDaoImpl.updateCardName(user.getId(),user.getCardName(),1);
			}
		}
		return resultNum;
	}
	
	/**
	 * 更新部门名称,	用于部门名称更新后,业务分组更新冗余字段
	* @param dept
	* @return	受影响数量
	* @Author: wangxingfei
	* @Date: 2015年5月8日
	 */
	public Integer updateDeptName(Department dept){
		Integer resultNum = 0; 
		List<BusiGroupDetail> list = busiGroupDetailDaoDaoImpl.findByUserId(dept.getId(),2);
		if(list!=null && list.size()>0){
			//如果有记录,并且名称变更
			if(dept.getName()!=null && list.get(0).getCardName()!=null && !dept.getName().equals(list.get(0).getCardName())){
				resultNum = busiGroupDetailDaoDaoImpl.updateCardName(dept.getId(),dept.getName(),2);
			}
		}
		return resultNum;
	}
	
}