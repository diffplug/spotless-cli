/*
 * Copyright 2025 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.spotless.cli.steps;

import java.util.stream.Stream;

import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.diffplug.spotless.cli.CLIIntegrationHarness;
import com.diffplug.spotless.cli.SpotlessCLIRunner;
import com.diffplug.spotless.tag.CliNativeTest;
import com.diffplug.spotless.tag.CliProcessTest;
import com.diffplug.spotless.tag.SeparateJvmTest;

@Isolated
@SeparateJvmTest
@CliNativeTest
@CliProcessTest
public class EclipseWtpSecondTest extends CLIIntegrationHarness {

    //    @BeforeEach
    //    void resetEclipseFramework() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    //        Class<?> frameworkClass =
    // Class.forName("com.diffplug.spotless.extra.eclipse.base.SpotlessEclipseFramework");
    //        Field instanceField = frameworkClass.getDeclaredField("INSTANCE");
    //        instanceField.setAccessible(true);
    //        instanceField.set(null, null);
    //
    //        Class<?> bundleRegistryClass =
    // Class.forName("com.diffplug.spotless.extra.eclipse.base.osgi.FrameworkBundleRegistry");
    //        Field registryInstanceField = bundleRegistryClass.getDeclaredField("INSTANCE");
    //        registryInstanceField.setAccessible(true);
    //        registryInstanceField.set(null, null);
    //
    //        Class<?> registryProviderFactoryClass =
    // Class.forName("org.eclipse.core.internal.registry.RegistryProviderFactory");
    //        Field registryProviderField = registryProviderFactoryClass.getDeclaredField("defaultRegistryProvider");
    //        registryProviderField.setAccessible(true);
    //        registryProviderField.set(null, null);
    //    }

    @ParameterizedTest(name = "itSupportsFormattingFileType {0}")
    @MethodSource
    void itSupportsFormattingFileType(EclipseWtp.Type type, String unformatted) {
        String fileName = "test." + type.name().toLowerCase();
        setFile(fileName).toContent(unformatted);

        SpotlessCLIRunner.Result result = cliRunner()
                .withTargets(fileName)
                .withStep(EclipseWtp.class)
                .withOption("--type", type.name())
                .run();

        selfie().expectResource(fileName).toMatchDisk(type.name());
    }

    //    @Test
    //    void itSupportsFormattingXmlFileType() {
    //        String unformatted = "<a><b>   c</b></a>";
    //        String fileName = "test.xml";
    //        setFile(fileName).toContent(unformatted);
    //
    //        SpotlessCLIRunner.Result result = cliRunner()
    //                .withTargets(fileName)
    //                .withStep(EclipseWtp.class)
    //                .withOption("--type", "XML")
    //                .run();
    //
    //        selfie().expectResource(fileName).toBe_TODO();
    //    }

    static Stream<Arguments> itSupportsFormattingFileType() {
        return Stream.of(
                //                Arguments.of(EclipseWtp.Type.CSS, "body {\n" + "a: v;   b:   \n" + "v;\n" + "}  \n"),
                Arguments.of(
                        EclipseWtp.Type.HTML,
                        "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  "));
        //                Arguments.of(EclipseWtp.Type.JSON, "{\"a\": \"b\",\t\"c\":   { \"d\": \"e\",\"f\": \"g\"}}"),
        //                Arguments.of(EclipseWtp.Type.JS, "function f(  )   {\n" + "a.b(1,\n" + "2);}"),
        //                Arguments.of(EclipseWtp.Type.XML, "<a><b>   c</b></a>"),
        //                Arguments.of(
        //                        EclipseWtp.Type.XHTML,
        //                        "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  "));
    }
}
