package com.xiaoma.kefu.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.xiaoma.kefu.dao.RoleDao;
import com.xiaoma.kefu.model.Role;



@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseDaoImpl<Role> implements RoleDao{
	/**
	 * 查询所有数据
	 */
	@Override
    public Integer getAllRoleCount() {
		
		Session session = getSession();
		String hql = "select count(1) from Role c where isDel<>1 ";
		Query query = session.createSQLQuery(hql);
		return ((Number)query.uniqueResult()).intValue();
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> getRoleOrderById(Integer start, Integer offset) {
		//参数检查
		start = (start == null)? 0 :start;
		offset = (offset == null)? 20 :offset;
		
		Session session = getSession();
		String hql = "from Role where isDel<>1 order by id asc";
		Query query = session.createQuery(hql).setFirstResult(start).setMaxResults(offset);
		
		return (List<Role>) query.list();
	}
	
	@Override
	public Integer checkRole(Role role) {
		Session session = getSession();
		StringBuffer hqlBf = new StringBuffer("select count(a.id) from Role a where isDel<>1");
		if(StringUtils.isNotBlank(role.getName())){
			hqlBf.append(" and a.name  = '"+role.getName()+"' ");
		}
		Query query = session.createSQLQuery(hqlBf.toString());
		Object obj = query.uniqueResult();
		return Integer.parseInt(obj.toString());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> findRole() {
		Session session = getSession();
		String hql = "from Role where isDel<>1 ";
		Query query = session.createQuery(hql);
		return query.list();
	} 
}





