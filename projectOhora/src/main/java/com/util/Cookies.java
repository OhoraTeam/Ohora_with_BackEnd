package com.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Cookies {   
   
   //        Ű:��Ű�̸�  ��:��Ű��ü
   public Map<String, Cookie> cookieMap = new HashMap<>();
   
   // ������ - ��Ű�� map�ȿ� ä���ִ� �۾�
   public Cookies( HttpServletRequest request ) {
      Cookie []  cookies = request.getCookies();
      if( cookies != null ) {
         for (Cookie c : cookies) {
            String cname =  c.getName();
            cookieMap.put(cname, c);
         } // for
      } // if
   }
   
   public Cookie getCookie( String cname ) {
      return this.cookieMap.get(cname);
   }
   
   // ��Ű�� �ִ��� ���� �Ǵ�
   public boolean exists( String cname ) {
      return this.cookieMap.get(cname)  != null ;  // true, false
   }
   
   // ��Ű name,value ������ -> ��Ű �����Ͽ� ����
   public static Cookie createCookie(String cname, String cvalue) throws UnsupportedEncodingException {
      Cookie c = new Cookie( cname, URLEncoder.encode(cvalue, "UTF-8"));
      return c;
   }
   
   // ��Ű ����, �ɼ��� �� �޸�
   public static Cookie createCookie(String cname, String cvalue, String path, int expiry) throws UnsupportedEncodingException {
      Cookie c = new Cookie( cname, URLEncoder.encode(cvalue, "UTF-8"));
      c.setPath(path);
      c.setMaxAge(expiry);
      return c;
   }
   
   public static Cookie createCookie(String cname, String cvalue, String domain, String path, int expiry) throws UnsupportedEncodingException {
      Cookie c = new Cookie( cname, URLEncoder.encode(cvalue, "UTF-8"));
      c.setDomain(domain);
      c.setPath(path);
      c.setMaxAge(expiry);
      return c;
   }
   
   // ��Ű name�ָ� value�� ����
   public String getValue( String cname) throws UnsupportedEncodingException {
      String cvalue = null; 
      Cookie c =  this.cookieMap.get(cname);
      if( c != null ) {
          cvalue = URLDecoder.decode(c.getValue(), "UTF-8");
      } 
      return cvalue;
   }

}




