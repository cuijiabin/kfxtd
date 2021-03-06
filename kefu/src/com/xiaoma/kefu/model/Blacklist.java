package com.xiaoma.kefu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author frongji
 * @time 2015年4月2日上午11:03:42
 *
 */
@Entity
@Table(name = "black_list")
public class Blacklist implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

   
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "ip")
	private String ip;
	 
	@Column(name = "ipInfo")
	private String ipInfo;
	
	@DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss"  )
	@Column(name = "startDate")
	private Date startDate;
	
	@DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss"  )
	@Column(name = "endDate")
	private Date endDate;
	
	@Column(name = "customerId")
	private Long  customerId;
	
	@Column(name= "description")
	private String description;
	
	
	@Column(name = "userId")
	private Integer userId;
	 
	@Column(name = "createDate")
	private Date createDate;
    
	@Column(name = "userName")
	private String userName;
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}





	
	
}
