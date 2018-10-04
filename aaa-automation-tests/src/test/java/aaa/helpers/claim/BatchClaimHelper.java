package aaa.helpers.claim;

import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import toolkit.exceptions.IstfException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

/**
 * @author Andrii Syniagin
 * TODO Comments
 */
public class BatchClaimHelper {

    private static final Logger log = LoggerFactory.getLogger(BatchClaimHelper.class);

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLAIM_TEMPLATES_PATH = "/claimsmatch/cas_claim_templates";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLAIM_DATA_MODELS_PATH = "claimsmatch/claim_data_models";

    private static final String CAS_CLAIM_TEMPLATE = "cas_claim_response.ftl";

    private final static List<Supplier<Configuration>> configurations = new ArrayList<>();
    private static final String CLAIM_RESPONSE_KEY = "claimResponse";

    private String dataModelFileName;

    private String outputFileName;

    static {
        configurations.add(() -> {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(BatchClaimHelper.class, CLAIM_TEMPLATES_PATH);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            return cfg;
        });
    }

    /**
     *
     * @param dataModelFileName
     * @param outputFileName
     */
    public BatchClaimHelper(@Nonnull String dataModelFileName, @Nonnull String outputFileName) {
        this.dataModelFileName = dataModelFileName;
        this.outputFileName = outputFileName;
    }

    /**
     *
     * @param postProcessor
     * @return
     */
    public File processClaimTemplate(Consumer<CASClaimResponse> postProcessor) {
        CASClaimResponse claimResponse = getClaimResponseDataModel();
        assertThat(claimResponse).isNotNull();
        assertThat(claimResponse.getClaimLineItemList()).isNotEmpty();

        postProcessClaimDataModel(claimResponse, postProcessor);

        Map<String, Object> root = ImmutableMap.of(CLAIM_RESPONSE_KEY, claimResponse);
        File file = processTemplate(root, outputFileName);

        assertThat(file).exists().isFile();
        assertThat(contentOf(file))
                .contains("<bcr:ClaimBatchResponse")
                .contains(claimResponse.getClaimLineItemList().get(0).getAgreementNumber())
                .endsWith("</bcr:ClaimBatchResponse>");
        return file;
    }

    private Optional<Template> getTemplateByName(String name) {
        return configurations.stream()
                .map(configurationSupplier -> getTemplate(configurationSupplier.get(), name))
                .filter(Objects::nonNull)
                .findFirst();
    }

    private Template getTemplate(Configuration cfg, String name) {
        Template template = null;
        try {
            template = cfg.getTemplate(name);
        } catch (IOException e) {
            log.warn("Can't find template {} in cfg {}", name, cfg.getTemplateLoader());
            log.warn(e.getMessage(), e);
        }
        return template;
    }

    private File processTemplate(@Nonnull Map dataModel, @Nonnull String outputFileName) {
        return getTemplateByName(CAS_CLAIM_TEMPLATE)
                .map(template -> processTemplateFile(dataModel, outputFileName, template))
                .orElseThrow(() -> new IstfException("Can't find template by name " + CAS_CLAIM_TEMPLATE));
    }

    private File processTemplateFile(@Nonnull Map dataModel, @Nonnull String outputFileName, Template template) {
        try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(outputFileName))) {
            template.process(dataModel, fileWriter);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
            throw new IstfException(e.getMessage(), e);
        }
        return new File(outputFileName);
    }

    private CASClaimResponse getClaimResponseDataModel() {
        Yaml yaml = new Yaml(new Constructor(CASClaimResponse.class));
        InputStream inputStream = BatchClaimHelper.class
                .getClassLoader()
                .getResourceAsStream(CLAIM_DATA_MODELS_PATH + File.separator + dataModelFileName);
        return (CASClaimResponse) yaml.load(inputStream);
    }

    private void postProcessClaimDataModel(CASClaimResponse response, Consumer<CASClaimResponse> consumer) {
        consumer.accept(response);
    }
}
