package kiv.zcu.knowledgeipr.core.dbaccess;

import kiv.zcu.knowledgeipr.core.report.IReport;

public interface IReportRepository {

    // TODO: Refactor to not use the second parameter
    boolean save(IReport object, String name);
}
