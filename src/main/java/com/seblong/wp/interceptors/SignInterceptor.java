package com.seblong.wp.interceptors;

import com.seblong.wp.utils.MapUtil;
import com.seblong.wp.utils.ResponseUtil;
import com.seblong.wp.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SignInterceptor extends HandlerInterceptorAdapter {

	@Value("${sign.key}")
	private String key;

	@Value("${sign.check.coerce}")
	private boolean signCheck;

	@Value("${sign.wx.applet.key}")
	private String wxKey;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		boolean mp = false;
		String mpStr = request.getParameter("mp");
		if (!StringUtil.isEmpty(mpStr)) {
			mp = Boolean.valueOf(mpStr);
		}
		if (mp) {
			Map<String, Object> map = new HashMap<>();
			map = MapUtil.getParameterMap(request);
			String signStr = request.getParameter("signToken");
			if (StringUtil.isEmpty(signStr)) {
				ResponseUtil.outputJson(403, "require-sign-token", response);
				return false;
			}
			map.remove("signToken");
			String sign = MapUtil.getWxAppletSign(map, wxKey);
			if (!sign.equals(signStr)) {
				ResponseUtil.outputJson(403, "error-sign-token", response);
				return false;
			}
			return true;
		} else {
			if (!signCheck) {
				return true;
			} else {
				if (StringUtil.isEmpty(request.getParameter("nonce_str"))) {
					ResponseUtil.outputJson(403, "require-nonce-str", response);
					return false;
				}
			}
			Map<String, Object> map = new HashMap<>();
			map = MapUtil.getParameterMap(request);
			String signStr = request.getParameter("signToken");
			if (StringUtil.isEmpty(signStr)) {
				ResponseUtil.outputJson(403, "require-sign-token", response);
				return false;
			}
			map.remove("signToken");
			String sign = MapUtil.getSign(map, key);
			if (!sign.equals(signStr)) {
				ResponseUtil.outputJson(403, "error-sign-token", response);
				return false;
			}
			return true;
		}
	}

}
