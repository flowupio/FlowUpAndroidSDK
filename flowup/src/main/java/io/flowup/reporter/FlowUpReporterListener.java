package io.flowup.reporter;

public interface FlowUpReporterListener {

  void onReport(DropwizardReport report);

}
