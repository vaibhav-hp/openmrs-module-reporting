package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.ParameterException;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.renderer.XmlReportRenderer;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/reporting/parameters/queryParameter")
public class QueryParameterFormController {

	/* Logger */
	private Log log = LogFactory.getLog(this.getClass());

	/* Date format */
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

	
	/**
	 * Allows us to bind a custom editor for a class.
	 * @param binder
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true)); 
    }    
	
	
    /**
     * Shows the form.  This method is called after the formBackingObject()
     * method below.
     * 
     * @return	the form model and view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView setupForm() {

				
		ModelAndView model = 
			new ModelAndView("/module/reporting/parameters/queryParameterForm");
		
		
		return model; 
	}	
	
	/**
	 * Processes the form when a user submits.  
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 */	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			HttpServletRequest request,	
			HttpServletResponse response,	
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type,
			@RequestParam(value = "action", required=false) String action,
			@RequestParam(value = "format", required=false) String format,
			@RequestParam(value = "successView", required=false) String successView,
			@ModelAttribute("parameterizable") Parameterizable parameterizable, 
			BindingResult bindingResult) throws Exception {
					
		log.info("Action: " + action);
		
		Object results = null;
		String defaultView = "/module/reporting/parameters/queryParameterForm";
		ModelAndView model = new ModelAndView(defaultView);
		
		if (bindingResult.hasErrors()) {			
			log.info("BindingResult: " + bindingResult);
			log.info("Errors: " + bindingResult.getAllErrors());
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "An error has occurred.  See below for more details.");
			return setupForm();
		}
		

		log.info("Parameterizable: " + parameterizable);
		if ( parameterizable == null ) 
			parameterizable = ParameterizableUtil.getParameterizable(uuid, type);		
			
			
		if (parameterizable != null) {			
			EvaluationContext context = new EvaluationContext();

			// Build a map of parameters from the request
			Map<String, String> parameterValuesAsStrings = new HashMap<String,String>();
			for (Parameter parameter : parameterizable.getParameters()) { 				
				log.info("Parameter: " + parameter.getName() + " = " + request.getParameter(parameter.getName()) );
				parameterValuesAsStrings.put(parameter.getName(), request.getParameter(parameter.getName()));				
			}
			
			// Convert values from string to object
			Map<String, Object> parameterValues = 
				ParameterizableUtil.getParameterValues(parameterizable, parameterValuesAsStrings);

			log.info("parameter values: " + parameterValues);
			log.info("success view: " + successView);
			// Set parameter values
			context.setParameterValues(parameterValues);		
			try { 
				// Evaluate the parameterizable and populate the model
				results = ParameterizableUtil.evaluateParameterizable(parameterizable, context);						
				//model.addObject("results", results);
				request.getSession().setAttribute("results", results);
				if (successView != null) {
					successView += "?uuid=" + parameterizable.getUuid() + "&type=" + type + "&format=" + format; 
					model.setViewName(successView);
				}
			} 
			catch(ParameterException e) { 
				log.error("unable to evaluate report: ", e);
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Unable to evaluate report: " + e.getMessage());
				setupForm();
			}								
		}		
		
		log.info("Returning model with view " + model.getViewName() + " and map " + model.getModelMap());
		return model;
	}
	
	/**
	 * Retrieves/creates a form backing object.
	 * 
	 * @param uuid
	 * @param type
	 * @param parameterName
	 * @return
	 */
	@ModelAttribute("parameterizable")
	public Parameterizable formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type) {
		
		
		return ParameterizableUtil.getParameterizable(uuid, type);

	}
	
}
