package zsjr;

import java.util.Map;

public class UserAccount {
	public static boolean addLog(Map<String, Object> data) throws NidExistsException {
		Map<String, Object> log = Mysql.instance().queryOne(
				String.format("SELECT id FROM `{account_log}` WHERE `nid`='%s'", data.get("nid"))
				);
        if (log.isEmpty() == false) {
        	throw new NidExistsException(String.format("account_log中已存在nid%s", data.get("nid")));
        }

	    Mysql.instance().execut("BEGIN");
	    Map<String, Object> account;
	    account = Mysql.instance().queryOne(
	    		String.format("SELECT * FROM `{account}` WHERE `user_id`=%d", data.get("user_id"))
	    		);
        //第二步，查询原来的总资金
        if (account.isEmpty()) {
        	Mysql.instance().insert(
        			String.format("insert into `{account}` set user_id=%d,total=0", data.get("user_id"))
        			);
    	    account = Mysql.instance().queryOne(
    	    		String.format("SELECT * FROM `{account}` WHERE `user_id`=%d", data.get("user_id"))
    	    		);
        }

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO `{account_log}` SET ");
        sql.append(String.format("`nid`='%s',", data.get("nid")));
        sql.append(String.format("`borrow_nid`='%s',", data.get("borrow_nid")));
        sql.append(String.format("`account_web_status`=%d,", data.get("account_web_status")));
        sql.append(String.format("`account_user_status`=%d,", data.get("account_user_status")));
        sql.append(String.format("`code`='%s',", data.get("code")));
        sql.append(String.format("`code_type`='%s',", data.get("code_type")));
        sql.append(String.format("`code_nid`='%s',", data.get("code_nid")));
        sql.append(String.format("`user_id`=%d,", data.get("user_id")));
        sql.append(String.format("`type`='%s',", data.get("type")));
        sql.append("`account_type`='null',");
        sql.append(String.format("`money`=%.2f,", data.get("money")));
        sql.append(String.format("`remark`='%s',", data.get("remark")));
        sql.append(String.format("`to_userid`=%d,", data.get("to_userid")));
        
        sql.append(String.format("`balance_cash_new`=%.2f,", data.get("balance_cash")));
        sql.append(String.format("`balance_cash_old`=%.2f,", account.get("balance_cash")));
        sql.append("`balance_cash`=balance_cash_new+balance_cash_old,");
        
        sql.append(String.format("`balance_frost_new`=%.2f,", data.get("balance_frost")));
        sql.append(String.format("`balance_frost_old`=%.2f,", account.get("balance_frost")));
        sql.append("`balance_frost`=balance_frost_new+balance_frost_old,");
        
        sql.append("`balance_new`=balance_cash_new+balance_frost_new,");
        sql.append(String.format("`balance_old`=%.2f,", account.get("balance")));
        sql.append("balance=balance_new+balance_old,");
        
        sql.append(String.format("`income_new`=%.2f,", data.get("income")));
        sql.append(String.format("`income_old`=%.2f,", account.get("income")));
        sql.append("income=income_new+income_old,");
        
        sql.append(String.format("`expend_new`=%.2f,", data.get("expend")));
        sql.append(String.format("`expend_old`=%.2f,", account.get("expend")));
        sql.append("expend=expend_new+expend_old,");
        
        sql.append(String.format("`frost_new`=%.2f,", data.get("frost")));
        sql.append(String.format("`frost_old`=%.2f,", account.get("frost")));
        sql.append("frost=frost_new+frost_old,");
        
        sql.append(String.format("`await_new`=%.2f,", data.get("await")));
        sql.append(String.format("`await_old`=%.2f,", account.get("await")));
        sql.append("await=await_new+await_old,");
        
        sql.append(String.format("`repay_new`=%.2f,", data.get("repay")));
        sql.append(String.format("`repay_old`=%.2f,", account.get("repay")));
        sql.append("repay=repay_new+repay_old,");
        
        sql.append(String.format("`total_old`=%.2f,", account.get("total")));
        sql.append("`total`=balance+frost+await,");
        
        sql.append(String.format("`addtime`=%d,", System.currentTimeMillis()/1000));
        sql.append("`addip`='127.0.0.1'");
        
        int lastID = Mysql.instance().insert(sql.toString());
		Map<String, Object> lastLog = Mysql.instance().queryOne(
				String.format("SELECT * FROM `{account_log}` WHERE `id`=%d", lastID)
				);

		StringBuffer updSql = new StringBuffer();
		updSql.append("UPDATE `{account}` SET ");
		updSql.append(String.format("`income`=%.2f,", lastLog.get("income")));
		updSql.append(String.format("`expend`=%.2f,", lastLog.get("expend")));
		updSql.append(String.format("`balance_cash`=%.2f,", lastLog.get("balance_cash")));
		updSql.append(String.format("`balance_frost`=%.2f,", lastLog.get("balance_frost")));
		updSql.append(String.format("`await`=%.2f,", lastLog.get("await")));
		updSql.append(String.format("`balance`=%.2f,", lastLog.get("balance")));
		updSql.append(String.format("`repay`=%.2f,", lastLog.get("repay")));
		updSql.append(String.format("`total`=%.2f", lastLog.get("total")));
		updSql.append(String.format(" where user_id=%d", data.get("user_id")));
		Mysql.instance().execut(updSql.toString());
		Mysql.instance().execut("COMMIT");


	    //第三步，加入总费用
		Map<String, Object> accountBalance = Mysql.instance().queryOne(
				String.format("SELECT COUNT(*) FROM `{account_balance}` WHERE `nid`= '%S'", data.get("nid"))
				);
		if (accountBalance.isEmpty()) {
			Map<String, Object> lastBalance = Mysql.instance().queryOne(
					"SELECT total,balance FROM `{account_balance}` ORDER BY id DESC"
					);
			if (lastBalance.isEmpty()) {
				lastBalance.put("total", 0.00);
				lastBalance.put("balance", 0.00);
			}
			float acbTotal = (float)lastBalance.get("total") + (float)data.get("income") + (float)data.get("expend");
			float acbBalance = (float)lastBalance.get("balance") - (float)data.get("income") + (float)data.get("expend");
			StringBuffer acbSql = new StringBuffer();
			acbSql.append("INSERT INTO `{account_balance}` SET ");
			acbSql.append(String.format("`total`=%.2f,", acbTotal));
			acbSql.append(String.format("`balance`=%.2f,", acbBalance));
			acbSql.append(String.format("`income`=%.2f,", data.get("income")));
			acbSql.append(String.format("`expend`=%.2f,", data.get("expend")));
			acbSql.append(String.format("`type`='%s',", data.get("type")));
			acbSql.append(String.format("`money`=%.2f,", data.get("money")));
			acbSql.append(String.format("`user_id`=%d,", data.get("user_id")));
			acbSql.append(String.format("`nid`='%s',", data.get("nid")));
			acbSql.append(String.format("`remark`='%s',", data.get("remark")));
			acbSql.append(String.format("`addtime`=%d,", System.currentTimeMillis()/1000));
			acbSql.append("`addip`='127.0.0.1'");
			Mysql.instance().insert(acbSql.toString());

		}

		if (data.get("account_web_status").equals(1)) {
			Map<String, Object> accWeb = Mysql.instance().queryOne(
					"SELECT total,balance FROM `{account_web}` ORDER BY id DESC"
					);
			if (accWeb.isEmpty()) {
				accWeb.put("total", 0.00);
				accWeb.put("balance", 0.00);
			}
			float acwTotal = (float)accWeb.get("total") + (float)data.get("income") + (float)data.get("expend");
			float acwBalance = (float)accWeb.get("balance") - (float)data.get("income") + (float)data.get("expend");
			StringBuffer acwSql = new StringBuffer();
			acwSql.append("INSERT INTO `{account_web}` SET ");
			acwSql.append(String.format("`total`=%.2f,", acwTotal));
			acwSql.append(String.format("`balance`=%.2f,", acwBalance));
			acwSql.append(String.format("`income`=%.2f,", data.get("income")));
			acwSql.append(String.format("`expend`=%.2f,", data.get("expend")));
			acwSql.append(String.format("`type`='%s',", data.get("type")));
			acwSql.append(String.format("`money`=%.2f,", data.get("money")));
			acwSql.append(String.format("`user_id`=%d,", data.get("user_id")));
			acwSql.append(String.format("`nid`='%s',", data.get("nid")));
			acwSql.append(String.format("`remark`='%s',", data.get("remark")));
			acwSql.append(String.format("`addtime`=%d,", System.currentTimeMillis()/1000));
			acwSql.append("`addip`='127.0.0.1'");
			Mysql.instance().insert(acwSql.toString());
		}
		
		if (data.get("account_user_status").equals(1)) {
			Map<String, Object> acuWeb = Mysql.instance().queryOne(
					"SELECT total,balance FROM `{account_users}` ORDER BY id DESC"
					);
			if (acuWeb.isEmpty()) {
				acuWeb.put("total", 0.00);
				acuWeb.put("balance", 0.00);
			}
			float acuTotal = (float)acuWeb.get("total") + (float)data.get("income") + (float)data.get("expend");
			float acuBalance = (float)acuWeb.get("balance") - (float)data.get("income") + (float)data.get("expend");
			StringBuffer acuSql = new StringBuffer();
			acuSql.append("INSERT INTO `{account_users}` SET ");
			acuSql.append(String.format("`total`=%.2f,", acuTotal));
			acuSql.append(String.format("`balance`=%.2f,", acuBalance));
			acuSql.append(String.format("`income`=%.2f,", data.get("income")));
			acuSql.append(String.format("`expend`=%.2f,", data.get("expend")));
			acuSql.append(String.format("`type`='%s',", data.get("type")));
			acuSql.append(String.format("`money`=%.2f,", data.get("money")));
			acuSql.append(String.format("`user_id`=%d,", data.get("user_id")));
			acuSql.append(String.format("`nid`='%s',", data.get("nid")));
			acuSql.append(String.format("`remark`='%s',", data.get("remark")));
			acuSql.append(String.format("`addtime`=%d,", System.currentTimeMillis()/1000));
			acuSql.append("`addip`='127.0.0.1'");
			Mysql.instance().insert(acuSql.toString());
		}
		return true;
	}
}
