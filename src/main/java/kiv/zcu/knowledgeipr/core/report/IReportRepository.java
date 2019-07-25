package kiv.zcu.knowledgeipr.core.report;

public interface IReportRepository {

    // TODO: Refactor to not use the second parameter
    boolean save(IReport object, String name);
}
