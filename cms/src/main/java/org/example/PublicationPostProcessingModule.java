package org.example;
 
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
 
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.HippoNode;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// INI
import javax.jcr.version.VersionManager;
import javax.jcr.NodeIterator;
// FIN 
public class PublicationPostProcessingModule implements DaemonModule {
 
    private static final Logger log = LoggerFactory.getLogger(PublicationPostProcessingModule.class);
 
    public static final String PUBLICATION_INTERACTION = "default:handle:publish";
    public static final String TITLE_PROPERTY = "HippoPrimer:title";
    public static final String CONTENT_PROPERTY = "hippostd:content";
 
    private Session session;
 
    @Override
    public void initialize(final Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);
    }
 
    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }
 
     
    @Subscribe
    public void handleEvent(final HippoWorkflowEvent event) {
        if (event.success() && PUBLICATION_INTERACTION.equals(event.interaction())) {
            postPublish(event);
        }
    }
 
    private void postPublish(final HippoWorkflowEvent workflowEvent) {
        String title = null;
        try {
            final HippoNode handle = (HippoNode) session.getNodeByIdentifier(workflowEvent.subjectId());
            final Node published = getPublishedVariant(handle);
// INI
                  NodeIterator children = handle.getNodes();
                while(children.hasNext()) {
                    Node child = children.nextNode();
                    VersionManager versionManager = session.getWorkspace().getVersionManager(); 
                    String nodeTypeStr = child.getPrimaryNodeType().getName();
                    if(log.isDebugEnabled()) {
                        log.debug("Node type is: "+nodeTypeStr+ " for node: "+child.getPath());
                        System.out.println("Node type is: "+nodeTypeStr+ " for node: "+child.getPath());
                    }
                    
                    NodeIterator children2 = child.getNodes();
                    while(children2.hasNext()) {
                        Node child2 = children2.nextNode();
                   //     VersionManager versionManager = session.getWorkspace().getVersionManager(); 
                        String nodeTypeStr2 = child2.getPrimaryNodeType().getName();
                        String text = JcrUtils.getStringProperty(child2, CONTENT_PROPERTY, null);
                        System.out.println("text "+text);
                 //       String prop = child2.getStringProperty();
                 //       System.out.println("property: "+prop);                        
                        if(log.isDebugEnabled()) {
                            log.debug("Node type is: "+nodeTypeStr2+ " for node: "+child2.getPath());
                            System.out.println("Node type is: "+nodeTypeStr2+ " for node: "+child2.getPath());
                        }                    
                } 
                }
// FIN
            if (published != null) {
                title = JcrUtils.getStringProperty(published, TITLE_PROPERTY, handle.getDisplayName());
            } else {
                log.warn("Something's wrong because I can't find the document variant that was just published");
                title = handle.getDisplayName();
            }
        } catch (ItemNotFoundException e) {
            log.warn("Something's wrong because I can't find the handle of the document that was just published");
        } catch (RepositoryException e) {
            log.error("Something's very wrong: unexpected exception while doing simple JCR read operations", e);
        }
        System.out.println(workflowEvent.user() + " published " + title);
        System.out.println("analiza el contenido y lo anade a un doc admin type");
//        if (bad_language)
        System.out.println("envia correo con el doc admin type");
    }
 
    private static Node getPublishedVariant(Node handle) throws RepositoryException {
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (HippoStdNodeType.PUBLISHED.equals(state)) {
                return variant;
            }
        }
        return null;
    }
 
 
}