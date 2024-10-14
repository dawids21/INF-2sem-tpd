package pl.dziekanat.tokstudiow;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Map;

@ProcessApplication("Loan Approval App")
public class ZaliczenieSemestru extends ServletProcessApplication {

    @PostDeploy
    public void evaluateDecisionTable(ProcessEngine processEngine) {
        DecisionService decisionService = processEngine.getDecisionService();

        VariableMap variables = Variables.createVariables()
                .putValue("podanie_punktyECTS", 16)
                .putValue("podanie_uzasadnienie", "adnsthstrherthjeyj");

        DmnDecisionTableResult decisionResult = decisionService.evaluateDecisionTableByKey("OcenaPodania", variables);

        DmnDecisionRuleResult sr = decisionResult.getSingleResult();
        if (decisionResult.getSingleResult().containsKey("decyzja_czyZaliczono")) {
            Boolean zal = decisionResult.getSingleResult().getEntry("decyzja_czyZaliczono");
            System.out.println("Czy zaliczono: " + zal);
        }
        StringValue uzasadnienie = sr.getEntryTyped("decyzja_uzasadnienie");
        System.out.println("Uzasadnienie: " + uzasadnienie.getValue());

        for(Map<String, Object> result: decisionResult.getResultList()) {
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }
    }
}
