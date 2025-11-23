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
import org.junit.jupiter.api.parallel.Isolated;

import com.diffplug.spotless.tag.CliProcessTest;
import com.diffplug.spotless.tag.SeparateJvmTest;

@Isolated
@SeparateJvmTest
@CliProcessTest
public class EclipseWtpHtmlTest extends EclipseWtpTestBase {

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
}
