package com.xiaoma.kefu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.xiaoma.kefu.util.JsonUtil;

/**
 * @author frongji
 * @time 2015年4月1日下午4:22:55
 *  访客信息--模型类
 */
@Entity
@Table(name = "customer")
public class Customer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "ip")
	private String ip;
	
	@Column(name = "ipInfo")
	private String ipInfo;
	
	@Column(name = "styleId")
	private Integer styleId;
	
	@Column(name = "customerName")
	private String customerName;
    
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name= "remark")
	private String remark;
	
	@Column(name = "createDate")
	private Date createDate;
	
	@Column(name = "updateDate")
	private Date updateDate;
	
	@Column(name = "userId")
	private Integer userId;
    
	@Column(name = "customerType")
	private Integer customerType;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "firstVisitSource")
	private String firstVisitSource;
	
	@Column(name = "firstLandingPage")
	private String firstLandingPage;
	
	@Column(name = "styleName")
	private String styleName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	

	public String getIpInfo() {
		return ipInfo;
	}

	public void setIpInfo(String ipInfo) {
		this.ipInfo = ipInfo;
	}

	public Integer getStyleId() {
		return styleId;
	}

	public void setStyleId(Integer styleId) {
		this.styleId = styleId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getCustomerType() {
		return customerType;
	}

	public void setCustomerType(Integer customerType) {
		this.customerType = customerType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFirstVisitSource() {
		return firstVisitSource;
	}

	public void setFirstVisitSource(String firstVisitSource) {
		this.firstVisitSource = firstVisitSource;
	}

	public String getFirstLandingPage() {
		return firstLandingPage;
	}

	public void setFirstLandingPage(String firstLandingPage) {
		this.firstLandingPage = firstLandingPage;
	}
	
	public String toString(){
		
		return JsonUtil.toJson(this);
	}

	public String getStyleName() {
		return styleName;
	}

	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	
	
	
}
