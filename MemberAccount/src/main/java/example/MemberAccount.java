/*******************************************************************************
 *
 * @Title: MemberAccount.java
 *
 * @Copyright (c) 2016 深圳前海融金所互联网金融服务有限公司 版权所有. 粤ICP备13026617号
 * 注意：本内容仅限于深圳前海融金所互联网金融服务有限公司 内部传阅，禁止外泄以及用于其他商业目的!
 *
 ******************************************************************************/
package example;

/**   
 * @Title: MemberAccount.java
 * @Package example
 * @Description: TODO
 * @author zouxuejun
 * @date 2016年10月12日 下午2:22:17
 * @version V1.0   
 */
public class MemberAccount {

	private String id;
	
	private int amount;
	
	
	private String lastItemId;
	
	private String updateTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getLastItemId() {
		return lastItemId;
	}

	public void setLastItemId(String lastItemId) {
		this.lastItemId = lastItemId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
