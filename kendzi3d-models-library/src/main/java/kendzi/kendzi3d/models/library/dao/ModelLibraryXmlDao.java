/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.models.library.dao;

import generated.ModelsLibrary;
import generated.ObjectFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import kendzi.kendzi3d.models.library.exception.ModelLibraryLoadException;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.util.UrlUtil;

import org.apache.log4j.Logger;

/**
 * Access to model library stored in xml files.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class ModelLibraryXmlDao {

    private static final String XSD_PACKAGE = "generated";


    /** Log. */
    private static final Logger log = Logger.getLogger(ModelsLibraryService.class);

    private ResourceService resourceManager;

    public ModelLibraryXmlDao(ResourceService resourceManager) {
        super();
        this.resourceManager = resourceManager;
    }

    public ModelsLibrary loadXml(String fileKey) throws ModelLibraryLoadException {

        try {

            URL url = resourceManager.resourceToUrl(fileKey);

            if (!UrlUtil.existUrl(url)) {
                log.warn("cant load point motel configuration from: " + fileKey + " url don't exist: " + url);
                return new ModelsLibrary();
            }

            return loadXml(url);

        } catch (Exception e) {
            throw new ModelLibraryLoadException("cant load model library from file key: " + fileKey, e);
        }
    }

    public static ModelsLibrary loadXml(URL url) throws ModelLibraryLoadException {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(XSD_PACKAGE, ModelsLibraryService.class.getClassLoader());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            @SuppressWarnings("unchecked")
            JAXBElement<ModelsLibrary> c = (JAXBElement<ModelsLibrary>) unmarshaller.unmarshal(url);

            return c.getValue();
        } catch (JAXBException e) {
            throw new ModelLibraryLoadException("cant load xml file from url: " + url, e);
        }
    }

    public static void saveXml(ModelsLibrary modelsLibrary, File file) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext = JAXBContext.newInstance(XSD_PACKAGE, ModelsLibraryService.class.getClassLoader());
        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ObjectFactory factory = new ObjectFactory();

        ModelsLibrary pointModels = factory.createModelsLibrary();

        pointModels.getNodeModel().addAll(modelsLibrary.getNodeModel());
        pointModels.getWayNodeModel().addAll(modelsLibrary.getWayNodeModel());

        JAXBElement<ModelsLibrary> gl = factory.createModelsLibrary(pointModels);

        marshaller.marshal(gl, new FileOutputStream(file));
    }
}
