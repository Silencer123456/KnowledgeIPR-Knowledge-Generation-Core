package kiv.zcu.knowledgeipr.core.model.report;

public interface IReportSavable {

    // TODO: Refactor to not use the second parameter
    boolean save(IReport object, String name);
}
