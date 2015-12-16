package com.dissidia986.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 公用函数
 * @author dissidia986
 *
 */
public class WtUtils {
	private final static Logger logger = LoggerFactory.getLogger(WtUtils.class);
	/**
	 * 手机号、邮箱、用户名、密码、有限长度输入框、正数、短信验证码、中文姓名、银行卡号正则
	 */
	private static final String MOBILE_PATTERN="^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String USERNAME_PATTERN="^[a-zA-Z0-9_-]{3,15}";
	private static final String PASSWORD_PATTERN="^[a-zA-Z0-9_-]{6,18}";
	private static final String IDCARD_PATTERN="\\d{15}|\\d{18}|\\d{17,17}X";
	private static final String LIMIT_TXT_PATTERN="^.{2,20}$";
	private static final String POSITIVE_NUM_PATTERN="^\\+?[1-9][0-9]*$";
	private static final String SMS_VERIFYCODE="\\d{6}";
	private static final String CHINESE_NAME_PATTERN="[\u4E00-\u9FA5]{2,12}";
	private static final String BANK_CARD_NUM_PATTERN="\\d{16}|\\d{19}";
	//线程安全的计数器
	private static final AtomicLong incremental = new AtomicLong(System.currentTimeMillis());
	//26个字母，0-9
	private static final String CHARS_SET ="01223456789abcdefghijklmnoopqrstuvwxyz";
	final static String USER_AGENT = "Mozilla/5.0 (compatible; MSIE 6.0 compatible;)" ;
    final static HttpClientContext context = HttpClientContext.create();
	/**
	 * 使用ThreadLocal，避免重复初始化
	 */
	private static ThreadLocal<ObjectMapper>  mapper =new ThreadLocal<ObjectMapper>(){
		@Override
		public ObjectMapper initialValue() {
			ObjectMapper _mapper = new ObjectMapper();
			_mapper.getDeserializationConfig().with(DateUtil.getNorm_datetime_format());
			_mapper.getSerializationConfig().with(DateUtil.getNorm_datetime_format());
			//TODO 允许JSON串的key不用双引号包括
			_mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			// to allow C/C++ style comments in JSON (non-standard, disabled by default)
			_mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			// to allow (non-standard) unquoted field names in JSON:
			_mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			// to allow use of apostrophes (single quotes), non standard
			_mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

			// JsonGenerator.Feature for configuring low-level JSON generation:

			// to force escaping of non-ASCII characters:
			_mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			return _mapper;
		}
	};
	/**
	 * 提取URL中的参数键值对
	 * @param url URL
	 * @return
	 * @throws URISyntaxException
	 */
	public static List<NameValuePair> getQueryMap(String url) throws URISyntaxException {
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		return params;
	}
	
	/**
	 * 获取HttpRequest的真实IP
	 * 
	 * @return
	 */
	public static String findRealIpUtil(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// String ip = request.getParameter("ip");
		return ip;
	}

	/**
	 *  TODO 如果字符串超过限制则截取，避免数据库保存时溢出
	 * @param origin 原始信息
	 * @param limit 限制长度
	 * @return 超过则进行截取，否则返回原始信息
	 */
	public static String subIfOutOfLimit(String origin, int limit) {
		String result = origin;
		if (StringUtils.isNotEmpty(origin) && origin.length() > limit) {
			result = origin.substring(0, limit);
		}
		return result;
	}

	public static Long createNonce() {

		return incremental.incrementAndGet();
	}

	public static String createSortedUrl(Map<String, Object> params) {

		// use a TreeMap to sort the headers and parameters
		TreeMap<String, String> headersAndParams = new TreeMap<String, String>();
		for(String key : params.keySet()){
			headersAndParams.put(key, params.get(key).toString());
		}
		return createSortedUrl(headersAndParams);
	}

	/**
	 * 按参数的字母顺序排序后拼接URL的queryString
	 * @param params
	 * @return
	 */
	public static String createSortedUrl(TreeMap<String, String> headersAndParams) {
		// build the url with headers and parms sorted
		String params = "";
		for (String key : headersAndParams.keySet()) {
			if (params.length() > 0) {
				params += "@";
			}
			params += key + "=" + headersAndParams.get(key).toString();
		}
		return params;
	}
	
	public static boolean isUsername(String txt){
		Pattern p = Pattern.compile(USERNAME_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isPassword(String txt){
		Pattern p = Pattern.compile(PASSWORD_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile(MOBILE_PATTERN);  
		Matcher m = p.matcher(mobiles);  
		return m.matches();
	}
	
	public static boolean isEmail(String email){
		Pattern p = Pattern.compile(EMAIL_PATTERN);  
		Matcher m = p.matcher(email);  
		return m.matches();
	}
	/**
	 * 根据身份证的checksum检查身份证号码是否有效
	 * @param idnum
	 * @return
	 */
	public static boolean isIDCard(String idnum){
		return checkIdCardInfo(idnum).isTrue;
	}
	public static boolean isLimitTxt(String txt){
		Pattern p = Pattern.compile(LIMIT_TXT_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isPositiveNum(String txt){
		Pattern p = Pattern.compile(POSITIVE_NUM_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isSMSVerifyCode(String txt){
		Pattern p = Pattern.compile(SMS_VERIFYCODE);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isChineseName(String txt){
		Pattern p = Pattern.compile(CHINESE_NAME_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isBankCard(String cardnum){
		Pattern p = Pattern.compile(BANK_CARD_NUM_PATTERN);  
		Matcher m = p.matcher(cardnum);  
		return m.matches();
	}

	public static class Info {
		private boolean isTrue = false;
		private String year = null;
		private String month = null;
		private String day = null;
		private boolean isMale = false;
		private boolean isFemale = false;
		@Override
		public String toString() {
			return "Info [isTrue=" + isTrue + ", year=" + year + ", month=" + month + ", day=" + day + ", isMale="
					+ isMale + ", isFemale=" + isFemale + "]";
		}
		
	}
	public static Info checkIdCardInfo(String cardNo){
		if(!cardNo.isEmpty())
			cardNo = cardNo.toUpperCase();
		Info info = new Info();
		Pattern pt = Pattern.compile(IDCARD_PATTERN);  
		Matcher m = pt.matcher(cardNo);
		if (!m.matches()) {
			info.isTrue = false;
			return info;
		}
		if (15 == cardNo.length()) {
			String year = cardNo.substring(6, 8);
			String month = cardNo.substring(8, 10);
			String day = cardNo.substring(10, 12);
			String p = cardNo.substring(14, 15); //性别位
			Date birthday = new Date();
			birthday.setYear(Integer.valueOf(year));
			birthday.setMonth(Integer.parseInt(month)-1);
			birthday.setDate(Integer.parseInt(day));
			// 对于老身份证中的年龄则不需考虑千年虫问题而使用getYear()方法  
			if (birthday.getYear() != Integer.parseInt(year)
					|| birthday.getMonth() != Integer.parseInt(month) - 1
					|| birthday.getDate() != Integer.parseInt(day)) {
				info.isTrue = false;
			} else {
				info.isTrue = true;
				info.year = birthday.getYear()+"";
				info.month = (birthday.getMonth() + 1)+"";
				info.day = birthday.getDate()+"";
				if (Integer.valueOf(p) % 2 == 0) {
					info.isFemale = true;
					info.isMale = false;
				} else {
					info.isFemale = false;
					info.isMale = true;
				}
			}
			return info;
		}
		
		if (18 == cardNo.length()) {
			String year = cardNo.substring(6, 10);
			String month = cardNo.substring(10, 12);
			String day = cardNo.substring(12, 14);
			String p = cardNo.substring(14, 17);
			Date birthday = new Date();
			birthday.setYear(Integer.valueOf(year));
			birthday.setMonth(Integer.parseInt(month)-1);
			birthday.setDate(Integer.parseInt(day));
			// 这里用getFullYear()获取年份，避免千年虫问题
			if (birthday.getYear() != Integer.parseInt(year)
					|| birthday.getMonth() != Integer.parseInt(month) - 1
					|| birthday.getDate() != Integer.parseInt(day)) {
				info.isTrue = false;
				return info;
			}
			int[] Wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };// 加权因子  
			int[] Y = { 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };// 身份证验证位值.10代表X 
			// 验证校验位
			int sum = 0; // 声明加权求和变量
			String[] _cardNo2 = cardNo.split("");
			String[] _cardNo = new String[_cardNo2.length-1];
			for(int i=0;i<_cardNo.length;i++){
				_cardNo[i]=_cardNo2[i+1];
			}
			if (_cardNo[17].toUpperCase().equals("X") ) {
				_cardNo[17] = "10";// 将最后位为x的验证码替换为10方便后续操作  
			}
			for ( int i = 0; i < 17; i++) {
//				System.out.println(_cardNo[i]);
				sum += Wi[i] * Integer.valueOf(_cardNo[i]);// 加权求和  
			}
			int i = sum % 11;// 得到验证码所位置
			if (!_cardNo[17].equals(Y[i]+"") ) {
				info.isTrue = false;
				return info;
			}
			info.isTrue = true;
			info.year = birthday.getYear()+"";
			info.month = (birthday.getMonth() + 1)+"";
			info.day = birthday.getDate()+"";
			if (Integer.valueOf(p) % 2 == 0) {
				info.isFemale = true;
				info.isMale = false;
			} else {
				info.isFemale = false;
				info.isMale = true;
			}
			return info;
		}
		return info;
	}
	
	/**
	 * 根据HTTP请求头User-Agent判断来源是否是移动设备
	 * @param txt User-Agent
	 * @return
	 */
	public static boolean isMobileHeader(String txt) {
		if (txt.matches(
				"(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*")
				|| txt.substring(0, 4).matches(
						"(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")) {
			return true;
		}
		return false;
	}
	/**
	 * 动态解析velocity的vm模板文件，可用于邮件模板、代码生成等
	 * @param velocityEngine
	 * @param templateLocation 模板的classpath位置
	 * @param model 动态参数
	 * @return
	 */
	public static String mergeTemplate(VelocityEngine velocityEngine, String templateLocation,Map<String,Object> model){
		model.put("DateTool", new DateTool());
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation,"UTF-8", model);
		//System.err.println(text);
		return text;
	}
	/**
	 * 获取线程安全的JSON序列化与反序列化转换器
	 * @return
	 */
	public static ObjectMapper getMapper(){
		return mapper.get();
	}
	/**
	 * JSON序列化
	 * @param obj 序列化对象
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String writeObjectAsString(Object obj) throws JsonProcessingException{
		return getMapper().writeValueAsString(obj);
	}
	/**
	 * JSON反序列化
	 * @param content JSON字符串
	 * @param valueType 结果的对象类型
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T readValue(String content, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException{
		return getMapper().readValue(content, valueType);
	}
	/**
	 * JSON反序列化
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static JsonNode readTree(String json) throws JsonProcessingException, IOException{
		return getMapper().readTree(json);
	}
	
	/** 
	 * 功能：把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串 去掉空值与签名参数后的新签名参数组
	 */
	public static StringBuilder CreateLinkString(Map params){
			List keys = new ArrayList(params.keySet());
			Collections.sort(keys);
	
			StringBuilder prestr = new StringBuilder();
			String key="";
			String value="";
			for (int i = 0; i < keys.size(); i++) {
				key=(String) keys.get(i);
				value = (String) params.get(key);
				if("".equals(value) || value == null || 
						key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")){
					continue;
				}
				if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
					prestr.append(key).append("=").append(value);
				} else {
					prestr.append(key).append("=").append(value).append("&");
				}
				
			}
			return prestr;
	}
	
	/**
	 * 将融宝支付POST过来反馈信息转换一下
	 * @param requestParams 返回参数信息
	 * @return Map 返回一个只有字符串值的MAP
	 * */
	public static Map transformRequestMap(Map requestParams){
		Map params = null;
		if(requestParams!=null && requestParams.size()>0){
			params = new HashMap();
			String name ="";
			String[] values =null;
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				name= (String) iter.next();
				values= (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				params.put(name, valueStr);
			}
		}
		return params;
	}
	
	public static CloseableHttpClient newInstance() {
		CloseableHttpClient hcInstance = null;
		try {
			hcInstance = HttpClients.custom().setConnectionManager(ConnectionManagerFactory.getInstance()).build();
		} catch (Exception e1) {
			
		}
		return hcInstance;
	}
	
	/**
     * 生成融宝支付的唯一订单号
     * @return
     */
    public static String createReapalOrderID(){
        String code = "10" + DateUtil.formatNumbericDate(new Date())+"" + new Random().nextInt(1000) ;
        return code;
    }
    
    /**
     * 请求参数按照参数名字符升序排列，如果有重复参数名，那么重复的参数再按照参数值的字符升序排列。
     * 所有参数（除了sign和sign_type）按照上面的排序用&连接起来，格式是：a=v1&b=v2。
     * 将上面参数组成的字符串加上安全校验码组成待签名的数据，安全校验码商户可通过登录商户平台系统下载，假设安全校验码为123456789，那计算sign的原串为a=v1&b=v212345678
     */
    public static String buildReapalSign(Map sArray, String signKey,String charset){
    	String result = null;
    	if(sArray!=null && sArray.size()>0){
            StringBuilder prestr = CreateLinkString(sArray);
            logger.error("加盐前:{}" ,prestr);
            //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            try{
            	result = (charset ==null || charset.isEmpty())?MD5Coder.encodeMD5Hex(prestr.toString()+signKey):MD5Coder.encodeMD5Hex(prestr.toString()+signKey,charset);
            	logger.debug("md5前后:{}|{}",prestr.toString()+signKey,result);
            }catch(Exception e){
            	logger.error("MD5异常:{}",e);
            }
            return result;
        }
        return result;
    }
    /**
     * 固定长度的随机字符串
     * @param count 长度
     * @return
     */
    public static String fixedLenRandom(int count){
    	return RandomStringUtils.random(count, CHARS_SET);
    }
    /**
     * 生成最终POST的map
     * 1:先随机生成16位字符串作为AES加密key
     * 2:通过公钥对(AES加密key)加密
     * 3:通过(AES加密key)对data加密
     * @param data MD5（源数据+signKey)的结果
     * @return 返回包含(merchant_id,data,encryptkey)的map
     * @throws Exception
     */
    public static Map<String, String> createFinalMap(String data,String certificatePath){
    	Map<String, String> finalMap = new HashMap<String, String>();
    	String AESkey = fixedLenRandom(16);
    	//AESkey= "36KRuy9wxB0ss81x";
    	byte[] aes_key_encrypted=null;
		try {
			aes_key_encrypted = CertificateCoderPfx.encryptByPublicKey(AESkey.getBytes(), certificatePath);
			String aes_key_encrypted_str = new String(Base64.encode(aes_key_encrypted),"UTF-8");
			logger.debug("加密前{}|加密后|{}",AESkey,aes_key_encrypted_str);
			String aes_encrypted_data = new String(Base64.encode(AESCoder.encrypt(data.getBytes("UTF-8"), AESkey.getBytes("UTF-8"))));
			finalMap.put("encryptkey", aes_key_encrypted_str);
			finalMap.put("data", aes_encrypted_data);
			logger.debug("补充签名前{}|签名后|{}",data,writeObjectAsString(finalMap));
		}catch (UnsupportedEncodingException e) {
			logger.error("对AES密钥加密异常:{}",e);
			return null;
		} catch (Exception e) {
			logger.error("对AES密钥加密异常:{}",e);
			return null;
		}
		return finalMap;
    }
    /**
     * 调用融宝快捷支付接口
     * @param reapal_url
     * @param paramMap
     * @return
     */
    public static String reapalPost(String reapal_url, Map<String, String> paramMap){
    	logger.debug("URL:{}请求{}",reapal_url,paramMap);
    	String response = null;
    	CloseableHttpClient httpclient = null;
    	CloseableHttpResponse chResp = null;
    	try{
    		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        	for(String key:paramMap.keySet()){
        		postParameters.add(new BasicNameValuePair(key, paramMap.get(key)));
        	}
        	URL url = new URL(reapal_url);
        	httpclient = newInstance(); 
    		HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
    		
    		HttpPost httppost = new HttpPost(reapal_url);
//    		httppost.addHeader("Content-Type", "text/html;charset=UTF-8");
    		//httppost.setEntity(new UrlEncodedFormEntity(postParameters,"UTF-8"));
    		httppost.setEntity(new UrlEncodedFormEntity(postParameters));
    		chResp = httpclient.execute(targetHost, httppost, context);
    		logger.debug("executing request" + httppost.getRequestLine());
    		HttpEntity entity = chResp.getEntity();
    		logger.debug(chResp.getStatusLine().toString());
    		if(entity==null){
				logger.error("reapal response is null.");
				return null;
			}else{
				response = EntityUtils.toString(entity);
			}
    	}catch(Exception e){
    		logger.error("融宝支付POST异常:{}",e);
    	}finally{
    		//关闭输出流
    		HttpClientUtils.closeQuietly(chResp);
//    		HttpClientUtils.closeQuietly(httpclient);
    	}
    	return response;
    }
    
    /**
     * 使用私钥对encryptkey解密，得到AESkey
     * @param response_encryptkey 融宝公钥加密的AESkey
     * @param keyStorePath 私钥库路径
     * @param keyStoreAlias 私钥别名
     * @param keyStorePassword 私钥密码
     * @return
     */
    public static String descryptAESKey(String response_encryptkey,String keyStorePath,String keyStoreAlias,String keyStorePassword){
    	byte[] decrypt_AES_secretkey = null;
		try {
			decrypt_AES_secretkey = CertificateCoderPfx.decryptByPrivateKey(response_encryptkey.getBytes(), keyStorePath, keyStoreAlias, keyStorePassword);
		} catch (Exception e) {
			logger.error("解析融宝快捷支付响应异常:{}",e);
		}
		String AES_secretkey = new String(decrypt_AES_secretkey);
		return AES_secretkey;
    }
    /**
     * 解析融宝快捷支付接口的响应体
     * 1:将响应体反序列化，提取data和encryptkey
     * 2:使用私钥对encryptkey解密，得到AESkey
     * 3:使用AESkey解密data，并将结果反序列化为JsonNode
     * 4:提取除(sign和sign_type)以外的数据，并重新MD5(a=v1&b=v2abcdef)
     * 5:数据完整性和一致性:核对sign和上一步结果是否一致，不一致则数据已发生篡改。
     * @param response
     * @return
     */
    public static Map<String,Object> decryptReapalResp(String response,String keyStorePath,String keyStoreAlias,String keyStorePassword,String signKey){
    	Map<String,Object> result = null;
    	try{
    		JsonNode dataMap = readTree(response);
        	String encryptkey = dataMap.get("encryptkey").asText();
        	String resposne_encryptData = dataMap.get("data").asText();
        	String AES_secretkey = descryptAESKey(encryptkey,keyStorePath,keyStoreAlias,keyStorePassword);
        	byte[] outputData = AESCoder.decrypt(org.bouncycastle.util.encoders.Base64.decode(resposne_encryptData.getBytes()), AES_secretkey.getBytes("UTF-8"));
    		String data =new String(outputData,"UTF-8");
    		Map<String,Object> dataMap2 = readValue(data,Map.class);
    		logger.error("解密后的响应:{}",data);
    		synchronized(dataMap2){
    			/**
    			 * 融宝支付响应没有签名验证机制，某种程度说是不安全的
    			String sign = (String) dataMap2.get("sign");
        		if(dataMap2.containsKey("sign")){
        			dataMap2.remove("sign");
        		}
        		if(dataMap2.containsKey("sign_type")){
        			dataMap2.remove("sign_type");
        		}
        		String check = buildReapalSign(dataMap2,signKey);
        		if(!check.equals(sign)){
        			logger.error("融宝快捷支付响应数据已被篡改:{}|{}",sign,check);
        		}
        		*/
        		result = dataMap2;
    		}
    		
    	}catch(Exception e){
    		logger.error("解密融宝快捷支付响应异常:{}",e);
    	}
    	return result;
    }
    
    /**
     * 融宝的公钥加密(模式="RSA/ECB/PKCS1Padding")
     * @param batchContent MD5（源数据+signKey)的结果
     * @param certificatePath 公钥文件路径
     * @return 加密内容
     * @throws Exception javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
     */
    public static String encryptByRongbaoPublicKey(String batchContent,String certificatePath,String charset){
		String batchContentEncryped = null;
		try {
			byte[] batchContentEncrypedBytes = CertificateCoderP12.encryptByPublicKey(batchContent.getBytes(charset), certificatePath);
			/*
			 *  Bouncy Castle与Commons Codec有差别：Bouncy Castle使用"."作为补位符，而Commons Codec则完全杜绝使用补位符。
			 *  Bouncy Castle的Base64 encode结果与JDK sun.misc.BASE64Encoder.BASE64Encoder encode结果相同
			 */
			batchContentEncryped = new String(org.bouncycastle.util.encoders.Base64.encode(batchContentEncrypedBytes));
		} catch (Exception e) {
			logger.error("解密融宝数据加密异常:{}",e);
		}
		logger.debug("RSA公钥加密前{}|RSA公钥加密后|{}",batchContent,batchContentEncryped);
		return batchContentEncryped;
    }
    /**
     * 融宝的公钥加密(模式="RSA/ECB/PKCS1Padding")
     * @param batchContent MD5（源数据+signKey)的结果
     * @param certificatePath 公钥文件路径
     * @return 加密内容
     * @throws Exception javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
     */
    public static String decryptByRongbaoPrivateKey(String batchContent,String keyStorePath,String keyStorepAssword,String keyStorePassword,String charset){
		String batchContentEncryped = null;
		try {
			//byte[] batchContentEncrypedBytes = CertificateCoderP12.decryptByPrivateKey(decoder.decodeBuffer(batchContent), keyStorePath, keyStorepAssword, keyStorePassword);
			byte[] batchContentEncrypedBytes = CertificateCoderP12.decryptByPrivateKey(org.bouncycastle.util.encoders.Base64.decode(batchContent), keyStorePath, keyStorepAssword, keyStorePassword);
			/*
			 *  Bouncy Castle与Commons Codec有差别：Bouncy Castle使用"."作为补位符，而Commons Codec则完全杜绝使用补位符。
			 *  Bouncy Castle的Base64 encode结果与JDK sun.misc.BASE64Encoder.BASE64Encoder encode结果相同
			 */
			batchContentEncryped = new String(batchContentEncrypedBytes,charset);
		} catch (Exception e) {
			logger.error("解密融宝数据加密异常:{}",e);
		}
		logger.debug("RSA私钥解密前{}|RSA私钥解密后|{}",batchContent,batchContentEncryped);
		return batchContentEncryped;
    }
    /**
      * 转义融宝代付接口响应中的中文unicode
     * 例如"<Resp><status>fail</status><reason>&#x7B7E;&#x540D;&#x5931;&#x8D25;</reason></Resp>" 转义为 "<Resp><status>fail</status><reason>签名失败</reason></Resp>"
     * @param response 可能包含unicode的字符串
     * @return 转为汉字的字符串
     */
    public static String unescapeChineseUnicode(String response){
    	String result = response;
    	if(response.contains("&#x")){
    		result = StringEscapeUtils.unescapeJava(response.replaceAll("(&#x)(\\w{4})(;)","\\\\u$2"));
    	}
    	return result;
    }
}
