package com.wujiabo.opensource.feather.web.shiro.filter;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.wujiabo.opensource.feather.constants.Constants;
import com.wujiabo.opensource.feather.mybatis.model.TUser;
import com.wujiabo.opensource.feather.service.RbacService;

/**
 * <p>
 * User: Zhang Kaitao
 * <p>
 * Date: 14-2-15
 * <p>
 * Version: 1.0
 */
public class SysUserFilter extends PathMatchingFilter {

	@Autowired
	private RbacService rbacService;

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {

		String username = (String) SecurityUtils.getSubject().getPrincipal();
		TUser user = rbacService.findByUsername(username);
		request.setAttribute(Constants.CURRENT_USER, user);

		String contextPath = request.getServletContext().getContextPath();
		request.setAttribute(Constants.CONTEXT_PATH, contextPath);

		Session shiroSession = SecurityUtils.getSubject().getSession();

		if (shiroSession.getAttribute(Constants.CURRENT_MENU) == null) {

			List<Map<String, Object>> menus = rbacService.getCurrentMenu(user.getUserId());

			StringBuffer menuSb = new StringBuffer();

			String pm = "<li class=\"dropdown\"><a href=\"#\" class=\"dropdown-toggle\""
					+ "data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\""
					+ "aria-expanded=\"false\">${pmn}$<span class=\"caret\"></span></a>"
					+ "<ul class=\"dropdown-menu\">${cm}$</ul>" + "</li>";
			String cm = "<li><a href=\"${cmu}$\">${cmn}$</a></li>";

			for (Map<String, Object> menu : menus) {
				String id = MapUtils.getString(menu, "menu_id");
				String pid = MapUtils.getString(menu, "menu_pid");
				String name = MapUtils.getString(menu, "menu_name");
				if (StringUtils.isEmpty(pid)) {
					menuSb.append(pm.replace("${pmn}$", name));
					StringBuffer cmSb = new StringBuffer();
					for (Map<String, Object> cmenu : menus) {
						String cpid = MapUtils.getString(cmenu, "menu_pid");
						String url = MapUtils.getString(cmenu, "menu_url");
						String cname = MapUtils.getString(cmenu, "menu_name");
						if (!StringUtils.isEmpty(cpid) && cpid.equals(id)) {
							cmSb.append(cm.replace("${cmu}$", contextPath + url).replace("${cmn}$", cname));
						}
					}
					menuSb = new StringBuffer(menuSb.toString().replace("${cm}$", cmSb.toString()));
				}
			}

			shiroSession.setAttribute(Constants.CURRENT_MENU, menuSb.toString());
		}

		return true;
	}
}
