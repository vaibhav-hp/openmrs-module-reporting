/*
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

package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DefinitionLibraryCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports=DefinitionLibraryCohortDefinition.class)
public class DefinitionLibraryCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private AllDefinitionLibraries definitionLibraries;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        DefinitionLibraryCohortDefinition cd = (DefinitionLibraryCohortDefinition) cohortDefinition;
        CohortDefinition referencedDefinition = definitionLibraries.getDefinition(CohortDefinition.class, cd.getDefinitionKey());

        Mapped<CohortDefinition> mapped = new Mapped<CohortDefinition>(referencedDefinition, cd.getParameterValues());

        return cohortDefinitionService.evaluate(mapped, context);
    }

}
