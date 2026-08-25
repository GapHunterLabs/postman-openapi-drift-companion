package dev.gaphunter.postmanopenapidriftcompanion.model

import com.intellij.psi.PsiElement

/**
 * One `item[].request` entry from a Postman collection.
 *
 * [anchorElement] is the most specific PSI element actually showing the
 * URL text -- the `"raw"` string literal when present, the `"path"`
 * array when there's no `raw`, or the plain string literal when `url`
 * itself is a bare string -- never the outer `"url"` property itself,
 * which would anchor the warning on the `"url": {` key instead of on
 * the URL text a developer needs to look at (found live via manual
 * testing: the warning appeared one line above where it should).
 */
data class PostmanEndpoint(
    val method: String,
    val pathSegments: List<String>,
    val anchorElement: PsiElement,
)
