package kiv.zcu.knowledgeipr.core.model.report;

import java.util.ArrayList;

/**
 * Serves as an empty report - Used if error occurred for example
 */
public class EmptySearchReport extends SearchReport<String> {

    public EmptySearchReport() {
        super(new ArrayList<>());
    }
}