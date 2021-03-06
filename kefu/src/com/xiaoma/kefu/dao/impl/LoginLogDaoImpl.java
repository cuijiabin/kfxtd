package com.xiaoma.kefu.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.xiaoma.kefu.dao.LoginLogDao;
import com.xiaoma.kefu.model.LoginLog;
import com.xiaoma.kefu.util.PageBean;
import com.xiaoma.kefu.util.StringHelper;

@Repository("loginLogDaoImpl")
public class LoginLogDaoImpl extends BaseDaoImpl<LoginLog> implements
		LoginLogDao {
	private static Logger logger = Logger.getLogger(LoginLogDaoImpl.class);

	public void findByCondition(Map<String, String> conditions,
			PageBean<LoginLog> pageBean) {
		List<String> relation = new ArrayList<String>();
		List<Criterion> role = new ArrayList<Criterion>();// 条件
		List<Order> orders = new ArrayList<Order>();// 排序
		try {
			if (conditions != null) {
				if (StringHelper.isNotEmpty(conditions.get("loginName"))) {
					role.add(Restrictions.like("loginName", "%"
							+ conditions.get("loginName").trim() + "%"));
				}
				if (StringHelper.isNotEmpty(conditions.get("deptId"))
						&& !"0".equals(conditions.get("deptId"))) {
					role.add(Restrictions.eq("deptId",
							Integer.parseInt(conditions.get("deptId"))));
				}
				if (StringHelper.isNotEmpty(conditions.get("userId"))
						&& !"0".equals(conditions.get("deptId"))) {
					role.add(Restrictions.eq("userId",
							Integer.parseInt(conditions.get("userId"))));
				}
				if (conditions.get("startDate") != null
						&& !conditions.get("startDate").isEmpty()) {
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");

					role.add(Restrictions.between(
							"createDate",
							format.parse(conditions.get("startDate")
									+ " 0:00:00"),
							(StringHelper.isEmpty(conditions.get("endDate")) ? format
									.parse(conditions.get("endDate")) : format
									.parse(conditions.get("endDate"))
									+ " 23:59:59")));
				}
			}

			orders.add(Order.asc("createDate"));
			find(LoginLog.class, relation, role, null, orders, pageBean);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	public List<LoginLog> findByUserId(Integer userId) {
		try {
			Session session = getSession();
			List<LoginLog> list = session.createQuery("from LoginLog a where a.userId="+userId+" order by a.createDate desc").setMaxResults(1).list();
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
