/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.report.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
@Handler
public class SimpleHtmlReportRenderer extends AbstractReportRenderer {
	
		
	/**
     * @see org.openmrs.report.ReportRenderer#getLabel()
     */
    public String getLabel() {
    	return "Simple Html";
    }

	/**
     * @see org.openmrs.report.ReportRenderer#getRenderedContentType(org.openmrs.report.ReportDefinition, java.lang.String)
     */
    public String getRenderedContentType(ReportDefinition schema, String argument) {
    	return "text/html";
    }

	/**
	 * @see org.openmrs.report.ReportRenderer#getLinkUrl(org.openmrs.report.ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition schema) {
		return null;
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getFilename(org.openmrs.report.ReportDefinition)
	 */
	public String getFilename(ReportDefinition schema, String argument) {
		return schema.getName() + ".html";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderingModes(org.openmrs.report.ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition schema) {
		return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE));
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		PrintWriter pw = new PrintWriter(out);
		render(results, argument, pw);
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#render(ReportData, String, Writer)
	 */
	@SuppressWarnings("unchecked")
	public void render(ReportData results, String argument, Writer writer) throws IOException, RenderingException {
		
		writer.write("<html>");
		writer.write("<head>");				
		writer.write("<body>");
		for (String key : results.getDataSets().keySet()) {
			DataSet<Object> dataset = results.getDataSets().get(key);
			List<DataSetColumn> columns = dataset.getDefinition().getColumns();
			writer.write("<h4>" + key + "</h4>");
			writer.write("<table id=\"simple-html-dataset-" + key + "\" class=\"display simple-html-dataset\"><tr>");
			for (DataSetColumn column : columns) {
				writer.write("<th>"+column.getColumnKey()+"</th>");
			}
			writer.write("</tr>");

			for (Iterator<DataSetRow<Object>> i = dataset.iterator(); i.hasNext();) {
				DataSetRow<Object> row = i.next();
				writer.write("<tr>");
				for (DataSetColumn column : columns) {
					writer.write("<td>");
					Object colValue = row.getColumnValue(column.getColumnKey());
					if (colValue != null) {
						if (colValue instanceof Cohort) {
							writer.write(Integer.toString(((Cohort) colValue).size()));
						} else {
							writer.write(colValue.toString());
						}
					}
					writer.write("</td>");
				}
				writer.write("</tr>");
			}
			writer.write("</table>");
		}
		writer.write("</body>");
		writer.write("</head>");		
		writer.write("</html>");
		writer.flush();
	}
	
}
