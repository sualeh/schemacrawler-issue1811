package com.example;

import java.sql.Connection;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.config.LinterConfigUtility;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

public class Main {

  public static void main(final String[] args) throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseConnectionSource dataSource = getDataSource();
    final Catalog catalog =
      SchemaCrawlerUtility.  getCatalog(dataSource,  schemaCrawlerOptions);

    final LintOptions lintOptions =
        LintOptionsBuilder.builder().withLinterConfigs("/issue1811-linter-configs.yaml").toOptions();

    final LinterConfigs linterConfigs = LinterConfigUtility.readLinterConfigs(lintOptions);

    final Linters linters = new Linters(linterConfigs, false);

    try (final Connection connection = dataSource.get(); ) {
      linters.lint(catalog, connection);
      final Lints lints = linters.getLints();
      System.out.println(lints);
      for (final Lint<?> lint : lints) {
        System.out.println(lint);
      }
    }
  }

  private static DatabaseConnectionSource getDataSource() {
    return DatabaseConnectionSources.newDatabaseConnectionSource(
        "jdbc:sqlite::memory:"
        + "",
        new MultiUseUserCredentials("", ""));
  }
}
