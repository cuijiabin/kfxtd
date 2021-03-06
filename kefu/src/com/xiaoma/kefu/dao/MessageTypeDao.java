package com.xiaoma.kefu.dao;

import java.util.List;

import com.xiaoma.kefu.model.MessageType;

/**
 * @author frongji
 * @time 2015年4月8日下午3:37:32
 *
 */
public interface MessageTypeDao extends BaseDao<MessageType> {
   
	/**
	 * 查询一条
	 * @param id
	 * @return
	 */
	public MessageType getMessageTypeById(Integer id);
    /**
     * 添加一条
     * @param messageType
     * @return
     */
	public boolean createNewMessageType(MessageType messageType);
	/**
	 * 修改一条
	 * @param messageType
	 * @return
	 */
	public boolean updateMessageType(MessageType messageType);
	/**
	 * 根据id删除一条
	 * @param id
	 * @return
	 */
   public	boolean deleteMessageTypeById(Integer id);
   /**
    * 查询树
    * @param tid
    * @return
    */
   public	List findTree(int typeId,int userId);
   /**
    * 查询子节点
    * @param id
    * @return
    */
   public Integer checkChild(Integer id);
   /**
    * 查询排序号最大的
    * @param id
    * @param typeId
    * @return
    */
   public Integer checkChildMax(Integer id, Integer typeId,Integer userId);
   /**
    * 条件查询
    * @param title
    * @return
    */
    public  MessageType getResultBySearch(Integer typeId,String title,Integer userId);

    /***
	 * 根据typeId查询所有的分类
	 * @param typeId
	 * @param userId
	 * @return
	 */
	public List<MessageType> findAllByParam(Integer typeId,Integer userId);
}
