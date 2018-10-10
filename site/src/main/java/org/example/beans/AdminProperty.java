package org.example.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "HippoPrimer:AdminProperty")
@Node(jcrType = "HippoPrimer:AdminProperty")
public class AdminProperty extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "HippoPrimer:name")
    public String getName() {
        return getProperty("HippoPrimer:name");
    }

    @HippoEssentialsGenerated(internalName = "HippoPrimer:value")
    public String getValue() {
        return getProperty("HippoPrimer:value");
    }
}
