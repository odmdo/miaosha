package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.SequenceDo;

public interface SequenceDoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Dec 24 15:26:47 CST 2018
     */
    int deleteByPrimaryKey(String name);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Dec 24 15:26:47 CST 2018
     */
    int insert(SequenceDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Dec 24 15:26:47 CST 2018
     */
    int insertSelective(SequenceDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Dec 24 15:26:47 CST 2018
     */
    SequenceDo selectByPrimaryKey(String name);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Dec 24 15:26:47 CST 2018
     */
    int updateByPrimaryKeySelective(SequenceDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Dec 24 15:26:47 CST 2018
     */
    int updateByPrimaryKey(SequenceDo record);

    //获取序列值
    SequenceDo getSequenceByName(String name);
}