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
@Api(value="v1",description="用户管理系统")
@RequestMapping("v1")
//每个方法前面的RequestMapping注释为Spring中的方法用来定义链接名称和请求方式
//每个方法前面的ApiOperation注释为Swagger框架中的方法，用来生成接口文档
public class UserManager {
	@Autowired
	private SqlSessionTemplate template;
	
	@ApiOperation(value="登录接口",httpMethod="POST")
	@RequestMapping(value="/login",method=RequestMethod.POST)
	//登录接口，传入User后查询数据库判断是否有这条数据，有的话将cookie传入response中
	public Boolean login(HttpServletResponse response,@RequestBody User user) {
		//使用User查询 
		int i=template.selectOne("login",user);
		//自己定义一个cookie
		Cookie cookie = new Cookie("login", "true");
		//将cookie加入到response中
		response.addCookie(cookie);
		log.info("查询到的结果是"+i);
		if (i==1) {
			log.info("登录的用户是:"+user.getUserName());
			return true;
		}
		return false;
	}	
	/**
	 * 添加用户，传入请求和User，如果请求中的cookies判断是正确的，则将User添加进数据库
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/addUser",method=RequestMethod.POST)
	@ApiOperation(value="添加用户接口",httpMethod="POST")
	public boolean addUser(HttpServletRequest request,@RequestBody User user) {
		Boolean x = verifyCookies(request);
		int result=0;
		if (x!=null) {
			 result = template.insert("addUser",user);
		}
		if (result>0) {
			log.info("添加用户的数量是："+result);
			return true;
		}
		return false;
	}
	/**
	 * 通过request中的cookies判断接口是否访问成功
	 * 如果成功就执行template中的方法来对数据库进行操作
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getUserInfo",method=RequestMethod.POST)
	@ApiOperation(value="获取用户列表接口",httpMethod="POST")
	public List<User> getUserInfo(@RequestBody User user,HttpServletRequest request){
		Boolean x = verifyCookies(request);
		if(x==true) {
			List<User> users = template.selectList("getUserInfo",user);
			log.info("获取到的用户数量是"+users.size());
			return users;
		}
		return null;
	}
	@RequestMapping(value="/updateUserInfo",method=RequestMethod.POST)
	@ApiOperation(value="更新/删除用户数据接口",httpMethod="POST")
	public int updateUser(HttpServletRequest request,@RequestBody User user) {
		Boolean x = verifyCookies(request);
		int i =0;
		if(x==true) {
			i = template.update("updateUserInfo",user);
		}
		log.info("更新数据的条目数为:"+i);
		return i;
	}
	/**
	 * 传入请求，判断cookies是否正确，返回true or false
	 * @param request
	 * @return
	 */
	private Boolean verifyCookies(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Cookie[] cookies = request.getCookies();
		if (Objects.isNull(cookies)) {
			log.info("cookies为空");
			return false;
		}
		for(Cookie cookie:cookies) {
			if (cookie.getName().equals("login")&&cookie.getValue().equals("true")) {
				log.info("cookies验证通过");
				return true;
			}
		}
		return false;
	}
	
	
}
