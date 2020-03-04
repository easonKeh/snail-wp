package com.seblong.wp.utils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MapUtil {
	
	/**
	 * 对MAP根据key按照字母排序
	 * @param map
	 * @return
	 */
	public static List<Entry<String, Object>> sortMap(Map<String, Object> map){
		List<Entry<String, Object>> infoIds = new ArrayList<Entry<String, Object>>(map.entrySet());

        //排序方法
        Collections.sort(infoIds, new Comparator<Entry<String, Object>>() {
            public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
                //return (o2.getValue() - o1.getValue());
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        return infoIds;
	}

    //返回值类型为Map<String, Object>
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String[]> properties = request.getParameterMap();//把请求参数封装到Map<String, String[]>中
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Iterator<Entry<String, String[]>> iter = properties.entrySet().iterator();
        String name = "";
        String value = "";
        while (iter.hasNext()) {
            Entry<String, String[]> entry = iter.next();
            name = entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) { //用于请求参数中有多个相同名称
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();//用于请求参数中请求参数名唯一
            }
            try {
                if(request.getMethod().equals("GET")){
                    String str = new String(value.getBytes("iso-8859-1"), "utf-8");
                    returnMap.put(name, str);
//                    String utf8Str = URLDecoder.decode(value, "UTF-8");
                }else{
                    returnMap.put(name, value);
                }
//                String encoding = request.getCharacterEncoding();
//			    System.out.println(encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return returnMap;
    }
    //返回值类型为Map<String, String>
    public static Map<String, String> getParameterStringMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();//把请求参数封装到Map<String, String[]>中
        Map<String, String> returnMap = new HashMap<String, String>();
        String name = "";
        String value = "";
        for (Entry<String, String[]> entry : properties.entrySet()) {
            name = entry.getKey();
            String[] values = entry.getValue();
            if (null == values) {
                value = "";
            } else if (values.length>1) {
                for (int i = 0; i < values.length; i++) { //用于请求参数中有多个相同名称
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = values[0];//用于请求参数中请求参数名唯一
            }
            returnMap.put(name, value);

        }
        return returnMap;
    }


    /**
     * 获取签名
     * @param params
     * @return
     */
    public static String getSign(Map<String, Object> params, String key){
        List<Entry<String, Object>> signMap = sortMap(params);
        String stringA = "";
        for(Entry<String, Object> entry: signMap){
            stringA += entry.getKey() + "=" + (entry.getValue()==null?"":entry.getValue()) + "&";
        }
        String strSignTemp = stringA + "key=" + key;
        return DigestUtils.md5Hex(strSignTemp).toUpperCase();
    }

	/**
	 * 微信小程序获取签名
	 * @param params
	 * @return
	 */
	public static String getWxAppletSign(Map<String, Object> params, String key){
		List<Entry<String, Object>> signMap = sortMap(params);
		String stringA = "{";
		for(Entry<String, Object> entry: signMap){
			stringA += entry.getKey() + ":" + (entry.getValue()==null?"":entry.getValue()) + ",";
		}
		String strSignTemp = stringA + "key:" + key + "}";
		return DigestUtils.md5Hex(strSignTemp).toUpperCase();
	}

}
