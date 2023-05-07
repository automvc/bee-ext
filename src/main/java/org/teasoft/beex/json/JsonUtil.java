package org.teasoft.beex.json;


import java.util.ArrayList;
import java.util.List;

import org.teasoft.honey.osql.core.Logger;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

/**
 * Json工具(用jackson).Json Util with jackson.
 * @author Kingstar
 * @since  2.0
 */
public class JsonUtil {
	
	public static String toJson(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static <T> T toEntity(String json, Class<T> clazz) {
		try {
			if(json==null) return null;
			ObjectMapper mapper = new ObjectMapper();
			compat(mapper);
			return (T) mapper.readValue(json, clazz);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @param json
	 * @param clazz eg:List.class
	 * @param elementClass 元素的类型
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toEntity(String json, Class<T> clazz, Class elementClass) {
		try {
			if(json==null) return null;
			ObjectMapper mapper = new ObjectMapper();
			compat(mapper);
			JavaType javaType = mapper.getTypeFactory().constructParametricType(clazz, elementClass);
			return (T)mapper.readValue(json, javaType);
			
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public static <T> List<T> toEntityList(String json, Class<T> elementClass) {
		try {
			if(json==null) return null;
			ObjectMapper mapper = new ObjectMapper();
			compat(mapper);
			try {
				JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, elementClass);
				return mapper.readValue(json, javaType);
			} catch (MismatchedInputException e2) {
				//compat one obj to list
				T t=toEntity(json, elementClass);
				List<T> list0= new ArrayList<>();
				list0.add(t);
				return list0;
			}
			
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	private static void compat(ObjectMapper mapper) {
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);//允许不使用引号
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);//允许使用单引号
		
//		mapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true);
//		mapper.configure(Feature.ALLOW_COMMENTS,true);
		mapper.configure(Feature.ALLOW_MISSING_VALUES,true);
//		mapper.configure(Feature.ALLOW_NON_NUMERIC_NUMBERS,true);
		mapper.configure(Feature.ALLOW_NUMERIC_LEADING_ZEROS,true);
		mapper.configure(Feature.ALLOW_TRAILING_COMMA,true);
//		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
//		mapper.configure(Feature.ALLOW_YAML_COMMENTS,true);
	}
}
