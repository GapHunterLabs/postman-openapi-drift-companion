package dev.gaphunter.postmanopenapidriftcompanion.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.gaphunter.postmanopenapidriftcompanion.parse.OpenApiPathScanner
import dev.gaphunter.postmanopenapidriftcompanion.parse.OpenApiSpecLocator
import dev.gaphunter.postmanopenapidriftcompanion.parse.PathMatcher
import dev.gaphunter.postmanopenapidriftcompanion.parse.PostmanCollectionParser
import dev.gaphunter.postmanopenapidriftcompanion.review.ReviewPrompt

/**
 * Flags a Postman collection endpoint whose method+path no longer
 * exists in the project's OpenAPI/Swagger spec -- collections and
 * specs drift apart silently: a collection is hand-edited or an
 * endpoint gets renamed/removed in the spec, and nothing tells the
 * team the collection now exercises a path that doesn't exist,
 * usually caught only when a request in the collection starts
 * returning 404.
 */
class PostmanEndpointDriftInspection : LocalInspectionTool() {

    companion object {
        private val COLLECTION_FILE_NAME = Regex(""".*\.postman_collection\.json$""", RegexOption.IGNORE_CASE)
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val jsonFile = file as? JsonFile ?: return null
        val virtualFile = file.virtualFile ?: return null
        if (!COLLECTION_FILE_NAME.matches(virtualFile.name)) return null

        val endpoints = PostmanCollectionParser.findEndpoints(jsonFile)
        if (endpoints.isEmpty()) return null

        val specText = OpenApiSpecLocator.findSpecText(virtualFile) ?: return null
        val specPaths = OpenApiPathScanner.scan(specText)
        if (specPaths.isEmpty()) return null

        val problems = mutableListOf<ProblemDescriptor>()
        for (endpoint in endpoints) {
            if (PathMatcher.existsInSpec(endpoint.pathSegments, specPaths)) continue

            val anchor = leafOf(endpoint.urlProperty) ?: continue
            val displayPath = "/" + endpoint.pathSegments.joinToString("/")
            problems += manager.createProblemDescriptor(
                anchor,
                TextRange(0, anchor.textLength),
                "${endpoint.method} $displayPath isn't declared in the project's OpenAPI spec -- this collection endpoint may be stale",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                isOnTheFly,
            )

            val lineNumber = jsonFile.viewProvider.document?.getLineNumber(anchor.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "${virtualFile.path}:$lineNumber")
        }

        return if (problems.isEmpty()) null else problems.toTypedArray()
    }

    private fun leafOf(element: PsiElement): PsiElement? {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
