package com.course.controller;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.course.model.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@Api(value="v1",description="�û�����ϵͳ")
@RequestMapping("v1")
//ÿ������ǰ���RequestMappingע��ΪSpring�еķ������������������ƺ�����ʽ
//ÿ������ǰ���ApiOperationע��ΪSwagger����еķ������������ɽӿ��ĵ�
public class UserManager {
	@Autowired
	private SqlSessionTemplate template;
	
	@ApiOperation(value="��¼�ӿ�",httpMethod="POST")
	@RequestMapping(value="/login",method=RequestMethod.POST)
	//��¼�ӿڣ�����User���ѯ���ݿ��ж��Ƿ����������ݣ��еĻ���cookie����response��
	public Boolean login(HttpServletResponse response,@RequestBody User user) {
		//ʹ��User��ѯ 
		int i=template.selectOne("login",user);
		//�Լ�����һ��cookie
		Cookie cookie = new Cookie("login", "true");
		//��cookie���뵽response��
		response.addCookie(cookie);
		log.info("��ѯ���Ľ����"+i);
		if (i==1) {
			log.info("��¼���û���:"+user.getUserName());
			return true;
		}
		return false;
	}	
	/**
	 * ����û������������User����������е�cookies�ж�����ȷ�ģ���User��ӽ����ݿ�
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/addUser",method=RequestMethod.POST)
	@ApiOperation(value="����û��ӿ�",httpMethod="POST")
	public boolean addUser(HttpServletRequest request,@RequestBody User user) {
		Boolean x = verifyCookies(request);
		int result=0;
		if (x!=null) {
			 result = template.insert("addUser",user);
		}
		if (result>0) {
			log.info("����û��������ǣ�"+result);
			return true;
		}
		return false;
	}
	/**
	 * ͨ��request�е�cookies�жϽӿ��Ƿ���ʳɹ�
	 * ����ɹ���ִ��template�еķ����������ݿ���в���
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getUserInfo",method=RequestMethod.POST)
	@ApiOperation(value="��ȡ�û��б�ӿ�",httpMethod="POST")
	public List<User> getUserInfo(@RequestBody User user,HttpServletRequest request){
		Boolean x = verifyCookies(request);
		if(x==true) {
			List<User> users = template.selectList("getUserInfo",user);
			log.info("��ȡ�����û�������"+users.size());
			return users;
		}
		return null;
	}
	@RequestMapping(value="/updateUserInfo",method=RequestMethod.POST)
	@ApiOperation(value="����/ɾ���û����ݽӿ�",httpMethod="POST")
	public int updateUser(HttpServletRequest request,@RequestBody User user) {
		Boolean x = verifyCookies(request);
		int i =0;
		if(x==true) {
			i = template.update("updateUserInfo",user);
		}
		log.info("�������ݵ���Ŀ��Ϊ:"+i);
		return i;
	}
	/**
	 * ���������ж�cookies�Ƿ���ȷ������true or false
	 * @param request
	 * @return
	 */
	private Boolean verifyCookies(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Cookie[] cookies = request.getCookies();
		if (Objects.isNull(cookies)) {
			log.info("cookiesΪ��");
			return false;
		}
		for(Cookie cookie:cookies) {
			if (cookie.getName().equals("login")&&cookie.getValue().equals("true")) {
				log.info("cookies��֤ͨ��");
				return true;
			}
		}
		return false;
	}
	
	
}
