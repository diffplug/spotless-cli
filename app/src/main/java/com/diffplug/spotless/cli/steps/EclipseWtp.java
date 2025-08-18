package com.diffplug.spotless.cli.steps;


import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.ThrowingEx;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.help.OptionConstants;
import com.diffplug.spotless.extra.EclipseBasedStepBuilder;
import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

@CommandLine.Command(name = "eclipse-wtp", description = "Runs Eclipse WTP formatter (" + EclipseWtp.ECLIPSE_WTP_VERSION+")")
public class EclipseWtp extends SpotlessFormatterStep {

    public static final String ECLIPSE_WTP_VERSION = "4.21.0"; // TODO we need to slurp in the lock file also

    @CommandLine.Option(
            names = {"-f", "--config-file"},
            arity = "0",
            description = "The path to the Eclipse WTP configuration file.\n" +
                    "For supported config file options see <https://github.com/diffplug/spotless/tree/main/plugin-gradle#eclipse-web-tools-platform>"
    )
    List<Path> configFiles;

    @CommandLine.Option(
            names = { "-t", "--type" },
            description = "The type of the Eclipse WTP formatter." + OptionConstants.VALID_VALUES_SUFFIX,
            required = true
    )
    Type type;

    public enum Type {
        CSS,
        HTML,
        JS,
        JSON,
        XML,
        XHTML;

        public void initCorrespondingEclipseWtpCorePlugin() {
            // due to internals of eclipse wtp, invoking various variants of the formatter in the same java process
            // requires some static initialization to take place

        }

        public @NotNull EclipseWtpFormatterStep toEclipseWtpType() {
            EclipseWtpFormatterStep step = switch (this) {
                case CSS -> EclipseWtpFormatterStep.CSS;
                case HTML, XHTML -> EclipseWtpFormatterStep.HTML; // XHTML is treated as HTML in Eclipse WTP
                case JS -> EclipseWtpFormatterStep.JS;
                case JSON -> EclipseWtpFormatterStep.JSON;
                case XML -> EclipseWtpFormatterStep.XML;
            };

            @SuppressWarnings("unused") Class<?> reverseLookup = lookupClass(step);
            return step;
        }

        static Class<?> lookupClass(EclipseWtpFormatterStep step) {
                        /* this statement is only here to
               1) get compile errors in case a new type is added in EclipseWtpFormatterStep
               2) allow graal to collect reflective Metadata*/
            @SuppressWarnings("unused") Class<?> reverseLookup = switch(step) {
                case CSS -> EclipseWtpMetadata.cssClass();
                case HTML -> EclipseWtpMetadata.htmlClass();
                case JS -> EclipseWtpMetadata.jsClass();
                case JSON -> EclipseWtpMetadata.jsonClass();
                case XML -> EclipseWtpMetadata.xmlClass();
            };
            return reverseLookup;
        }
    }


    @Override
    public @NotNull List<FormatterStep> prepareFormatterSteps(SpotlessActionContext context) {
        EclipseWtpFormatterStep wtpType = type.toEclipseWtpType();
        EclipseBasedStepBuilder builder = wtpType.createBuilder(context.provisioner());
        builder.setVersion(ECLIPSE_WTP_VERSION);
        if (configFiles != null && !configFiles.isEmpty()) {
            builder.setPreferences(
                    configFiles.stream()
                            .map(context::resolvePath)
                            .map(Path::toFile)
                            .toList());
        }
        return List.of(builder.build());
    }


    static class EclipseWtpMetadata {

        private static Class<?> cssClass() {
            return ThrowingEx.get(() -> Class.forName("com.diffplug.spotless.extra.eclipse.wtp.EclipseCssFormatterStepImpl"));
        }

        private static Class<?> htmlClass() {
            return ThrowingEx.get(() -> {
                Class<?> result = Class.forName("com.diffplug.spotless.extra.eclipse.wtp.EclipseHtmlFormatterStepImpl");
                Constructor<?> declaredConstructor = result.getDeclaredConstructor(Properties.class);
                return result;
            });
        }

        private static Class<?> jsClass() {
            return ThrowingEx.get(() -> Class.forName("com.diffplug.spotless.extra.eclipse.wtp.EclipseJsFormatterStepImpl"));
        }

        private static Class<?> jsonClass() {
            return ThrowingEx.get(() -> Class.forName("com.diffplug.spotless.extra.eclipse.wtp.EclipseJsonFormatterStepImpl"));
        }

        private static Class<?> xmlClass() {
            return ThrowingEx.get(() -> Class.forName("com.diffplug.spotless.extra.eclipse.wtp.EclipseXmlFormatterStepImpl"));
        }
    }
}
