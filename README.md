# PayPal Subscription API — Test Automation Framework

A REST API test automation framework for the **PayPal Subscriptions API** (Sandbox), built with Java and Rest Assured. The framework automates the subscription lifecycle (create, retrieve, suspend, cancel) along with negative, authentication, and security scenarios, with each automated test traceable back to a manual test case.

> Status: **work in progress.** Core auth + create/retrieve flows are automated; lifecycle and security suites, the token manager, POJO models, reusable assertions, and CI/CD are in progress.

---

## Tech Stack

| Concern | Tool |
|---|---|
| Language | Java 21 (LTS) |
| Build | Maven |
| HTTP / API testing | Rest Assured |
| Test runner | TestNG |
| Assertions | Hamcrest + TestNG |
| JSON | Gson |
| Configuration | `.env` via java-dotenv |
| Logging | Log4j2 |
| Reporting | Allure |
| Misc | Apache Commons IO |

---

## Architecture

The framework follows a **layered design** so that tests express business intent and contain no HTTP detail. Each layer only depends on the one below it:

```
Test            asserts business outcomes (status, contract, body)
  ↓
Service         one method per API operation (create / retrieve / ...)
  ↓
AuthClient      OAuth2 token acquisition (client-credentials)
  ↓
Rest Assured    the HTTP call
```

Supporting concerns sit alongside: configuration (`.env`), test data (JSON templates + in-memory injection), logging, and reporting.

Key design decisions:

- **Production code in `src/main`, tests in `src/test`.** Test annotations never leak into framework code.
- **Services own the HTTP, tests own the assertions.** Reusable clients return the `Response`; tests judge it.
- **Parameterized requests over duplication.** Auth is passed in (valid / invalid / absent), so one method covers positive and negative cases without copy-paste.
- **Test data is injected in memory**, never written back to fixture files — keeping runs idempotent and parallel-safe.
- **No DI framework** — kept intentionally lightweight for a single-module project.

---

## Project Structure

```
src/
├── main/java/com/paypal/subscription/
│   ├── auth/         AuthClient — OAuth2 token handling
│   ├── config/       Env — typed access to .env values
│   ├── services/     Subscription — API operations (product/plan/subscription)
│   └── utils/        LogsUtils, DataUtils, AllureUtils
├── main/resources/
│   ├── log4j2.properties
│   └── allure.properties
└── test/
    ├── java/com/paypal/subscription/tests/
    │   ├── AuthTest.java
    │   └── SubscriptionTest.java
    └── resources/testdata/
        ├── create-product.json
        ├── create-plan.json
        └── create-subscription.json
```

---

## Configuration

Credentials and environment values are read from a local `.env` file (gitignored — never committed). Create a `.env` in the project root:

```env
BASE_URL=https://api-m.sandbox.paypal.com/v1/
CLIENT_ID=your_sandbox_client_id
CLIENT_SECRET=your_sandbox_client_secret
ACTIVE_SUBSCRIPTION_ID=I-XXXXXXXXXXXX   # a pre-approved sandbox subscription, for retrieve/lifecycle tests
```

Get sandbox credentials by creating a REST API app at [developer.paypal.com](https://developer.paypal.com).

In CI, the same keys are supplied as environment variables / secrets instead of a file.

---

## Running the Tests

```bash
# run the full suite
mvn test

# generate and open the Allure report from the results
allure serve test-outputs/allure-results
```

Logs are written to `test-outputs/Logs/` and to the console.

---

## Test Coverage & Traceability

Each automated test maps back to a manual test case (`TC_PAYPAL_SUB_*`) for full traceability.

| TC ID | Scenario | Type | Automated |
|---|---|---|:---:|
| TC_001 | Create subscription with valid plan | Positive | ✅ |
| TC_002 | Suspend active subscription | Positive | ⬜ planned |
| TC_003 | Cancel suspended subscription | Positive | ⬜ planned |
| TC_004 | Create rejected — invalid access token | Negative | ✅ |
| TC_005 | Create rejected — non-existent plan_id | Negative | ✅ |
| TC_006 | Cancel an already-cancelled subscription (422) | Negative | ⬜ planned |
| TC_007 | Create rejected — missing authorization header | Negative | ✅ |
| TC_008 | Create rejected — missing plan_id | Negative | ✅ |
| TC_009 | Create rejected — expired access token | Negative | 📝 manual* |
| TC_010 | Retrieve subscription by valid id | Positive | ✅ |
| TC_011 | SQL injection payload in address_line_1 | Security | ⬜ planned |
| TC_012 | XSS payload in address_line_1 | Security | ⬜ planned |
| TC_013 | Retrieve subscription with invalid id | Negative | ✅ |

**Automated: 7 / 13.** Plus 2 foundational OAuth token-endpoint tests in `AuthTest`.

\* *TC_009 is documented as a manual case rather than automated: token expiry is non-deterministic (~9h TTL, no force-expire), and an expired token returns the same `401 / invalid_token` contract already covered by TC_004.*

### Notes on automatability

- **Lifecycle tests (suspend/cancel)** require an `ACTIVE` subscription. API-created subscriptions are `APPROVAL_PENDING` and can only be activated through the buyer's browser approval flow, so these tests run against a pre-approved subscription supplied via `ACTIVE_SUBSCRIPTION_ID`.

---

## Reporting & Logging

- **Allure** — rich HTML report; results written to `test-outputs/allure-results`.
- **Log4j2** — structured request/response and failure logging to console and rolling files under `test-outputs/Logs/`.

---

## Roadmap

- [ ] Token manager: single shared instance with caching + expiry handling
- [ ] POJO models (replace raw `JsonObject` payloads) with builders + Faker
- [ ] Lifecycle suite (suspend / cancel / cancel-already-cancelled)
- [ ] Security suite (SQL injection / XSS)
- [ ] Reusable assertion layer (TestNG `SoftAssert`)
- [ ] Allure attachments (request/response on failure) via TestNG listener
- [ ] TestNG suite XMLs (smoke / regression / security) + parallel execution
- [ ] CI/CD with GitHub Actions + Allure report publishing

---

## Author

**Mahmoud Elsaqqa** — QA Automation
