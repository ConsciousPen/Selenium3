package aaa.admin.modules.reports.templates;

public enum TemplateType {

    TEMPLATE("Template", new Template());

    private String templateType;
    private ITemplate template;

    TemplateType(String templateType, ITemplate template) {
        this.templateType = templateType;
        this.template = template;
    }

    public ITemplate get() {
        return template;
    }

    public String getName() {
        return templateType;
    }

    public String getKey() {
        return templateType.getClass().getSimpleName();
    }

}
