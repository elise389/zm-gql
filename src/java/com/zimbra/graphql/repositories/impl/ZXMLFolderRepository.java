package com.zimbra.graphql.repositories.impl;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.service.mail.CreateFolder;
import com.zimbra.cs.service.mail.FolderAction;
import com.zimbra.cs.service.mail.GetFolder;
import com.zimbra.cs.service.mail.ItemAction;
import com.zimbra.graphql.repositories.IRepository;
import com.zimbra.graphql.utilities.GQLAuthUtilities;
import com.zimbra.graphql.utilities.XMLDocumentUtilities;
import com.zimbra.soap.mail.message.CreateFolderRequest;
import com.zimbra.soap.mail.message.FolderActionRequest;
import com.zimbra.soap.mail.message.GetFolderRequest;
import com.zimbra.soap.mail.type.Folder;
import com.zimbra.soap.mail.type.FolderActionResult;
import com.zimbra.soap.mail.type.FolderActionSelector;
import com.zimbra.soap.mail.type.GetFolderSpec;
import com.zimbra.soap.mail.type.NewFolderSpec;

public class ZXMLFolderRepository extends ZXMLItemRepository implements IRepository {

    /**
     * The createFolder document handler.
     */
    protected final CreateFolder createFolderHandler;

    /**
     * The getFolder document handler.
     */
    protected final GetFolder getFolderHandler;

    /**
     * Creates an instance with default document handlers.
     */
    public ZXMLFolderRepository() {
        super(new FolderAction());
        createFolderHandler = new CreateFolder();
        getFolderHandler = new GetFolder();
    }

    /**
     * Creates an instance with specified handlers.
     *
     * @param actionHandler The item action handler.
     * @param createHandler The create folder handler
     * @param getHandler The get folder handler
     */
    public ZXMLFolderRepository(ItemAction actionHandler, CreateFolder createHandler,
        GetFolder getHandler) {
        super(actionHandler);
        this.createFolderHandler = createHandler;
        this.getFolderHandler = getHandler;
    }

    /**
     * Retrieves a folder by given properties.
     *
     * @param octxt The operation context
     * @param account The account to search for folder
     * @param visible Whether to include all visible subfolders
     * @param needGranteeName Whether to return the grantee name
     * @param view Filter results by folder view
     * @param depth Filter subfolder tree depth
     * @param traverseMountpoints Whether or not to traverse one level of mountpoints
     * @param getFolder The primary folder identifiers
     * @return Fetch reuslts
     * @throws ServiceException If there are issues executing the document
     */
    public Folder getFolder(OperationContext octxt, Account account, Boolean visible,
        Boolean needGranteeName, Folder.View view, Integer depth, Boolean traverseMountpoints,
        GetFolderSpec getFolder) throws ServiceException {
        // map the request params
        final GetFolderRequest req = new GetFolderRequest();
        req.setFolder(getFolder);
        req.setNeedGranteeName(needGranteeName);
        req.setTraverseMountpoints(traverseMountpoints);
        req.setTreeDepth(depth);
        if (view != null) {
            req.setViewConstraint(view.name());
        }
        req.setVisible(visible);
        // execute
        final Element response = XMLDocumentUtilities.executeDocument(
            getFolderHandler,
            XMLDocumentUtilities.toElement(req),
            GQLAuthUtilities.getZimbraSoapContext(octxt, account));
        Folder folder = null;
        if (response != null) {
            folder = XMLDocumentUtilities.fromElement(response.getElement(MailConstants.E_FOLDER),
                Folder.class);
        }
        return folder;
    }

    /**
     * Create a folder with given properties.
     *
     * @param octxt The operation context
     * @param account The account to create the folder
     * @param folderToCreate New folder properties
     * @return The newly created folder
     * @throws ServiceException If there are issues executing the document
     */
    public Folder createFolder(OperationContext octxt, Account account,
        NewFolderSpec folderToCreate) throws ServiceException {
        // execute
        final CreateFolderRequest req = new CreateFolderRequest(folderToCreate);
        final Element response = XMLDocumentUtilities.executeDocument(
            createFolderHandler,
            XMLDocumentUtilities.toElement(req),
            GQLAuthUtilities.getZimbraSoapContext(octxt, account));
        Folder zCreatedFolder = null;
        if (response != null) {
            zCreatedFolder = XMLDocumentUtilities
                .fromElement(response.getElement(MailConstants.E_FOLDER), Folder.class);
        }
        return zCreatedFolder;
    }

    /**
     * Performs a folder action with given properties.
     *
     * @param octxt The operation context
     * @param account The account to perform the action
     * @param input The properties
     * @return Action result
     * @throws ServiceException If there are issues executing the document
     */
    public FolderActionResult action(OperationContext octxt, Account account,
        FolderActionSelector input) throws ServiceException {
        final FolderActionRequest req = new FolderActionRequest(input);
        final Element response = super.action(octxt, account, req);
        FolderActionResult result = null;
        if (response != null) {
            result = XMLDocumentUtilities.fromElement(response.getElement(MailConstants.E_ACTION),
                FolderActionResult.class);
        }
        return result;
    }

}