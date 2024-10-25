package xyz.stasiak.miapbscholarship;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class BankAccountVerificationCallback implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RuntimeService runtimeService =
                execution.getProcessEngineServices().getRuntimeService();
        runtimeService.createMessageCorrelation("bankAccountVerificationResultMsg")
                .processInstanceId(execution.getVariable("parentBussinesKey").toString())
                .setVariable("verification_result", execution.getVariable("verification_result"))
                .correlateWithResult();
    }
}
