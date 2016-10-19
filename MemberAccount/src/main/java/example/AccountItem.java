/*******************************************************************************
 *
 * @Title: AccountItem.java
 *
 * @Copyright (c) 2016 深圳前海融金所互联网金融服务有限公司 版权所有. 粤ICP备13026617号
 * 注意：本内容仅限于深圳前海融金所互联网金融服务有限公司 内部传阅，禁止外泄以及用于其他商业目的!
 *
 ******************************************************************************/
package example;

/**   
 * @Title: AccountItem.java
 * @Package example
 * @Description: TODO
 * @author zouxuejun
 * @date 2016年10月12日 下午2:34:12
 * @version V1.0   
 */
public class AccountItem {

	private String itemId;
	
	private String account;
	
	private String to;
	
	private int itemAmount;
	
	private String itemType;
	
	private String restAmount;
	
	private String itemTime;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(int itemAmount) {
		this.itemAmount = itemAmount;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getRestAmount() {
		return restAmount;
	}

	public void setRestAmount(String restAmount) {
		this.restAmount = restAmount;
	}

	public String getItemTime() {
		return itemTime;
	}

	public void setItemTime(String itemTime) {
		this.itemTime = itemTime;
	}
	
	
	
}
