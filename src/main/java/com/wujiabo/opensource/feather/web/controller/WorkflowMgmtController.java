package com.wujiabo.opensource.feather.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wujiabo.opensource.feather.service.exception.ServiceException;

@Controller
@RequestMapping("/workflowMgmt")
public class WorkflowMgmtController {

	@Resource
	protected RepositoryService repositoryService;

	@RequestMapping(value = "/process", method = { RequestMethod.POST, RequestMethod.GET })
	@RequiresPermissions(value = "WORKFLOW_MGMT_PROCESS")
	public String process(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "processDefinitionKey", defaultValue = "") String processDefinitionKey, Model model) {

		long totalCount = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKeyLike("%" + processDefinitionKey + "%").active().count();
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().active()
				.asc().listPage((currentPage - 1) * 10, 10);

		Boolean isFirst = false;
		Boolean isLast = false;
		if (currentPage == 1) {
			isFirst = true;
			isLast = false;
		}
		if (totalCount <= currentPage * 10 && totalCount > (currentPage - 1) * 10) {
			isLast = true;
		}
		int totalPage = (int) totalCount / 10 + (totalCount % 10 > 0 ? 1 : 0);

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("isLast", isLast);
		model.addAttribute("list", list);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("processDefinitionKey", processDefinitionKey);
		return "workflow/process";
	}

	@RequestMapping(value = "deploy", method = RequestMethod.GET)
	@RequiresPermissions(value = "WORKFLOW_MGMT_DEPLOY")
	public String deployForm() {
		return "workflow/deploy";
	}

	@RequestMapping(value = "deploy", method = RequestMethod.POST)
	@RequiresPermissions(value = "WORKFLOW_MGMT_DEPLOY")
	public String group(@RequestParam(value = "processFile", required = false) MultipartFile processFile,
			RedirectAttributes redirectAttributes) {
		try {
			redirectAttributes.addFlashAttribute("message", "操作成功");
		} catch (ServiceException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/workflowMgmt/deploy";
		}
		return "redirect:/workflowMgmt/process";
	}
}