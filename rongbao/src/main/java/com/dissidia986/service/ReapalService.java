package com.dissidia986.service;

import com.dissidia986.model.ReturnResult;

public interface ReapalService {
	public ReturnResult subscribeBindDebitCard(String merchant_id, String card_no, String owner, String cert_type,
			String cert_no, String phone, String currency, String total_fee, String title, String body,
			String member_id, String terminal_type, String terminal_info, String member_ip, String seller_email,
			String notify_url, String token_id, String sign_type,String open_bank,String branch_bank,String sub_bank,String open_bank_province,String open_bank_city) throws Exception;

	public ReturnResult subscribeBindDebitCard(String card_no, String owner,
			String cert_no, String phone, String total_fee,
			String title, String body, String member_id, String terminal_type, String terminal_info, String member_ip, String token_id,String open_bank,String branch_bank,String sub_bank,String open_bank_province,String open_bank_city) throws Exception;

	public ReturnResult BindDebitCardPayConfirm(String order_no, String check_code,String user_id,String bind_id,String money) throws Exception;
	
	public ReturnResult agentPay(String realName, String identity,String mobile,String total_fee, String card_no, String open_bank, String branch_bank,
			String sub_bank, String open_bank_province, String open_bank_city);
	
	public ReturnResult agentPayQuery(String batchDate,String batchCurrnum);
	
	public ReturnResult boundPay(String bind_id,String total_fee, String title, String body,
			String member_id, String terminal_type, String terminal_info, String member_ip) throws Exception;
	
	ReturnResult boundPay(String merchant_id, String bind_id, String currency, String total_fee, String title, String body,
			String member_id, String terminal_type, String terminal_info, String member_ip, String seller_email) throws Exception;
	
	ReturnResult queryCardInfo(String card_no) throws Exception;
	ReturnResult queryOrder(String order_no) throws Exception;
}
