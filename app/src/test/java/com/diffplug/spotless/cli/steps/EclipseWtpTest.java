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

import org.junit.jupiter.api.Test;

import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
public class EclipseWtpTest extends EclipseWtpTestBase {

    // XML
    @Test
    void itSupportsFormattingXmlFileType() {
        String fileName = runEclipseWtpWithType(EclipseWtp.Type.XML, "<a><b>   c</b></a>");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersXmlFileTypeFromFileExtension() {
        String fileName = runEclipseWtpWithTypeInferred("xml", "<a><b>   c</b></a>");
        selfie().expectResource(fileName).toMatchDisk();
    }

    // HTML
    @Test
    void itSupportsFormattingHtmlFileType() {
        String fileName = runEclipseWtpWithType(
                EclipseWtp.Type.HTML,
                "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersHtmlFileTypeFromFileExtension() {
        String fileName = runEclipseWtpWithTypeInferred(
                "html", "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersHtmlFileTypeFromShortFileExtension() {
        String fileName = runEclipseWtpWithTypeInferred(
                "htm", "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void htmlItUsesConfigurationFile() {
        String fileName = runEclipseWtpWithTypeAndConfigFile(
                EclipseWtp.Type.HTML,
                "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ",
                "org.eclipse.wst.html.core.prefs");
        selfie().expectResource(fileName).toMatchDisk();
    }

    // CSS
    @Test
    void itSupportsFormattingCssFileType() {
        String fileName = runEclipseWtpWithType(EclipseWtp.Type.CSS, "body {\n" + "a: v;   b:   \n" + "v;\n" + "}  \n");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersCssFileTypeFromFileExtension() {
        String fileName = runEclipseWtpWithTypeInferred("css", "body {\n" + "a: v;   b:   \n" + "v;\n" + "}  \n");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void cssItUsesConfigurationFile() {
        String fileName = runEclipseWtpWithTypeAndConfigFile(
                EclipseWtp.Type.CSS,
                "body {\n" + "a: v;   b:   \n" + "v;\n" + "}  \n",
                "org.eclipse.wst.css.core.prefs");
        selfie().expectResource(fileName).toMatchDisk();
    }

    // JS
    @Test
    void itSupportsFormattingJsFileType() {
        String fileName = runEclipseWtpWithType(EclipseWtp.Type.JS, "function f(  )   {\n" + "a.b(1,\n" + "2);}");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersJsFileTypeFromFileExtension() {
        String fileName = runEclipseWtpWithTypeInferred("js", "function f(  )   {\n" + "a.b(1,\n" + "2);}");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void jsItUsesConfigurationFile() {
        String fileName = runEclipseWtpWithTypeAndConfigFile(
                EclipseWtp.Type.JS, "function f(  )   {\n" + "a.b(1,\n" + "2);}", "eclipse-wtp-js-profile.xml");
        selfie().expectResource(fileName).toMatchDisk();
    }

    // XHTML
    @Test
    void itSupportsFormattingXhtmlFileType() {
        String fileName = runEclipseWtpWithType(
                EclipseWtp.Type.XHTML,
                "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersXhtmlFileTypeFromFileExtension() {
        String fileName = runEclipseWtpWithTypeInferred(
                "xhtml", "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void xhtmlItUsesConfigurationFile() {
        String fileName = runEclipseWtpWithTypeAndConfigFile(
                EclipseWtp.Type.XHTML,
                "<!DOCTYPE html> <html>\t<head> <meta   charset=\"UTF-8\"></head>\n" + "</html>  ",
                "org.eclipse.wst.html.core.prefs");
        selfie().expectResource(fileName).toMatchDisk();
    }

    // JSON
    @Test
    void itSupportsFormattingJsonFileType() {
        String fileName =
                runEclipseWtpWithType(EclipseWtp.Type.JSON, "{\"a\": \"b\",\t\"c\":   { \"d\": \"e\",\"f\": \"g\"}}");
        selfie().expectResource(fileName).toMatchDisk();
    }

    @Test
    void itInfersJsonFileTypeFromFileExtension() {
        String fileName =
                runEclipseWtpWithTypeInferred("json", "{\"a\": \"b\",\t\"c\":   { \"d\": \"e\",\"f\": \"g\"}}");
        selfie().expectResource(fileName).toMatchDisk();
    }
}
