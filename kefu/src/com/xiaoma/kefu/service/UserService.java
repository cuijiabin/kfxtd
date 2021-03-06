package com.xiaoma.kefu.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoma.kefu.cache.CacheMan;
import com.xiaoma.kefu.cache.CacheName;
import com.xiaoma.kefu.dao.DepartmentDao;
import com.xiaoma.kefu.dao.UserDao;
import com.xiaoma.kefu.dict.DictMan;
import com.xiaoma.kefu.model.Department;
import com.xiaoma.kefu.model.User;
import com.xiaoma.kefu.redis.JedisTalkDao;
import com.xiaoma.kefu.util.JsonUtil;
import com.xiaoma.kefu.util.PageBean;
import com.xiaoma.kefu.util.StringHelper;
import com.xiaoma.kefu.util.SysConst.RoleName;

/**
 * User DAO service class
 * 
 * @author yangxiaofeng
 * 
 */
@Service("userService")
public class UserService {

	@Autowired
	private UserDao userDaoImpl;
	@Autowired
	private DepartmentDao deptDao;
	@Autowired
	private BusiGroupDetailService busiGroupDetailService;

	public User login(String name) {
		return userDaoImpl.findUser(name);
	}

	/**
	 * 条件查询用户
	 */
	public PageBean<User> getResultByuserNameOrPhone(Integer currentPage,
			Integer pageRecorders, String userName, String phone) {

		Integer totalCount = userDaoImpl.getUserCountByuserNameOrPhone(
				userName, phone);
		PageBean<User> result = new PageBean<User>();

		result.setCurrentPage(currentPage);
		result.setPageRecorders(pageRecorders);
		result.setTotalRows(totalCount);

		Integer start = result.getStartRow();

		List<User> list = userDaoImpl.getUserByuserNameOrPhone(start,
				pageRecorders, userName, phone);
		result.setObjList(list);

		return result;
	}

	/**
	 * 分页查询
	 * 
	 * @param map
	 * @param pageBean
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public void getResult(Map<String, String> conditions,
			PageBean<User> pageBean) {
		userDaoImpl.findByCondition(conditions, pageBean);
		List<User> list=pageBean.getObjList();
		if(CollectionUtils.isNotEmpty(list)){
		List<User> newList= new ArrayList();
		for (User user : list) {
			User us = (User) CacheMan.getObject(CacheName.SUSER, user.getId());
			newList.add(us);
		}
		pageBean.setObjList(newList);
		}
	}

	/**
	 * 精确查询
	 */
	public Integer checkUser(User user) {

		Integer totalCount = userDaoImpl.checkUser(user);

		return totalCount;
	}

	/**
	 * 添加
	 * 
	 * @throws ParseException
	 */
	public Integer createNewUser(User user) throws ParseException {
		user.setOnLineStatus(2);
		user.setStatus(1);
		user.setIsLock(0);
		Integer succ = (Integer) userDaoImpl.add(user);
		List<Department> dlist = deptDao.getDeptById(user.getDeptId());
		Department dept = dlist.get(0);
		dept.setUserCount(dept.getUserCount() + 1);
		deptDao.update(dept);
		return succ;
	}

	/**
	 * 在弹出的对话框显示详细信息
	 */
	public User getUserById(Integer id) {

		User user = userDaoImpl.findById(User.class, id);
		return user;
	}

	/**
	 * 修改或者重置密码
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public Integer updateUser(String pass, User user)
			throws UnsupportedEncodingException {
		User toUpdateUser = userDaoImpl.findById(User.class, user.getId());

		if (StringHelper.isNotEmpty(pass) && !pass.equals("1")) {
			String password = new String(DigestUtils.md5Hex(pass
					.getBytes("UTF-8")));
			toUpdateUser.setPassword(password);
		} else if (StringHelper.isNotEmpty(pass) && pass.equals("1")) {
			toUpdateUser.setIsLock(1);
		} else if (StringHelper.isNotEmpty(user.getPassword())) {
			String password = new String(DigestUtils.md5Hex(user.getPassword()
					.getBytes("UTF-8")));
			toUpdateUser.setPassword(password);
			toUpdateUser.setIsLock(0);
		}
		toUpdateUser.setLoginName(user.getLoginName());
		toUpdateUser.setCardName(user.getCardName());
		if (toUpdateUser.getDeptId() != user.getDeptId()) {
			List<Department> dlist = deptDao.getDeptById(toUpdateUser
					.getDeptId());
			Department dept = dlist.get(0);
			dept.setUserCount(dept.getUserCount() - 1);
			deptDao.update(dept);
			List<Department> dlist1 = deptDao.getDeptById(user.getDeptId());
			Department dept1 = dlist1.get(0);
			dept1.setUserCount(dept1.getUserCount() + 1);
			deptDao.update(dept1);
			toUpdateUser.setDeptId(user.getDeptId());
		}
		toUpdateUser.setRoleId(user.getRoleId());
		toUpdateUser.setDeptName(user.getDeptName());
		toUpdateUser.setRoleName(user.getRoleName());
		toUpdateUser.setListenLevel(user.getListenLevel());
		toUpdateUser.setEmail(user.getEmail());
		toUpdateUser.setMaxListen(user.getMaxListen());
		toUpdateUser.setCreateDate(user.getCreateDate());
		Integer succ = userDaoImpl.update(toUpdateUser);

		if (user.getMaxListen() != null) {
			JedisTalkDao.setMaxReceiveCount(user.getId().toString(),
					user.getMaxListen());
		}
		busiGroupDetailService.updateUserCardName(user);
		CacheMan.remove(CacheName.USERFUNCTION, user.getId());
		CacheMan.remove(CacheName.SUSER, user.getId());
		return succ;

	}

	/**
	 * 删除(假删除)
	 */
	public Integer deleteUserById(String ids) {
		int val = 0;
		if (ids.length() > 2) {
			String[] array = ids.split(",");
			for (String str : array) {
				if (Integer.parseInt(str) == 0)
					continue;
				User user = new User();
				user.setId(Integer.parseInt(str));
				val = userDaoImpl.delete(user);
				CacheMan.remove(CacheName.USERFUNCTION, user.getId());
				CacheMan.remove(CacheName.SUSER, user.getId());
			}
			if (val == 1) {
				return 1;
			} else {
				return 0;
			}
		} else {
			User user = new User();
			user.setId(Integer.parseInt(ids));
			CacheMan.remove(CacheName.USERFUNCTION, user.getId());
			CacheMan.remove(CacheName.SUSER, user.getId());
			return userDaoImpl.delete(user);
		}
	}

	/**
	 * 员工离职支持多选处理
	 * @param ids
	 * @param status
	 * @return
	 */
	public Integer leaveUser(String ids, Integer status) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		int val = 0;
		if (ids.length() > 2) {
			String[] array = ids.split(",");
			for (String str : array) {
				if (Integer.parseInt(str) == 0)
					continue;
				User leup = userDaoImpl.findById(User.class,
						Integer.parseInt(str));
				leup.setStatus(status);
				leup.setEndDate(time);
				List<Department> dlist = deptDao.getDeptById(leup.getDeptId());
				Department dept = dlist.get(0);
				if (status == 2) {
					dept.setUserCount(dept.getUserCount() - 1);
					busiGroupDetailService.deleteByUserId(leup.getId());
				} else {
					dept.setUserCount(dept.getUserCount() + 1);
				}
				deptDao.update(dept);
				val = userDaoImpl.update(leup);
				CacheMan.remove(CacheName.USERFUNCTION, leup.getId());
				CacheMan.remove(CacheName.SUSER, leup.getId());
			}
			if (val == 1) {
				return 1;
			} else {
				return 0;
			}
		} else {
			User leup = userDaoImpl.findById(User.class, Integer.parseInt(ids));
			leup.setStatus(status);
			leup.setEndDate(time);
			List<Department> dlist = deptDao.getDeptById(leup.getDeptId());
			Department dept = dlist.get(0);
			if (status == 2) {
				dept.setUserCount(dept.getUserCount() - 1);
				busiGroupDetailService.deleteByUserId(leup.getId());
			} else {
				dept.setUserCount(dept.getUserCount() + 1);
			}
			deptDao.update(dept);
			Integer succ = userDaoImpl.update(leup);
			CacheMan.remove(CacheName.USERFUNCTION, leup.getId());
			CacheMan.remove(CacheName.SUSER, leup.getId());
			if (succ == 1) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 员工转换部门
	 * @param ids
	 * @param deptId
	 * @return
	 */
	public Integer tradeUser(String ids, Integer deptId) {
		if (ids.length() > 2) {
			String[] array = ids.split(",");
			Integer succ = 0;
			for (String str : array) {
				if (Integer.parseInt(str) == 0)
					continue;
				User leup = userDaoImpl.findById(User.class,
						Integer.parseInt(str));
				List<Department> dlist1 = deptDao.getDeptById(leup.getDeptId());
				Department dept1 = dlist1.get(0);
				dept1.setUserCount(dept1.getUserCount() - 1);
				deptDao.update(dept1);
				List<Department> dlist = deptDao.getDeptById(deptId);
				Department dept = dlist.get(0);
				dept.setUserCount(dept.getUserCount() + 1);
				deptDao.update(dept);
				leup.setDeptId(deptId);
				leup.setDeptName(dept.getName());
				succ = userDaoImpl.update(leup);
				CacheMan.remove(CacheName.USERFUNCTION, leup.getId());
				CacheMan.remove(CacheName.SUSER, leup.getId());
			}
			if (succ == 1) {
				return succ;
			}
			return 0;
		} else {
			User leup = userDaoImpl.findById(User.class, Integer.parseInt(ids));
			List<Department> dlist1 = deptDao.getDeptById(leup.getDeptId());
			Department dept1 = dlist1.get(0);
			dept1.setUserCount(dept1.getUserCount() - 1);
			deptDao.update(dept1);
			List<Department> dlist = deptDao.getDeptById(deptId);
			Department dept = dlist.get(0);
			dept.setUserCount(dept.getUserCount() + 1);
			deptDao.update(dept);
			leup.setDeptId(deptId);
			leup.setDeptName(dept.getName());
			CacheMan.remove(CacheName.USERFUNCTION, leup.getId());
			CacheMan.remove(CacheName.SUSER, leup.getId());
			return userDaoImpl.update(leup);
		}
	}

	/**
	 * 通过部门查询员工
	 * @param deptId
	 * @return
	 */
	public List<User> getResultDept(Integer deptId) {

		List<User> list = userDaoImpl.getUsertByDeptId(deptId);
		return list;
	}

	/**
	 * 根据id列表批量获取用户
	 * @param ids
	 * @return
	 */
	public List<User> getUsersByIds(List<Integer> ids) {
		List<Serializable> userIds = JsonUtil.convertInteger2Serializable(ids);
		if (CollectionUtils.isEmpty(userIds)) {
			return null;
		}
		return userDaoImpl.findByIds(User.class, userIds);
	}

	/**
	 * 获取全部用户
	 * @return
	 */
	public List<User> getAll() {

		return userDaoImpl.findAll(User.class);

	}

	/**
	 * 判断用户是否 有 roleName 这个角色
	 * 
	 * @param user
	 *            用户
	 * @param roleName
	 *            角色名称
	 * @return
	 * @Author: wangxingfei
	 * @Date: 2015年5月4日
	 */
	public boolean hasThisRole(User user, RoleName roleName) {
		boolean falg = false;
		if (user != null && user.getRoleId() != null) {
			String zhuguanId = DictMan.getDictItem("d_role_id",
					roleName.toString()).getItemName();
			if (user.getRoleId().equals(Integer.valueOf(zhuguanId))) {
				falg = true;
			}
		}
		return falg;
	}

}
