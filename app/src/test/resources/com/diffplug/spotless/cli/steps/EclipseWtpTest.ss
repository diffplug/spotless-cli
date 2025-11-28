╔═ cssItUsesConfigurationFile ═╗
body {
 a: v;
 b: v;
}
╔═ htmlItUsesConfigurationFile ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ itInfersCssFileTypeFromFileExtension ═╗
body {
	a: v;
	b: v;
}
╔═ itInfersHtmlFileTypeFromFileExtension ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ itInfersHtmlFileTypeFromShortFileExtension ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ itInfersJsFileTypeFromFileExtension ═╗
function f() {
    a.b(1, 2);
}
╔═ itInfersJsonFileTypeFromFileExtension ═╗
{
	"a": "b",
	"c": {
		"d": "e",
		"f": "g"
	}
}
╔═ itInfersXhtmlFileTypeFromFileExtension ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ itInfersXmlFileTypeFromFileExtension ═╗
<a>
	<b> c</b>
</a>
╔═ itSupportsFormattingCssFileType ═╗
body {
	a: v;
	b: v;
}
╔═ itSupportsFormattingHtmlFileType ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ itSupportsFormattingJsFileType ═╗
function f() {
    a.b(1, 2);
}
╔═ itSupportsFormattingJsonFileType ═╗
{
	"a": "b",
	"c": {
		"d": "e",
		"f": "g"
	}
}
╔═ itSupportsFormattingXhtmlFileType ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ itSupportsFormattingXmlFileType ═╗
<a>
	<b> c</b>
</a>
╔═ jsItUsesConfigurationFile ═╗
function f() {
                a.b(1, 2);
}
╔═ xhtmlItUsesConfigurationFile ═╗
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
</html>

╔═ [end of file] ═╗
