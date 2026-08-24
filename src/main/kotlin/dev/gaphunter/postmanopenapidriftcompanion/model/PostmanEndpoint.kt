package dev.gaphunter.postmanopenapidriftcompanion.model

import com.intellij.json.psi.JsonProperty

/** One `item[].request` entry from a Postman collection, with its raw URL literal PSI for anchoring. */
data class PostmanEndpoint(
    val method: String,
    val pathSegments: List<String>,
    val urlProperty: JsonProperty,
)
