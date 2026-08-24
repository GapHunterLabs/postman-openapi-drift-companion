# Demo data — Postman OpenAPI Drift Companion

For capturing the real Marketplace screenshot:

1. `./gradlew runIde`
2. Open the `demo/` folder as a project (or copy both `openapi.yaml`
   and `acmecorp-orders.postman_collection.json` into any sandbox
   project, same directory) inside the sandbox IDE.
3. Open `acmecorp-orders.postman_collection.json`. The `"Get legacy
   order status"` request's URL shows a warning — `GET
   /orders/{id}/legacy-status` isn't declared in `openapi.yaml`. The
   `"List orders"` request stays clean, for contrast.
4. Enter Full Screen (`View > Appearance > Enter Full Screen`), capture
   with `Win+Shift+S`, save directly to `docs/screenshots/` in this
   repo.
