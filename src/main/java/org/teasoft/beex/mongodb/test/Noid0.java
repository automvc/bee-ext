package org.teasoft.beex.mongodb.test;

import java.io.Serializable;

import org.teasoft.bee.osql.annotation.GenId;
import org.teasoft.bee.osql.annotation.GenUUID;
import org.teasoft.bee.osql.annotation.PrimaryKey;
import org.teasoft.bee.osql.annotation.Table;

/**
*@author Honey
*Create on 2021-07-08 22:25:17
*/
@Table("Noid")
public class Noid0 implements Serializable {

	private static final long serialVersionUID = 1590272445571L;
	
	@PrimaryKey
//	@GenUUID
	@GenId   //在String类型字段,使用long型id
	private String uuid;
	private String name;
	private Integer num;
	private String remark;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	 public String toString(){	
		 StringBuffer str=new StringBuffer();	
		 str.append("Noid[");			
		 str.append("uuid=").append(uuid);		 
		 str.append(",name=").append(name);		 
		 str.append(",num=").append(num);		 
		 str.append(",remark=").append(remark);		 
		 str.append("]");			 
		 return str.toString();			 
	 }		 
}