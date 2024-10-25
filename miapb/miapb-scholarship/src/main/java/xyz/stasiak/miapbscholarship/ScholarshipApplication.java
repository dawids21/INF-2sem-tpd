package xyz.stasiak.miapbscholarship;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.StringValue;

import java.util.Map;

@ProcessApplication("Scholarship Application")
public class ScholarshipApplication extends ServletProcessApplication {

    @PostDeploy
    public void evaluateDecisionTable(ProcessEngine processEngine) {
        DecisionService decisionService = processEngine.getDecisionService();

        VariableMap variables = Variables.createVariables()
                .putValue("application_averageGrade", 4.3)
                .putValue("application_references", "abc,def");

        DmnDecisionTableResult decisionResult = decisionService.evaluateDecisionTableByKey("ApplicationVerification", variables);

        DmnDecisionRuleResult sr = decisionResult.getSingleResult();
        if (decisionResult.getSingleResult().containsKey("decision_isPositive")) {
            Boolean zal = decisionResult.getSingleResult().getEntry("decision_isPositive");
            System.out.println("Is positive: " + zal);
        }
        StringValue justification = sr.getEntryTyped("decision_justification");
        System.out.println("decision_justification: " + justification.getValue());

        for(Map<String, Object> result: decisionResult.getResultList()) {
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }
    }
}
