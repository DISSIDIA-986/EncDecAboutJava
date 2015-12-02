package com.dissidia986.test.rongbao;

import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dissidia986.model.ReturnResult;
import com.dissidia986.service.ReapalService;
import com.dissidia986.util.WtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RongbaoTest extends TestBase {
	@Autowired ReapalService reapalService;
	
	//@Test
	public void agentPayQuery() throws JsonProcessingException {
		String batchDate="20151126";
		String batchCurrnum="14485227810617187";
		ReturnResult result=null;
		try {
			result = reapalService.agentPayQuery(batchDate,batchCurrnum);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	}

	//@Test
	public void agentPay() throws JsonProcessingException {
		ReturnResult result=null;
		try {
			String ownerName="持卡人姓名";
			String identity="持卡人身份证号";
			String mobile="持卡人手机号";
			String bankCardNo="银行卡号";
			String money="0.1";
			String openBank="建设银行";
			String branchBank="分行";
			String subBank="支行";
			String openBankProvince="开户省";
			String openBankCity="开户市";
			result=reapalService.agentPay(ownerName, identity, mobile, money, bankCardNo, openBank, branchBank, subBank, openBankProvince, openBankCity);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	}
	
	//@Test
	public void queryQuickOrder() throws JsonProcessingException {
		//String order_no="10151201112231051412";
		String order_no="1015120113412041247";
		ReturnResult result=null;
		try {
			result = reapalService.queryOrder(order_no);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	}
	/**
	 * 快捷-绑定支付(预约)
	 * @throws JsonProcessingException
	 */
	//@Test
	public void bindQuickPay() throws JsonProcessingException {
		String bind_id="5438";
		String total_fee="0.1";
		String title="持卡人手机号"+"充值";
		String order_no="1015120113412041247";
		String member_id="5438";
		String terminal_info = "持卡人手机号";
		
		ReturnResult result=null;
		try {
			result = reapalService.boundPay(bind_id,total_fee,title,total_fee,member_id,"mobile",terminal_info,"127.0.0.1");
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	}
	
	@Test
	public void queryCardInfoQuickPay() throws JsonProcessingException {
		String bankCardNo="银行卡号";
		
		ReturnResult result=null;
		try {
			result = reapalService.queryCardInfo(bankCardNo);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	} 
	

	/**
	 * 快捷-支付(预约)
	 * @throws JsonProcessingException
	 * {"bank_name":"建设银行","bind_id":"1395700","merchant_id":"100000000052211","order_no":"10151201152937999293","result_code":"0000","result_msg":"签约成功"}
	 * @return 预约的订单号 
	 */
	@Test
	public void subscribeQuickPay() throws JsonProcessingException {
		String ownerName="持卡人姓名";
		String identity="持卡人身份证号";
		String mobile="持卡人手机号";
		String bankCardNo="银行卡号";
		String money="0.1";
		String openBank="建设银行";
		String branchBank="分行";
		String subBank="支行";
		String openBankProvince="开户省";
		String openBankCity="开户市";
		String esn = "手机设备号";
		String userId="5438";
		
		ReturnResult result=null;
		try {
			result = reapalService.subscribeBindDebitCard(bankCardNo, ownerName, identity, mobile, money, mobile+"充值", money,
					userId, "mobile", esn, "127.0.0.1", UUID.randomUUID().toString().replaceAll("-", ""), openBank, branchBank, subBank,
					openBankProvince, openBankCity);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	}	
	
	/**
	 * 快捷-支付-短信验证码确认支付
	 * @throws JsonProcessingException
	 */
	@Test
	public void confirmQuickPay() throws JsonProcessingException {
		//上一步预约的订单号
		String orderNo="10151201152937999293";
		//短信验证码
		String captcha="848871";
		String bindId="1395700";
		String money="0.1";
		String userId="5438";
		
		ReturnResult result=null;
		try {
			result = reapalService.BindDebitCardPayConfirm(orderNo, captcha,userId,bindId,money);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ReturnResult("2", "系统错误");
		}
		logger.error(WtUtils.writeObjectAsString(result));
	}	
}
