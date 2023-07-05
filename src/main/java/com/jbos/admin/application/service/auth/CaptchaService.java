package com.jbos.admin.application.service.auth;

import com.jbos.admin.common.utils.StringUtils;
import com.jbos.admin.infrastructure.repository.auth.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CaptchaService
 * @author youfu.wang
 * @date 2019-01-31
 */
@Service
public class CaptchaService {


	@Autowired
	private CaptchaRepository captchaRepository;
	/**
	 * 根据令牌查询验证码信息
	 * @param token
	 * @return
	 */
	public boolean validate(String token,String text){
		Object data=null;
		data=captchaRepository.getCaptcha(token);
		//每次验证则删除验证码数据
		this.deleteCaptcha(token);
		if(data==null){
			return false;
		}else {
			if(text.equals(StringUtils.replaceNull(data))){
				return true;
			}else{
				return false;
			}
		}
	}

	/**
	 * 新增验证码信息
	 * @param text
	 * @param token
	 */
	public void addCaptcha(String text,String token){
		captchaRepository.addCaptcha(text,token);
	}

	/**
	 * 删除验证码信息
	 * @param token
	 */
	public void deleteCaptcha(String token){
		captchaRepository.deleteCaptcha(token);
	}
}
