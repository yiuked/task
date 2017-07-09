package net.changmi.junit.test;

import java.util.HashMap;
import java.util.Map;

import zsjr.NidExistsException;
import zsjr.UserAccount;

import org.junit.Test;

public class JUnitTest {

	@Test
	public void test() throws NidExistsException {
		Map<String, Object> data = new HashMap<String, Object>();        
		data.put("nid", String.format("cash_%s", "20170630125"));
		data.put("user_id", 319887);
		data.put("account_web_status", 0);
		data.put("account_user_status", 0);
		data.put("money", 10.00);
		data.put("income", 0.00);
		data.put("expend", 0.00);
		data.put("balance_cash", -10.00);
		data.put("balance_frost", 0.00);
		data.put("frost", 10.00);
		data.put("await", 0.00);
		data.put("repay", 0.00);
		data.put("type", "cash");
		data.put("code", "account");
		data.put("to_userid", 0);
		data.put("remark", String.format("ÉêÇëÌáÏÖ%fÔª", 10.00));
		UserAccount.addLog(data);
	}

}
