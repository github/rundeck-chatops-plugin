package be.fluid_it.tools.rundeck.plugins.util;

import com.dtolabs.rundeck.core.authorization.AuthContext;
import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodesSelector;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.execution.ExecutionListener;
import com.dtolabs.rundeck.core.storage.StorageTree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FakeExecutionContext implements ExecutionContext {
    private final Map<String, Map<String, String>> dataContext = new HashMap<String, Map<String, String>>();

    public FakeExecutionContext() {
        init();
    }

    private void init() {
        this.dataContext.put("option", new HashMap<String, String>());
    }

    public void addOption(String name, String value) {
        this.dataContext.get("option").put(name, value);
    }

    @Override
    public String getFrameworkProject() {
        return null;
    }

    @Override
    public Framework getFramework() {
        return null;
    }

    @Override
    public AuthContext getAuthContext() {
        return null;
    }

    @Override
    public StorageTree getStorageTree() {
        return null;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public NodesSelector getNodeSelector() {
        return null;
    }

    @Override
    public INodeSet getNodes() {
        return null;
    }

    @Override
    public int getThreadCount() {
        return 0;
    }

    @Override
    public String getNodeRankAttribute() {
        return null;
    }

    @Override
    public boolean isNodeRankOrderAscending() {
        return false;
    }

    @Override
    public boolean isKeepgoing() {
        return false;
    }

    @Override
    public int getLoglevel() {
        return 0;
    }

    @Override
    public Map<String, Map<String, String>> getDataContext() {
        return dataContext;
    }

    @Override
    public Map<String, Map<String, String>> getPrivateDataContext() {
        return null;
    }

    @Override
    public ExecutionListener getExecutionListener() {
        return null;
    }

    @Override
    public File getNodesFile() {
        return null;
    }
}
