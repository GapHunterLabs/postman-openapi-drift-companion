package dev.gaphunter.postmanopenapidriftcompanion.parse

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import dev.gaphunter.postmanopenapidriftcompanion.model.PostmanEndpoint

/**
 * Reads every `item[].request` from an already-parsed Postman
 * collection ([JsonFile]) via the bundled JSON plugin's real PSI --
 * same "don't reinvent a parser for a format the platform already
 * parses correctly" principle proven in `unused-npm-script-companion`.
 * `item` arrays nest (folders contain items), so this walks
 * recursively. Matches the real Postman Collection Format v2.1.0
 * schema (schema.postman.com): `request.url.path` is the segment
 * array form; `request.url.raw` is the fallback string form for a
 * request whose URL was never split into `path`.
 */
object PostmanCollectionParser {

    fun findEndpoints(file: JsonFile): List<PostmanEndpoint> {
        val root = file.topLevelValue as? JsonObject ?: return emptyList()
        val items = root.findProperty("item")?.value as? JsonArray ?: return emptyList()
        val endpoints = mutableListOf<PostmanEndpoint>()
        collectFromItems(items, endpoints)
        return endpoints
    }

    private fun collectFromItems(items: JsonArray, out: MutableList<PostmanEndpoint>) {
        for (element in items.valueList) {
            val itemObject = element as? JsonObject ?: continue

            val nestedItems = itemObject.findProperty("item")?.value as? JsonArray
            if (nestedItems != null) {
                collectFromItems(nestedItems, out)
                continue
            }

            val requestObject = itemObject.findProperty("request")?.value as? JsonObject ?: continue
            val method = (requestObject.findProperty("method")?.value as? JsonStringLiteral)?.value ?: "GET"
            val urlProperty = requestObject.findProperty("url") ?: continue
            val urlValue = urlProperty.value

            val segments = when (urlValue) {
                is JsonObject -> {
                    val pathArray = urlValue.findProperty("path")?.value as? JsonArray
                    if (pathArray != null) {
                        pathArray.valueList.mapNotNull { (it as? JsonStringLiteral)?.value }
                    } else {
                        val rawLiteral = urlValue.findProperty("raw")?.value as? JsonStringLiteral
                        segmentsFromRaw(rawLiteral?.value)
                    }
                }
                is JsonStringLiteral -> segmentsFromRaw(urlValue.value)
                else -> emptyList()
            }
            if (segments.isEmpty()) continue

            out += PostmanEndpoint(method.uppercase(), segments, urlProperty)
        }
    }

    private fun segmentsFromRaw(raw: String?): List<String> {
        if (raw == null) return emptyList()
        val withoutQuery = raw.substringBefore("?")
        val withoutProtocolHost = Regex("""^\{\{[^}]*}}""").replace(withoutQuery, "")
            .removePrefix("http://").removePrefix("https://")
            .substringAfter("/", missingDelimiterValue = "")
        return withoutProtocolHost.split("/").filter { it.isNotBlank() }
    }
}
