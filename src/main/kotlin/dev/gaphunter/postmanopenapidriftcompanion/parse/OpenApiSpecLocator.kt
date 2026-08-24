package dev.gaphunter.postmanopenapidriftcompanion.parse

import com.intellij.openapi.vfs.VirtualFile

/**
 * Finds an OpenAPI/Swagger spec file near a Postman collection --
 * walks up from the collection's own directory to the content root
 * (bounded, never a full-disk/project-wide index scan), checking each
 * directory for a conventionally-named spec file. v0.1 only looks at
 * directories on the path from the collection to the root, not the
 * whole project tree -- a spec filed under an unrelated sibling
 * directory isn't found, an honest scope limit documented in the
 * README.
 */
object OpenApiSpecLocator {

    private val SPEC_NAMES = listOf(
        "openapi.yaml", "openapi.yml", "openapi.json",
        "swagger.yaml", "swagger.yml", "swagger.json",
    )
    private const val MAX_DIRS_UP = 6

    fun findSpecText(collectionFile: VirtualFile): String? {
        var dir = collectionFile.parent
        var hops = 0
        while (dir != null && hops < MAX_DIRS_UP) {
            for (name in SPEC_NAMES) {
                val candidate = dir.findChild(name)
                if (candidate != null && !candidate.isDirectory) {
                    return runCatching { String(candidate.contentsToByteArray(), Charsets.UTF_8) }.getOrNull()
                }
            }
            dir = dir.parent
            hops++
        }
        return null
    }
}
