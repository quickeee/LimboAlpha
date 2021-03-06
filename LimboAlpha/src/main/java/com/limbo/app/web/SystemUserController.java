package com.limbo.app.web;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.limbo.app.authentication.LoggedUser;
import com.limbo.app.domain.DataTablesRequest;
import com.limbo.app.domain.DataTablesResponse;
import com.limbo.app.domain.Role;
import com.limbo.app.domain.SystemUser;
import com.limbo.app.service.RoleService;
import com.limbo.app.service.SystemUserService;
import com.limbo.app.util.RoleEditor;

@Controller
public class SystemUserController {
	
	@Autowired
	SystemUserService userService;
	
	@Autowired
	RoleService roleService;
	
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	@InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Role.class, new RoleEditor(this.roleService));
    }
	
	@RequestMapping("/user")
	public String userRedirect(){
		return "redirect:/user/list";
	}

	@RequestMapping("/user/list")
	public String showAllUsers(Map<String, Object> map) {
		map.put("roles", roleService.listRoles());
		map.put("user", new SystemUser());
		return "user_json";
	}
	
	//@PreAuthorize("hasRole('ROLE_SUPERADMIN')")
	@RequestMapping("/user/add")
	public String addUser(Map<String, Object> map) {
		map.put("roles", roleService.listRoles());
		map.put("user", new SystemUser());
		return "user_add";
	}
	
	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public String doAddUser(@ModelAttribute("user") SystemUser user, BindingResult result) {		
		userService.addUser(user);	
		return "redirect:/user/list";

	}
	
	@RequestMapping("/user/delete/{userId}")
	public String deleteUser(@PathVariable("userId") Integer userId) {
		userService.removeUser(userId);
		return "redirect:/user/list";
	}
	
	@RequestMapping("/user/update/{userId}")
	public String updateUser(Map<String, Object> map, @PathVariable("userId") Integer userId) {		
		map.put("userId", userId);
		SystemUser user = userService.getUser(userId);
		logger.info("Controller: user roles - " + user.getRoles().toString());
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
			LoggedUser loggedUser = (LoggedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Collection<GrantedAuthority> grants = loggedUser.getAuthorities();
			for (GrantedAuthority aut: grants){
				logger.info(aut.getAuthority());
			}
		}	
		
		map.put("user", user);
		return "user_add";
	}	

	@RequestMapping("/user/encrypt")
	public String encryptPasswords() throws Exception {
		userService.encryptPasswords();
		return "user_list";
	}
	
	@RequestMapping(value = "/user/doajax.do", method = RequestMethod.POST)
	public @ResponseBody
	DataTablesResponse<SystemUser> getData(@RequestBody DataTablesRequest dtReq, Boolean isReturned, Boolean isRepaired) {
		logger.info("User ajax request.");
		DataTablesResponse<SystemUser> dtResp = userService.getDataTableResponse(dtReq);		
		return dtResp;
	}	

}
