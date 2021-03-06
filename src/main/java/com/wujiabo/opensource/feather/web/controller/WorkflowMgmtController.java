package com.wujiabo.opensource.feather.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wujiabo.opensource.feather.mybatis.model.TUser;
import com.wujiabo.opensource.feather.service.WorkflowMgmtService;
import com.wujiabo.opensource.feather.service.exception.ServiceException;
import com.wujiabo.opensource.feather.web.bind.CurrentUser;

@Controller
@RequestMapping("/workflowMgmt")
public class WorkflowMgmtController {

	@Autowired
	private WorkflowMgmtService workflowMgmtService;

	@Resource
	protected RepositoryService repositoryService;

	@Resource
	protected HistoryService historyService;

	@Resource
	protected TaskService taskService;

	@RequestMapping(value = "/process", method = { RequestMethod.POST, RequestMethod.GET })
	@RequiresPermissions(value = "WORKFLOW_MGMT_PROCESS")
	public String process(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "processDefinitionKey", defaultValue = "") String processDefinitionKey, Model model) {

		long totalCount = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKeyLike("%" + processDefinitionKey + "%").active().count();
		List<ProcessDefinition> processDefList = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKeyLike("%" + processDefinitionKey + "%").orderByDeploymentId().active().asc()
				.listPage((currentPage - 1) * 10, 10);

		Boolean isFirst = false;
		Boolean isLast = false;
		if (currentPage == 1) {
			isFirst = true;
			isLast = false;
		}
		if (totalCount <= currentPage * 10 && totalCount >= (currentPage - 1) * 10) {
			isLast = true;
		}
		int totalPage = (int) totalCount / 10 + (totalCount % 10 > 0 ? 1 : 0);

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("isLast", isLast);
		model.addAttribute("processDefList", processDefList);
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
	public String deploy(@RequestParam(value = "processFile", required = false) MultipartFile processFile,
			RedirectAttributes redirectAttributes) {
		try {
			workflowMgmtService.deployProcess(processFile);
			redirectAttributes.addFlashAttribute("message", "操作成功");
		} catch (ServiceException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/workflowMgmt/deploy";
		}
		return "redirect:/workflowMgmt/process";
	}

	@RequestMapping(value = "view/{viewType}/{viewId}", method = RequestMethod.GET)
	@RequiresPermissions(value = "WORKFLOW_MGMT_PROCESS")
	public String viewForm(@PathVariable("viewType") String viewType, @PathVariable("viewId") String viewId,
			Model model) {
		model.addAttribute("viewType", viewType);
		model.addAttribute("viewId", viewId);
		return "workflow/view";
	}

	@RequestMapping(value = "diagram/{viewType}/{viewId}", method = RequestMethod.GET)
	@RequiresPermissions(value = "WORKFLOW_MGMT_PROCESS")
	public String diagram(@PathVariable("viewType") String viewType, @PathVariable("viewId") String viewId,
			HttpServletResponse response) {
		InputStream diagramInputStream = null;
		if ("definition".equals(viewType)) {
			diagramInputStream = workflowMgmtService.getProcessDefDiagram(viewId);
		} else if ("instance".equals(viewType)) {
			diagramInputStream = workflowMgmtService.getProcessInstanceDiagram(viewId);
		}
		try {
			response.getOutputStream().write(IoUtil.readInputStream(diagramInputStream, "diagramInputStream"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/instance", method = { RequestMethod.POST, RequestMethod.GET })
	@RequiresPermissions(value = "WORKFLOW_MGMT_INSTANCE")
	public String instance(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderId", defaultValue = "") String orderId, Model model) {

		long totalCount = historyService.createHistoricProcessInstanceQuery().count();
		List<HistoricProcessInstance> processInstanceList = historyService.createHistoricProcessInstanceQuery()
				.orderByProcessInstanceId().asc().listPage((currentPage - 1) * 10, 10);

		Boolean isFirst = false;
		Boolean isLast = false;
		if (currentPage == 1) {
			isFirst = true;
			isLast = false;
		}
		if (totalCount <= currentPage * 10 && totalCount >= (currentPage - 1) * 10) {
			isLast = true;
		}
		int totalPage = (int) totalCount / 10 + (totalCount % 10 > 0 ? 1 : 0);

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("isLast", isLast);
		model.addAttribute("processInstanceList", processInstanceList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("orderId", orderId);
		return "workflow/instance";
	}

	@RequestMapping(value = "start", method = RequestMethod.GET)
	@RequiresPermissions(value = "WORKFLOW_MGMT_START")
	public String startForm(Model model) {
		List<ProcessDefinition> processDefList = repositoryService.createProcessDefinitionQuery().latestVersion()
				.active().orderByProcessDefinitionName().asc().list();
		model.addAttribute("processDefList", processDefList);
		return "workflow/start";
	}

	@RequestMapping(value = "start", method = RequestMethod.POST)
	@RequiresPermissions(value = "WORKFLOW_MGMT_START")
	public String start(@RequestParam(value = "processDefId", required = false) String processDefId,
			@RequestParam(value = "variables", required = false) String variables,
			RedirectAttributes redirectAttributes) {
		try {
			workflowMgmtService.startProcess(processDefId, variables);
			redirectAttributes.addFlashAttribute("message", "操作成功");
		} catch (ServiceException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/workflowMgmt/start";
		}
		return "redirect:/workflowMgmt/instance";
	}

	@RequestMapping(value = "/claim", method = { RequestMethod.POST, RequestMethod.GET })
	@RequiresPermissions(value = "WORKFLOW_MGMT_CLAIM")
	public String claim(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderId", defaultValue = "") String orderId, @CurrentUser TUser loginUser,
			Model model) {

		long totalCount = taskService.createTaskQuery().taskCandidateUser(loginUser.getUserId().toString()).count();
		List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(loginUser.getUserId().toString())
				.orderByTaskCreateTime().asc().listPage((currentPage - 1) * 10, 10);

		Boolean isFirst = false;
		Boolean isLast = false;
		if (currentPage == 1) {
			isFirst = true;
			isLast = false;
		}
		if (totalCount <= currentPage * 10 && totalCount >= (currentPage - 1) * 10) {
			isLast = true;
		}
		int totalPage = (int) totalCount / 10 + (totalCount % 10 > 0 ? 1 : 0);

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("isLast", isLast);
		model.addAttribute("taskList", taskList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("orderId", orderId);
		return "workflow/claim";
	}

	@RequestMapping(value = "claim/{taskId}", method = RequestMethod.GET)
	@RequiresPermissions(value = "WORKFLOW_MGMT_CLAIM")
	public String claimTask(@PathVariable("taskId") String taskId, @CurrentUser TUser loginUser,
			RedirectAttributes redirectAttributes) {
		try {
			workflowMgmtService.claimTask(taskId, loginUser.getUserId().toString());
			redirectAttributes.addFlashAttribute("message", "操作成功");
		} catch (ServiceException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/workflowMgmt/claim";
		}
		return "redirect:/workflowMgmt/claim";
	}

	@RequestMapping(value = "/todo", method = { RequestMethod.POST, RequestMethod.GET })
	@RequiresPermissions(value = "WORKFLOW_MGMT_HANDLE")
	public String todo(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderId", defaultValue = "") String orderId, @CurrentUser TUser loginUser,
			Model model) {

		long totalCount = taskService.createTaskQuery().taskAssignee(loginUser.getUserId().toString()).count();
		List<Task> taskList = taskService.createTaskQuery().taskAssignee(loginUser.getUserId().toString())
				.orderByTaskCreateTime().asc().listPage((currentPage - 1) * 10, 10);

		Boolean isFirst = false;
		Boolean isLast = false;
		if (currentPage == 1) {
			isFirst = true;
			isLast = false;
		}
		if (totalCount <= currentPage * 10 && totalCount >= (currentPage - 1) * 10) {
			isLast = true;
		}
		int totalPage = (int) totalCount / 10 + (totalCount % 10 > 0 ? 1 : 0);

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("isLast", isLast);
		model.addAttribute("taskList", taskList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("orderId", orderId);
		return "workflow/todo";
	}

	@RequestMapping(value = "handle/{taskId}", method = RequestMethod.GET)
	@RequiresPermissions(value = "WORKFLOW_MGMT_DEPLOY")
	public String handleForm(@PathVariable("taskId") String taskId, Model model) {
		model.addAttribute("taskId", taskId);
		return "workflow/handle";
	}

	@RequestMapping(value = "handle", method = RequestMethod.POST)
	@RequiresPermissions(value = "WORKFLOW_MGMT_DEPLOY")
	public String handle(@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "variables", required = false) String variables,
			RedirectAttributes redirectAttributes) {
		try {
			workflowMgmtService.completeTask(taskId, variables);
			redirectAttributes.addFlashAttribute("message", "操作成功");
		} catch (ServiceException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/workflowMgmt/handle/" + taskId;
		}
		return "redirect:/workflowMgmt/todo";
	}
}
