<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Postman OpenAPI Drift Companion Changelog

## [Unreleased]

## [0.1.2]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.1]

### Fixed

- **Warning anchored on the wrong line** (found during manual testing
  2026-08-25, present since 0.1.0): the inspection anchored on the
  outer `"url"` JSON property instead of the `"raw"` string literal
  (or the `"path"` array when there's no `raw`) that actually contains
  the stale URL text -- the underline showed up on the `"url": {` line
  above the text a developer needed to look at, instead of on the URL
  itself. Fixed by having the parser hand back the most specific PSI
  element (the `raw` literal when present) instead of the generic
  `url` property.

## [0.1.0]

### Added

- Warning on a Postman collection endpoint whose method+path no
  longer exists in the project's nearby OpenAPI/Swagger spec.
- 100% static analysis (JSON PSI for the collection, plain text scan
  for the spec), no network calls, no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/postman-openapi-drift-companion/compare/0.1.2...HEAD
[0.1.2]: https://github.com/GapHunterLabs/postman-openapi-drift-companion/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/GapHunterLabs/postman-openapi-drift-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/postman-openapi-drift-companion/commits/0.1.0
