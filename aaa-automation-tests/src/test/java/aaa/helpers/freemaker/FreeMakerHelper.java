package aaa.helpers.freemaker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class FreeMakerHelper {

    private static final Logger log = LoggerFactory.getLogger(FreeMakerHelper.class);

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLAIM_TEMPLATES_PATH = "/claimsmatch/cas_claim_templates";

    private static final String CAS_CLAIM_TEMPLATE = "cas_claim_response.ftl";

    private final static List<Supplier<Configuration>> configurations = new ArrayList<>();

    static {
        configurations.add(() -> {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(FreeMakerHelper.class, CLAIM_TEMPLATES_PATH);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            return cfg;
        });
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

    private File processTemplate(@Nonnull String templateName, @Nonnull Map dataModel, @Nonnull String outputFileName) {
        return getTemplateByName(templateName)
                .map(template -> getTemplateFile(dataModel, outputFileName, template))
                .orElseThrow(() -> new IstfException("Can't find template by name " + templateName));
    }

    private File getTemplateFile(@Nonnull Map dataModel, @Nonnull String outputFileName, Template template) {
        try {
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(outputFileName));
            template.process(dataModel, fileWriter);
        } catch (IOException | TemplateException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new IstfException(e.getMessage(), e);
        }
        return new File(outputFileName);
    }

    public File processClaimTemplate(@Nonnull Map dataModel, @Nonnull String outputFileName) {
        return processTemplate(CAS_CLAIM_TEMPLATE, dataModel, outputFileName);
    }
}
