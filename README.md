# Postman OpenAPI Drift Companion

Warning on a Postman collection (`*.postman_collection.json`) endpoint
whose method+path no longer exists in the project's OpenAPI/Swagger
spec. Collections and specs drift apart silently: a collection is
hand-edited or an endpoint gets renamed/removed in the spec, and
nothing tells the team the collection now exercises a path that
doesn't exist — usually caught only when a request in the collection
starts returning 404.

## Why it exists

A team maintains both an OpenAPI spec (source of truth) and a Postman
collection (used for manual testing/demos). The spec evolves, an
endpoint gets renamed from `/users/{id}` to `/accounts/{id}`, and the
collection's "Get user" request keeps pointing at the old path —
silently, until someone runs it.

## Why built this way

- **100% static analysis** — reads the Postman collection's real
  `item[].request.url` via the bundled JSON plugin's PSI (Postman
  Collection Format v2.1.0's own schema), and the OpenAPI/Swagger
  spec's `paths:` via a plain text scan (works for either YAML or JSON
  specs, no YAML plugin dependency). Path placeholders (`{id}`, `:id`,
  `{{id}}`) are matched as wildcards on both sides.

## v0.1 scope — stated honestly, not exhaustively

Looks for a conventionally-named spec file (`openapi.yaml/.yml/.json`,
`swagger.yaml/.yml/.json`) walking up from the collection's own
directory — a spec filed elsewhere in the project, or referenced only
via `$ref`, isn't found, and the inspection stays silent rather than
guessing (no spec found means "can't evaluate", never a false
positive).

## Usage

Open a `*.postman_collection.json` file in a project that also has an
OpenAPI/Swagger spec nearby. An endpoint whose method+path isn't
declared in the spec shows a warning.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
