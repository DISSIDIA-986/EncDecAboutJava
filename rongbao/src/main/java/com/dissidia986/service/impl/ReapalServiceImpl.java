package com.dissidia986.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dissidia986.model.ReturnResult;
import com.dissidia986.service.ReapalService;
import com.dissidia986.util.DateUtil;
import com.dissidia986.util.WtUtils;

@PropertySource("classpath:reapal-credit90.properties")
@Service
public class ReapalServiceImpl implements ReapalService {
	@Value("${reapal.merchant_id}")
	private String merchantID;
	@Value("${reapal.seller_email}")
	private String sellerEmail;
	@Value("${reapal.currency}")
	private String currency;
	@Value("${reapal.notify_url}")
	private String notifyUrl;
	@Value("${reapal.sign_type}")
	private String signType;
	@Value("${reapal.cert_type}")
	private String certType;
	final String batchContent = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s";
	@Value("${reapal.agentpay.payaction}")
	private String payaction;
	@Value("${reapal.agentpay.signkey}")
	private String agentpaySignkey;
	@Value("${reapal.quickpay.signkey}")
	private String quickpaySignkey;
	@Value("${reapal.agentpay.signType}")
	private String paysignType;
	@Value("${reapal.agentpay.batchBizid}")
	private String batchBizid;
	@Value("${reapal.agentpay._input_charset}")
	private String _input_charset;
	@Value("${reapal.agentpay.batchBiztype}")
	private String batchBiztype;
	@Value("${reapal.agentpay.batchVersion}")
	private String batchVersion;
	@Value("${reapal.agentpay.batchCount}")
	private String batchCount;
	@Value("${reapal.agentpay.payqueryaction}")
	private String payqueryaction;
	@Value("${reapal.agentpay.paysinglequery}")
	private String paysinglequery;
	@Value("${morsharePrivateKey}")
	private String morsharePrivateKey;
	@Value("${morsharePublicKey}")
	private String morsharePublicKey;
	@Value("${rongbaoPrivateKey}")
	private String rongbaoPrivateKey;
	@Value("${rongbaoPublicKey}")
	private String rongbaoPublicKey;
	@Value("${url}")
	private String urlPrefix;
	@Value("${morsharePrivateKey.password}")
	private String morsharePrivateKeyPassword;
	@Value("${morsharePrivateKey.alias}")
	private String morsharePrivateKeyAlias;
	@Value("${rongbaoPrivateKey.password}")
	private String rongbaoPrivateKeyPassword;
	@Value("${rongbaoPrivateKey.alias}")
	private String rongbaoPrivateKeyAlias;
	final SimpleDateFormat sdfYmd = DateUtil.sdfYmd();
	final SimpleDateFormat sdfyMdhms = DateUtil.sdfyMdhms();
	private static Logger logger = LoggerFactory.getLogger(ReapalServiceImpl.class);
	private final Random random = new Random(100000);
	@Transactional
	public ReturnResult subscribeBindDebitCard(String merchant_id, String card_no, String owner, String cert_type,
			String cert_no, String phone, String currency, String total_fee, String title, String body,
			String member_id, String terminal_type, String terminal_info, String member_ip, String seller_email,
			String notify_url, String token_id, String sign_type,String open_bank,String branch_bank,String sub_bank,String open_bank_province,String open_bank_city) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(0);
		map.put("merchant_id", merchant_id);
		map.put("card_no", card_no);
		map.put("owner", owner);
		map.put("cert_type", cert_type);
		map.put("cert_no", cert_no);
		map.put("phone", phone);
		String order_no = WtUtils.createReapalOrderID();
		map.put("order_no", order_no);
		map.put("transtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		map.put("currency", currency);
		map.put("title", title);
		map.put("body", body);
		map.put("member_id", member_id);
		map.put("terminal_type", terminal_type);
		map.put("terminal_info", terminal_info);
		map.put("member_ip", member_ip);
		map.put("seller_email", seller_email);
		map.put("notify_url", notify_url);
		map.put("token_id", token_id);
		map.put("sign_type", sign_type);
		BigDecimal total_fee_decimal = new BigDecimal(total_fee).movePointRight(2);
		map.put("total_fee", total_fee_decimal.toString());
		//TODO 开户行等代付所需的额外信息
		String reqStr = WtUtils.writeObjectAsString(map);
		//MD5(a=v1&b=v2abcdef)
		String mysign = WtUtils.buildReapalSign(map, quickpaySignkey,null);
		System.out.println("验签之前==========>" + mysign);

		map.put("sign", mysign);
		
		String json = WtUtils.writeObjectAsString(map);
		
		Map<String, String> maps = WtUtils.createFinalMap(json,morsharePublicKey);
		maps.put("merchant_id", merchant_id);
		long currentTime = System.currentTimeMillis();
		String URI = urlPrefix + "/fast/debit/portal";
		
		String response = WtUtils.reapalPost(URI, maps);

		System.out.println("返回结果==========>" + response);
		Map<String,Object> resMap = WtUtils.decryptReapalResp(response, morsharePrivateKey, morsharePrivateKeyAlias, morsharePrivateKeyPassword, agentpaySignkey);
		System.err.println("充值预约耗时:"+(System.currentTimeMillis()-currentTime)+" ms");
		
		if(resMap!=null && resMap.get("result_code").toString().equals("0000")){
			
			return new ReturnResult("1","充值预约成功",resMap);
		}
		return new ReturnResult("2","充值预约失败",resMap);
	}
	@Transactional
	public ReturnResult boundPay(String merchant_id, String bind_id, String currency, String total_fee, String title, String body,
			String member_id, String terminal_type, String terminal_info, String member_ip, String seller_email) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(0);
		map.put("merchant_id", merchant_id);
		 map.put("bind_id", bind_id);
		String order_no = WtUtils.createReapalOrderID();
		map.put("order_no", order_no);
		map.put("transtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		map.put("currency", currency);
		map.put("title", title);
		map.put("body", body);
		map.put("member_id", member_id);
		map.put("terminal_type", terminal_type);
		map.put("terminal_info", terminal_info);
		map.put("member_ip", member_ip);
		map.put("seller_email", seller_email);
		BigDecimal total_fee_decimal = new BigDecimal(total_fee).movePointRight(2);
		map.put("total_fee", total_fee_decimal.toString());
		String mysign = WtUtils.buildReapalSign(map, quickpaySignkey,null);// 生成签名结果

		System.out.println("验签之前==========>" + mysign);

		map.put("sign", mysign);

		String json = WtUtils.writeObjectAsString(map);

		Map<String, String> maps = WtUtils.createFinalMap(json,morsharePublicKey);
		maps.put("merchant_id", merchant_id);
		
		long currentTime = System.currentTimeMillis();
		String URI = urlPrefix + "/fast/bindcard/portal";
		String response = WtUtils.reapalPost(URI, maps);
		System.err.println("绑定充值预约耗时:"+(System.currentTimeMillis()-currentTime)+" ms");
		
		System.out.println("返回结果post==========>" + response);
		Map<String,Object> resMap = WtUtils.decryptReapalResp(response, morsharePrivateKey, morsharePrivateKeyAlias, morsharePrivateKeyPassword, agentpaySignkey);
		if(resMap!=null && resMap.get("result_code").toString().equals("0000")){
			return new ReturnResult("1","充值预约成功",resMap);
		}
		return new ReturnResult("2","充值预约失败",resMap);
	}
	@Transactional
	public ReturnResult boundPay(String bind_id,String total_fee, String title, String body,
			String member_id, String terminal_type, String terminal_info, String member_ip) throws Exception {
		return boundPay(merchantID,bind_id,currency,total_fee,title,body,member_id,terminal_type,terminal_info,member_ip,sellerEmail);
	}
	@Transactional
	public ReturnResult subscribeBindDebitCard(String card_no, String owner, String cert_no, String phone, String total_fee,
			String title, String body, String member_id, String terminal_type, String terminal_info, String member_ip,
			String token_id,String open_bank,String branch_bank,String sub_bank,String open_bank_province,String open_bank_city) throws Exception {
		return subscribeBindDebitCard(merchantID, card_no, owner, certType, cert_no, phone, currency, total_fee, title,
				body, member_id, terminal_type, terminal_info, member_ip, sellerEmail, notifyUrl, token_id, signType,open_bank,branch_bank,sub_bank,open_bank_province,open_bank_city);
	}
	@Transactional
	public ReturnResult BindDebitCardPayConfirm(String order_no, String check_code,String user_id,String bind_id,String money) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(0);
		map.put("merchant_id", merchantID);
		map.put("order_no", order_no);
		map.put("check_code", check_code);

		StringBuilder reqStr = WtUtils.CreateLinkString(map);
		String mysign = WtUtils.buildReapalSign(map, agentpaySignkey,null);// 生成签名结果

		System.out.println("验签之前==========>" + mysign);
		map.put("sign", mysign);
		String json = (String) WtUtils.writeObjectAsString(map);

		Map<String, String> maps = WtUtils.createFinalMap(json,morsharePublicKey);
		maps.put("merchant_id", merchantID);
		long currentTime = System.currentTimeMillis();
		String URI = urlPrefix + "/fast/pay";
		String response = WtUtils.reapalPost(URI, maps);
		System.err.println("充值确认耗时:"+(System.currentTimeMillis()-currentTime)+" ms");
		
		Map<String,Object> resMap = WtUtils.decryptReapalResp(response, morsharePrivateKey, morsharePrivateKeyAlias, morsharePrivateKeyPassword, agentpaySignkey);
		if(resMap!=null && resMap.get("result_code").toString().equals("0000")){
			return new ReturnResult("1","充值成功",resMap);
		}
		return new ReturnResult("2","充值失败",resMap);
	}

	@Transactional
	public ReturnResult agentPay(String realName, String identity,String mobile,String total_fee, String card_no, String open_bank, String branch_bank,
			String sub_bank, String open_bank_province, String open_bank_city) {
		try {

			Calendar now = Calendar.getInstance();
			String batchDate = sdfYmd.format(now.getTime());
			String userProtocolNum = sdfyMdhms.format(now.getTime());
			String batchCurrnum = WtUtils.createNonce().toString() + random.nextInt(10000);
			long serialNum = now.getTimeInMillis();
			String order_no = WtUtils.createReapalOrderID();
			String batch = "";
			batch = String.format(batchContent, serialNum, card_no, realName, open_bank, branch_bank,
					sub_bank, "私", total_fee, "CNY", open_bank_province, open_bank_city, mobile, "身份证",
					identity, userProtocolNum, order_no,
					mobile + "提现");
			Map<String, String> sPara = new HashMap<String, String>();
			sPara.put("batchBizid", batchBizid);
			sPara.put("batchVersion", batchVersion);
			sPara.put("batchBiztype", batchBiztype);
			sPara.put("batchDate", batchDate);
			sPara.put("batchCurrnum", batchCurrnum);
			sPara.put("batchCount", batchCount);
			sPara.put("batchAmount", total_fee);
			sPara.put("batchContent", batch);
			sPara.put("_input_charset", "utf8");
			String sign = WtUtils.buildReapalSign(sPara, agentpaySignkey,"utf8");
			String batchContentEncrypted = WtUtils.encryptByRongbaoPublicKey(batch, rongbaoPublicKey,"utf8");
			logger.debug("batchContent融宝公钥加密后:{}",batchContentEncrypted);
			sPara.put("batchContent", batchContentEncrypted);
			sPara.put("sign", sign);
			long currentTime = System.currentTimeMillis();
			String response = WtUtils.reapalPost(payaction, sPara);
			String results = WtUtils.unescapeChineseUnicode(response);
			logger.debug("转义前:{}|转义后:{}",response,results);
			logger.debug("提现耗时:{} ms",(System.currentTimeMillis()-currentTime));
			if(StringUtils.containsIgnoreCase(results,"succ")){
				logger.warn("realName={}|batchDate={}|batchCurrnum={}|serialNum={}|order_no={}|提现",realName,batchDate,batchCurrnum,serialNum,order_no);
				return result("1","提现成功","");
			}else{
				return domParse(results);
			}
		} catch (Exception e) {
			logger.error("代付接口异常:", e);
			return result("2", "系统异常,取现失败", "");
		}
	}
	
	@Transactional
	public ReturnResult queryOrder(String order_no) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(0);
		map.put("merchant_id", merchantID);
		map.put("order_no", order_no);

		StringBuilder reqStr = WtUtils.CreateLinkString(map);
		String mysign = WtUtils.buildReapalSign(map, agentpaySignkey,null);// 生成签名结果

		System.out.println("验签之前==========>" + mysign);
		map.put("sign", mysign);
		map.put("sign_type", signType);
		String json = (String) WtUtils.writeObjectAsString(map);

		Map<String, String> maps = WtUtils.createFinalMap(json,morsharePublicKey);
		maps.put("merchant_id", merchantID);
		long currentTime = System.currentTimeMillis();
		String URI = urlPrefix + "/fast/search";
		String response = WtUtils.reapalPost(URI, maps);
		System.err.println("充值确认耗时:"+(System.currentTimeMillis()-currentTime)+" ms");
		
		Map<String,Object> resMap = WtUtils.decryptReapalResp(response, morsharePrivateKey, morsharePrivateKeyAlias, morsharePrivateKeyPassword, agentpaySignkey);
		if(resMap!=null && resMap.get("result_code").toString().equals("0000")){
			return new ReturnResult("1","查询成功",resMap);
		}
		return new ReturnResult("2","查询失败",resMap);
	}
	
	// 返回方法
	private static ReturnResult result(String code, String msg, Object data) {
		ReturnResult returnResult = new ReturnResult(code, msg, data);
		return returnResult;
	}
	
	private static ReturnResult domParse(String content){
		try{
			//读取文件 转换成Document  
	        Document document = getDocument(content);
	        //获取根节点元素对象  
	        Element root = document.getRootElement();  
	        System.out.println("-------添加节点前------");  
	        //获取节点Resp
	        String status ="";
        	String reason = "";
	        for(Object e:root.elements()){
	        	Element el = (Element)e;
	        	if(el.getName().equals("status")){
	        		status=el.getText();
	        	}
	        	if(el.getName().equals("reason")){
	        		reason=el.getText();
	        	}
	        }
	        return result(status.equals("succ")?"1":"2",reason,"");
		}catch(Exception e){
			logger.error("XML解析异常:",e);
			return result("2","系统异常,支付失败","XML解析异常");
		}
		
	}
	public static Document getDocument(String content) throws DocumentException {
        return DocumentHelper.parseText(content);
    }
	
	/**
	 * 出金查询接口
	 * http://entrust.reapal.com/agentpay/payquery
	 */
	public ReturnResult agentPayQuery(String batchDate, String batchCurrnum) {
		try {
			Map<String, String> sPara = new HashMap<String, String>();
			sPara.put("batchBizid", batchBizid);
			sPara.put("batchVersion", batchVersion);
			sPara.put("batchDate", batchDate);
			sPara.put("batchCurrnum", batchCurrnum);
			sPara.put("_input_charset", "utf8");
			String sign = WtUtils.buildReapalSign(sPara, agentpaySignkey,"utf8");
			sPara.put("signType", signType);
			sPara.put("sign", sign);
			long currentTime = System.currentTimeMillis();
			String response = WtUtils.reapalPost(payqueryaction, sPara);
			//分行合并
			response = response.replace("\n", "").replace("\r", "").replaceAll("\\s*", "").replaceAll(" ", "");
			logger.debug("换行重新合并后:{}",response.toString());
			response = WtUtils.decryptByRongbaoPrivateKey(response.toString(), rongbaoPrivateKey, rongbaoPrivateKeyAlias, rongbaoPrivateKeyPassword, "utf8");
			String results = WtUtils.unescapeChineseUnicode(response.toString());
			logger.debug("转义前:{}|转义后:{}",response,results);
			logger.debug("提现耗时:{} ms",(System.currentTimeMillis()-currentTime));
			if(StringUtils.containsIgnoreCase(results,"fail")){
				return domParse(results);
			}else{
				return result("1","查询成功",results);
			}
		} catch (Exception e) {
			logger.error("代付接口异常:", e);
			return result("2", "系统异常,查询支付记录失败", "");
		}
	}
	/**
	 * 代付出金查询批次明细接口
	          请求参数列表
	          查询订单提交地址: http://entrust.reapal.com/agentpay/paysinglequery?
	 * @param batchDate
	 * @param batchCurrnum
	 * @return
	 */
	public ReturnResult paysinglequery(String batchDate, String batchCurrnum,String tradenum,String tradecustorder){
		try {
			Map<String, String> sPara = new HashMap<String, String>();
			sPara.put("batchBizid", batchBizid);
			sPara.put("batchVersion", batchVersion);
			sPara.put("batchDate", batchDate);
			sPara.put("batchCurrnum", batchCurrnum);
			sPara.put("_input_charset", "utf8");
			sPara.put("tradenum", tradenum);
			sPara.put("tradecustorder", tradecustorder);
			String sign = WtUtils.buildReapalSign(sPara, agentpaySignkey,"utf8");
			sPara.put("signType", signType);
			sPara.put("sign", sign);
			long currentTime = System.currentTimeMillis();
			String response = WtUtils.reapalPost(paysinglequery, sPara);
			//分行合并
			response = response.replace("\n", "").replace("\r", "").replaceAll("\\s*", "").replaceAll(" ", "");
			logger.debug("换行重新合并后:{}",response.toString());
			response = WtUtils.decryptByRongbaoPrivateKey(response.toString(), rongbaoPrivateKey, rongbaoPrivateKeyAlias, rongbaoPrivateKeyPassword, "utf8");
			String results = WtUtils.unescapeChineseUnicode(response.toString());
			logger.debug("转义前:{}|转义后:{}",response,results);
			logger.debug("提现耗时:{} ms",(System.currentTimeMillis()-currentTime));
			if(StringUtils.containsIgnoreCase(results,"fail")){
				return domParse(results);
			}else{
				return result("1","查询成功",results);
			}
		} catch (Exception e) {
			logger.error("代付接口异常:", e);
			return result("2", "系统异常,查询支付记录失败", "");
		}
	}
	/**
	 * 快捷支付-卡信息查询
	 * @return {"bank_card_type":"0","bank_code":"CCB","bank_name":"建设银行","card_no":"银行卡号","merchant_id":"商户号"}
	 */
	@Transactional
	public ReturnResult queryCardInfo(String card_no) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(0);
		map.put("merchant_id", merchantID);
		map.put("card_no", card_no);

		StringBuilder reqStr = WtUtils.CreateLinkString(map);
		String mysign = WtUtils.buildReapalSign(map, quickpaySignkey,null);// 生成签名结果

		
		map.put("sign", mysign);
		String json = (String) WtUtils.writeObjectAsString(map);

		Map<String, String> maps = WtUtils.createFinalMap(json,morsharePublicKey);
		maps.put("merchant_id", merchantID);
		long currentTime = System.currentTimeMillis();
		String URI = urlPrefix + "/fast/bankcard/list";
		String response = WtUtils.reapalPost(URI, maps);
		System.err.println("查询卡信息耗时:"+(System.currentTimeMillis()-currentTime)+" ms");
		
		Map<String,Object> resMap = WtUtils.decryptReapalResp(response, morsharePrivateKey, morsharePrivateKeyAlias, morsharePrivateKeyPassword, agentpaySignkey);
		
		if(resMap!=null && resMap.containsKey("bank_name")){
			return new ReturnResult("1","查询卡信息成功",resMap);
		}
		return new ReturnResult("2","查询卡信息失败",resMap);
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
