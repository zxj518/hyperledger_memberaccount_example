/*
Copyright DTCC 2016 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package example;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.java.shim.ChaincodeBase;
import org.hyperledger.java.shim.ChaincodeStub;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.;
/**
 * <h1>Classic "transfer" sample chaincode</h1>
 * (java implementation of <A href="https://github.com/hyperledger/fabric/blob/master/examples/chaincode/go/chaincode_example02/chaincode_example02.go">chaincode_example02.go</A>)
 * @author Sergey Pomytkin spomytkin@gmail.com
 *
 */
public class MemberAccountChainCode extends ChaincodeBase {
	 private static Log log = LogFactory.getLog(MemberAccountChainCode.class);
	 
	 private static final String _ACCOUNT_PERFIX = "account_";
	 
	 private static final String _ITEM_PERFIX = "item_";
	 
	 ObjectMapper mapper = new ObjectMapper();
	 
	@Override
	public String run(ChaincodeStub stub, String function, String[] args) {
		log.info("In run, function:"+function);
		
		switch (function) {
		case "init":
			init(stub, function, args);
			break;
		case "transfer":
			String re = transfer(stub, args);	
			System.out.println(re);
			return re;					
		case "put":
			for (int i = 0; i < args.length; i += 2)
				stub.putState(args[i], args[i + 1]);
			break;
		case "del":
			for (String arg : args)
				stub.delState(arg);
			break;
		case "consume":
			return consume(stub, args);
		
		case "recharge":
			recharge(stub, args);
			break;
		case "create_account":
			return createAccount(stub, args);
			
		default: 
			return "{\"Error\":\"unknow function\"}";
		}
	 
		return null;
	}
	
	
	private String  createAccount(ChaincodeStub stub, String[] args) {
		log.error("in create account");
		if(args.length!=2){
			log.error("Incorrect number of arguments:"+args.length);
			return "{\"Error\":\"Incorrect number of arguments. Expecting 3: from, to, amount\"}";
		}
		
		MemberAccount account = new MemberAccount();
		String accountId =args[0];
		
		
		account.setId(accountId);
		
		String amountArg = args[1];
	
		int valAmount=0;
		if (amountArg!=null&&!amountArg.isEmpty()){			
			try{
				valAmount = Integer.parseInt(amountArg);
			}catch(NumberFormatException e ){
				System.out.println("{\"Error\":\"Expecting integer value for asset holding of "+accountId+" \"}"+e);		
				return "{\"Error\":\"Expecting integer value for asset holding of "+accountId+" \"}";		
			}		
		}else{
			return "{\"Error\":\"Expecting integer value for asset holding of "+accountId+" \"}";		
		}

		account.setAmount(valAmount);
		account.setLastItemId(null);
		account.setUpdateTime("" + new Date().getTime());
		
		
		stub.putState(_ACCOUNT_PERFIX + accountId, toJSON(account));
		//stub.putState(toName, ""+valTo);		

		System.out.println("create account complete");

		return _ACCOUNT_PERFIX + accountId;
		
	}
	
	private String toJSON(Object obj){
		 // can reuse, share globally
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private <T> T fromJSON(String jsonStr, Class<T> clazz){
		 // can reuse, share globally
		try {
			return mapper.readValue(jsonStr, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	private String  transfer(ChaincodeStub stub, String[] args) {
		System.out.println("in transfer");
		if(args.length!=3){
			System.out.println("Incorrect number of arguments:"+args.length);
			return "{\"Error\":\"Incorrect number of arguments. Expecting 3: from, to, amount\"}";
		}
		String fromName =args[0];
		String fromAm=stub.getState(fromName);
		String toName =args[1];
		String toAm=stub.getState(toName);
		String am =args[2];
		int valFrom=0;
		if (fromAm!=null&&!fromAm.isEmpty()){			
			try{
				valFrom = Integer.parseInt(fromAm);
			}catch(NumberFormatException e ){
				System.out.println("{\"Error\":\"Expecting integer value for asset holding of "+fromName+" \"}"+e);		
				return "{\"Error\":\"Expecting integer value for asset holding of "+fromName+" \"}";		
			}		
		}else{
			return "{\"Error\":\"Failed to get state for " +fromName + "\"}";
		}

		int valTo=0;
		if (toAm!=null&&!toAm.isEmpty()){			
			try{
				valTo = Integer.parseInt(toAm);
			}catch(NumberFormatException e ){
				e.printStackTrace();
				return "{\"Error\":\"Expecting integer value for asset holding of "+toName+" \"}";		
			}		
		}else{
			return "{\"Error\":\"Failed to get state for " +toName + "\"}";
		}
		
		int valA =0;
		try{
			valA = Integer.parseInt(am);
		}catch(NumberFormatException e ){
			e.printStackTrace();
			return "{\"Error\":\"Expecting integer value for amount \"}";
		}		
		if(valA>valFrom)
			return "{\"Error\":\"Insufficient asset holding value for requested transfer amount \"}";
		valFrom = valFrom-valA;
		valTo = valTo+valA;
		System.out.println("Transfer "+fromName+">"+toName+" am='"+am+"' new values='"+valFrom+"','"+ valTo+"'");
		stub.putState(fromName,""+ valFrom);
		stub.putState(toName, ""+valTo);		

		System.out.println("Transfer complete");

		return null;
		
	}

	private MemberAccount getAccountState(ChaincodeStub stub, String accountId){
		
		String accountStr=stub.getState(_ACCOUNT_PERFIX + accountId);
		
		MemberAccount account = null;
		if (accountStr!=null&&!accountStr.isEmpty()){			
			try{
				account = fromJSON(accountStr, MemberAccount.class);
			}catch(NumberFormatException e ){
				//System.out.println("{\"Error\":\"Expecting integer value for asset holding of "+fromName+" \"}"+e);		
				log.error( "{\"Error\":\"parse account value  of "+accountId+" \"}", e);		
			}		
		}else{
			log.error( "{\"Error\":\"Failed to get state for " +accountId + "\"}");
		}
		return account;
	}
	
	private AccountItem getAccountItemState(ChaincodeStub stub, String accountItemId){
		
		String itemStr=stub.getState(_ITEM_PERFIX + accountItemId);
		
		AccountItem accountItem = null;
		if (itemStr!=null&&!itemStr.isEmpty()){			
			try{
				accountItem = fromJSON(itemStr, AccountItem.class);
			}catch(NumberFormatException e ){
				//System.out.println("{\"Error\":\"Expecting integer value for asset holding of "+fromName+" \"}"+e);		
				log.error( "{\"Error\":\"parse account item value  of "+_ITEM_PERFIX+accountItemId + " \"}", e);		
			}		
		}else{
			log.error( "{\"Error\":\"Failed to get state for " +_ITEM_PERFIX + accountItemId + "\"}");
		}
		return accountItem;
	}
	
	public String consume(ChaincodeStub stub, String[] args){
		System.out.println("in consume");
		if(args.length!=2){
			System.out.println("Incorrect number of arguments:"+args.length);
			return "{\"Error\":\"Incorrect number of arguments. Expecting 2: aid, consume, amount\"}";
		}
		String accountId =args[0];
		
		MemberAccount account = getAccountState(stub, accountId);
		
		String consumeItemStr = args[1];
		AccountItem accountItem = fromJSON(consumeItemStr, AccountItem.class);
	
		if(account==null){
			return  "{\"Error\":\"Failed to consume state for account " +accountId + "\"}";
		}
		
		if(accountItem==null){
			return  "{\"Error\":\"Failed to consume state for accountItem " +consumeItemStr + "\"}";
		}
		
		int restAmount = account.getAmount() - accountItem.getItemAmount();
		account.setAmount(restAmount);
		account.setUpdateTime("" + new Date().getTime());
		account.setLastItemId(accountItem.getItemId());
		
	
	
			try{
				String existItem =stub.getState(_ITEM_PERFIX + accountItem.getItemId());
				if(existItem!=null && existItem.length() > 0){
					log.error("exist item:" + existItem);
					throw new Exception("exist item");
				}
				stub.putState(_ACCOUNT_PERFIX + account.getId(), toJSON(account));
				stub.putState(_ITEM_PERFIX + accountItem.getItemId(), toJSON(accountItem));
				
			}catch(Exception e ){
				log.error("save account failed", e);
				return "{\"Error\":\"Expecting integer value for asset holding of "+accountId+" \"}";		
			}	
		
//		if(valA>valFrom)
//			return "{\"Error\":\"Insufficient asset holding value for requested transfer amount \"}";
//		valFrom = valFrom-valA;
//		valTo = valTo+valA;
//		System.out.println("Transfer "+fromName+">"+toName+" am='"+am+"' new values='"+valFrom+"','"+ valTo+"'");
//		stub.putState(fromName,""+ valFrom);
//		stub.putState(toName, ""+valTo);		

		log.info("consume complete");
		

		return null;
		
	}
	
	public String recharge(ChaincodeStub stub, String[] args){
		System.out.println("in recharge");
		if(args.length!=2){
			System.out.println("Incorrect number of arguments:"+args.length);
			return "{\"Error\":\"Incorrect number of arguments. Expecting 2: aid, recharge, amount\"}";
		}
		String accountId =args[0];
		
		MemberAccount account = getAccountState(stub, accountId);
		
		String rechargeItemStr = args[1];
		AccountItem accountItem = fromJSON(rechargeItemStr, AccountItem.class);
	
		if(account==null){
			return  "{\"Error\":\"Failed to recharge state for account " +accountId + "\"}";
		}
		
		if(accountItem==null){
			return  "{\"Error\":\"Failed to recharge state for accountItem " + rechargeItemStr + "\"}";
		}
		
		int restAmount = account.getAmount() + accountItem.getItemAmount();
		account.setAmount(restAmount);
		account.setUpdateTime("" + new Date().getTime());
		account.setLastItemId(accountItem.getItemId());
		
	
			try{
				String existItem =stub.getState(_ITEM_PERFIX + accountItem.getItemId());
				if(existItem!=null && existItem.length() > 0){
					log.error("exist item:" + existItem);
					throw new Exception("exist item");
				}
				stub.putState(_ACCOUNT_PERFIX + account.getId(), toJSON(account));
				stub.putState(_ITEM_PERFIX + accountItem.getItemId(), toJSON(accountItem));
				
			}catch(Exception e ){
				log.error("save account failed", e);
				return "{\"Error\":\"Expecting integer value for asset holding of "+accountId+" \"}";		
			}	
		
//		if(valA>valFrom)
//			return "{\"Error\":\"Insufficient asset holding value for requested transfer amount \"}";
//		valFrom = valFrom-valA;
//		valTo = valTo+valA;
//		System.out.println("Transfer "+fromName+">"+toName+" am='"+am+"' new values='"+valFrom+"','"+ valTo+"'");
//		stub.putState(fromName,""+ valFrom);
//		stub.putState(toName, ""+valTo);		

		log.info("recharge complete");
		

		return null;
		
	}
	
	public String init(ChaincodeStub stub, String function, String[] args) {
		if(args.length!=2){
			return "{\"Error\":\"Incorrect number of arguments. Expecting 4\"}";
		}
		try{
			createAccount(stub, new String[]{args[0], args[1]});
			createAccount(stub, new String[]{args[2], args[3]});
//			int valB = Integer.parseInt(args[3]);
//			stub.putState(_ACCOUNT_PERFIX + args[0], args[1]);
//			stub.putState(_ACCOUNT_PERFIX + args[2], args[3]);		
		}catch(NumberFormatException e ){
			log.error("init failed", e);
			return "{\"Error\":\"Expecting integer value for asset holding\"}";
		}		
		return null;
	}

	
	@Override
	public String query(ChaincodeStub stub, String function, String[] args) {
		if(args.length!=2){
			return "{\"Error\":\"Incorrect number of arguments. Expecting name of the person to query\"}";
		}
		
		switch(args[0]){
			case "member_account":
				return queryAccount(stub, args);
				
			case "account_item":
				return queryItem(stub, args);
			
		}
		return "{\"Error\":\"Failed to get state for " + function + "\"}";
	}
	
	
	public String queryAccount(ChaincodeStub stub, String[] args){
		String accountStr =stub.getState(_ACCOUNT_PERFIX + args[1]);
		if (accountStr!=null&&!accountStr.isEmpty()){
			try{
				
				return  "{\"Name\":\"" + args[1] + "\",\"data\":\"" + accountStr + "\"}";
			}catch(NumberFormatException e ){
				return "{\"Error\":\"Expecting integer value for asset holding\"}";		
			}		
		}else{
			return "{\"Error\":\"Failed to get state for " + _ACCOUNT_PERFIX + args[1] + "\"}";
		}
		

	}
	
	public String queryItem(ChaincodeStub stub, String[] args){
		String itemStr =stub.getState(_ITEM_PERFIX + args[1]);
		if (itemStr!=null&&!itemStr.isEmpty()){
			try{
				return  "{\"Name\":\"" + args[1] + "\",\"data\":\"" + itemStr + "\"}";
	
			}catch(NumberFormatException e ){
				return "{\"Error\":\"Expecting integer value for asset holding\"}";		
			}		}else{
			return "{\"Error\":\"Failed to get state for " + _ACCOUNT_PERFIX + args[1] + "\"}";
		}
		

	}
	
	

	@Override
	public String getChaincodeID() {
		return "MemberAccount";
	}

	public static void main(String[] args) throws Exception {
		new MemberAccountChainCode().start(args);
	}
	
	

}
