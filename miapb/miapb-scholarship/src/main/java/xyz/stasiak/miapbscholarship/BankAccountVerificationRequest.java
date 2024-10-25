package xyz.stasiak.miapbscholarship;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public class BankAccountVerificationRequest implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RuntimeService runtimeService = execution.getProcessEngineServices()
                .getRuntimeService();
        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put("parentBussinesKey", execution.getProcessInstanceId());
        processVariables.put("application_bankAccountNumber", execution.getVariable
                ("application_bankAccountNumber"));
        runtimeService.startProcessInstanceByMessage("bankAccountVerificationMsg",
                processVariables);
    }
}
