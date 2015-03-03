package be.fluid_it.tools.rundeck.plugins.rest;

import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.dtolabs.rundeck.plugins.step.StepPlugin;
import be.fluid_it.tools.rundeck.plugins.util.ExpandUtil;
import org.boon.HTTP;

import java.util.HashMap;
import java.util.Map;

@Plugin(name = RestPostWorkflowStepPlugin.SERVICE_PROVIDER_NAME, service = ServiceNameConstants.WorkflowStep)
@PluginDescription(title = "Rest Post Plugin", description = "Performs a post to a rest resource")
public class RestPostWorkflowStepPlugin implements StepPlugin {
    public static final String SERVICE_PROVIDER_NAME = "eu.europa.ec.ercea.backoffice.tools.rundeck.plugins.rest.RestPostWorkflowStepPlugin";

    @PluginProperty(title = "Remote URL", description = "Rest resource url to be posted to", required = true)
    private String remoteURL;

    @PluginProperty(title = "Post parameters", description = "Post parameters (syntax name1=value1&name2=value2&...", required = false)
    private String postParameters;

    @Override
    public void executeStep(PluginStepContext context, Map<String, Object> configuration) throws StepException {
        Map<String, Map<String, String>> dataContext = context.getExecutionContext().getDataContext();

        String user = context.getExecutionContext().getUser();
        String expandedRemoteUrl = ExpandUtil.expand(remoteURL, context.getExecutionContext());

        Map<String, String> headers = new HashMap<String, String>();
        Map<String, Object> formData = new HashMap<String, Object>();
        String expandedPostParameters = null;
        if (postParameters != null) {
            expandedPostParameters = ExpandUtil.expand(postParameters, context.getExecutionContext());
            for (String postNameValue: expandedPostParameters.split("&")) {
                if (postNameValue.contains("=")) {
                    String[] nameValue =  postNameValue.split("=");
                    if (nameValue.length == 2) {
                        formData.put(nameValue[0], nameValue[1]);
                    }
                }
            };
        }

        StringBuffer buffer = new StringBuffer("Posting to [");
        buffer.append(expandedRemoteUrl).append("]");
        if (postParameters != null) {
            buffer.append(" with parameters [").append(expandedPostParameters).append("]");
        }
        buffer.append(" ...");
        System.out.println(buffer);

        String result = HTTP.postForm(expandedRemoteUrl, headers, formData);

        System.out.println(result);
    }
}